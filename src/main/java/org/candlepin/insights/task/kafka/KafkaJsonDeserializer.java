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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class KafkaJsonDeserializer<T> implements Deserializer<T> {

    private ObjectMapper mapper;

    private Class <T> type;

    public KafkaJsonDeserializer(Class type, ObjectMapper mapper) {
        this.type = type;
        this.mapper = mapper;
    }

    @Override
    public void configure(Map map, boolean b) {

    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        T obj = null;
        try {
            obj = mapper.readValue(bytes, type);
        } catch (Exception e) {
            throw new RuntimeException("Unable to deserialize object.", e);
        }
        return obj;
    }

    @Override
    public void close() {

    }
}
