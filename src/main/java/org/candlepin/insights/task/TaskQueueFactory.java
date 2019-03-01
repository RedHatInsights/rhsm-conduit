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
package org.candlepin.insights.task;

import org.candlepin.insights.task.kafka.KafkaTaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

public class TaskQueueFactory implements FactoryBean<TaskQueue> {

    private static Logger log = LoggerFactory.getLogger(TaskQueueFactory.class);

    @Override
    public TaskQueue getObject() throws Exception {
        // TODO The type of queue and what groups to use should be configurable.
        log.info("Using a KafkaTaskQueue...");
        KafkaTaskQueue queue = new KafkaTaskQueue();
        queue.registerProcessors(TaskQueue.TASK_GROUP);
        return queue;
    }

    @Override
    public Class<?> getObjectType() {
        return TaskQueue.class;
    }

}
