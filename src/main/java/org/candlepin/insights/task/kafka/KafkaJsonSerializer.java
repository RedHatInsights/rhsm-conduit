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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;



/**
 * A JSON serialization for serializing Kafka messages to JSON strings.
 * @param <T>
 */
public class KafkaJsonSerializer<T> implements Serializer<T> {

    private ObjectMapper mapper;

    public KafkaJsonSerializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, T data) {
        byte[] retVal = null;
        try {
            retVal = mapper.writeValueAsBytes(data);
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to serialize object.", e);
        }
        return retVal;
    }

    @Override
    public void close() {

    }
}
