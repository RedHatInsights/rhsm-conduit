/*
 * Copyright (c) 2009 - 2019 Red Hat, Inc.
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.candlepin.insights.task.kafka;

import org.candlepin.insights.task.TaskDescriptor;
import org.candlepin.insights.task.TaskProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;


/**
 * Processes messages received from a Kafka queue.
 */
public class KafkaTaskProcessor extends TaskProcessor {

    private static Logger log = LoggerFactory.getLogger(KafkaTaskProcessor.class);

    private KafkaConsumer<String, TaskDescriptor> consumer;
    private Thread reader;

    /**
     * Create a processor associated to the defined taskGroup, and create the serializers/deserializers
     * from the specified ObjectMapper.
     *
     * @param taskGroup the task address to subscribe to.
     * @param mapper the object mapper that should be used to do message serialization/deserialization
     */
    public KafkaTaskProcessor(String taskGroup, ObjectMapper mapper) {

        Properties consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", "localhost:9092");
        consumerProperties.put("group.id", "rhsm-task-processor");
        consumerProperties.put("enable.auto.commit", "false");
        consumerProperties.put("max.poll.records", "1");
        consumer = new KafkaConsumer<>(consumerProperties, new StringDeserializer(),
            new KafkaJsonDeserializer<>(TaskDescriptor.class, mapper));
        // TODO Need to determine the difference between this prop and the group.id property.
        consumer.subscribe(Arrays.asList(taskGroup));

        reader = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        ConsumerRecords<String, TaskDescriptor> records =
                            consumer.poll(Duration.ofSeconds(5));
                        if (records.isEmpty()) {
                            log.info("No messages were found...");
                            continue;
                        }
                        records.forEach(record -> {
                            TaskDescriptor descriptor = record.value();
                            log.info("Message Received: {}:{}", record.key(), descriptor.getTaskType());
                            notifyTaskReceived(descriptor);
                        });
                        // Sleep to simulate message processing
                        Thread.sleep(4000);

                        // The task is complete, let Kafka know.
                        consumer.commitSync();
                    }
                }
                catch (InterruptedException ie) {
                    log.info("The message processing thread was interrupted and will stop.");
                }
            }
        });
    }

    @Override
    public void start() {
        // TODO There's a better way to do this. Check the Kafka client examples.
        reader.start();
    }

    @Override
    public void stop() {
        reader.interrupt();
    }
}
