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
package org.candlepin.insights.task;

import org.springframework.beans.factory.DisposableBean;

/**
 * A TaskQueue is responsible for storing tasks until they are picked up by a registered processor
 * for processing.
 *
 * @param <P> the type of TaskProcessor that will process the tasks in this queue.
 */
public interface TaskQueue<P extends TaskProcessor> extends DisposableBean {

    String TASK_GROUP = "rhsm-conduit-tasks";

    /**
     * Enqueues a task that is to be processed by the registered processor.
     *
     * @param taskDescriptor a TaskDescriptor describing the task that is to be processed.
     */
    void enqueue(TaskDescriptor taskDescriptor);

    /**
     * Registers a TaskProcessor with this queue.
     * @param processors
     */
    void registerProcessors(P ... processors);
}
