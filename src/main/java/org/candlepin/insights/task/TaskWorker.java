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

import org.springframework.stereotype.Component;


/**
 * A TaskWorker executes a task when it is notified by a processor that a task was received
 * from a Queue. The task worker should be added as a listener to a TaskQueue's TaskProcessors.
 */
@Component
public class TaskWorker implements TaskListener {

    private TaskFactory taskFactory;

    public TaskWorker() {
        this.taskFactory = new TaskFactory();
    }

    /**
     * When a TaskDescriptor is received, executes the described Task.
     *
     * @param taskDescriptor the task descriptor that was received.
     */
    @Override
    public void onTaskReceived(TaskDescriptor taskDescriptor) {
        Task toExecute = taskFactory.build(taskDescriptor);
        toExecute.execute();
    }
}
