/*
 * Copyright (c) 2019 Red Hat, Inc.
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

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.candlepin.insights.task.TaskProcessor;
import org.candlepin.insights.task.TaskQueue;
import org.candlepin.insights.task.TaskWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class KafkaTaskProcessor extends TaskProcessor {

    private static Logger log = LoggerFactory.getLogger(KafkaTaskProcessor.class);

    private KafkaConsumer<String, String> consumer;
    private Thread reader;

    public KafkaTaskProcessor(String taskGroup) {

        Properties consumerProperties = new Properties();
        consumerProperties.put("bootstrap.servers", "localhost:9092");
        consumerProperties.put("group.id", "rhsm-task-processor");
        consumerProperties.put("enable.auto.commit", "false");
        consumerProperties.put("max.poll.records", "1");
        consumerProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumer = new KafkaConsumer<>(consumerProperties);
        // TODO Need to determine the difference between this prop and the group.id property.
        consumer.subscribe(Arrays.asList(TaskQueue.TASK_GROUP));

        reader = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
                        if (records.isEmpty()) {
                            log.info("No messages were found...");
                            continue;
                        }
                        records.forEach(record -> log.info("Message Received: {}:{}", record.key(), record.value()));
                        // Sleep to simulate message processing
//                        notifyTaskReceived(task);
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
