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

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.candlepin.insights.task.Task;
import org.candlepin.insights.task.TaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class KafkaTaskQueue implements TaskQueue {

    private static Logger log = LoggerFactory.getLogger(KafkaTaskQueue.class);

    private KafkaProducer<String, String> producer;
    private List<KafkaTaskProcessor> processors;

    public KafkaTaskQueue() {
        this.processors = new ArrayList<>();

        // TODO Properly load the config properties.
        Properties producerProperties = new Properties();
        producerProperties.put("bootstrap.servers", "localhost:9092");
        producerProperties.put("acks", "all");
        producerProperties.put("delivery.timeout.ms", 30000);
        producerProperties.put("request.timeout.ms", 20000);
        producerProperties.put("batch.size", 16384);
        producerProperties.put("linger.ms", 1);
        producerProperties.put("buffer.memory", 33554432);
        producerProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producer = new KafkaProducer<>(producerProperties);
    }

    @Override
    public void enqueue(Task task) {
        // TODO Need to create a custom serializer for sending a task, and a deserializer for receiving a task.
        log.debug("Sending task to kafka...");
        producer.send(new ProducerRecord<>(task.getGroupId(), "rhsm-conduit-task", task.getValue("org_id")));
    }

    @Override
    public void registerProcessors(String... taskGroups) {
        for (String group : taskGroups) {
            log.info("Registering Kafka task processor for task group: {}", group);
            KafkaTaskProcessor processor = new KafkaTaskProcessor(group);
            processors.add(processor);
            processor.start();
        }
    }

    @Override
    public void destroy() throws Exception {
        log.info("Stopping all kafka task processors...");
        processors.forEach(KafkaTaskProcessor::stop);
    }
}
