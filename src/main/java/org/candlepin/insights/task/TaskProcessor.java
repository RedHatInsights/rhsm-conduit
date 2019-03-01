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

import java.util.LinkedList;
import java.util.List;

public abstract class TaskProcessor {

    private List<TaskListener> taskListeners;

    public TaskProcessor() {
        taskListeners = new LinkedList<>();
    }

    public void addTaskListener(TaskListener listener) {
        this.taskListeners.add(listener);
    }

    protected void notifyTaskReceived(TaskDescriptor taskDescriptor) {
        this.taskListeners.forEach(listener -> listener.onTaskReceived(taskDescriptor));
    }

    public abstract void start();

    public abstract void stop();

}
