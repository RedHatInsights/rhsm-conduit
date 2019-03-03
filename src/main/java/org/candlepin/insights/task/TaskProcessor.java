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

import java.util.LinkedList;
import java.util.List;


/**
 * A TaskProcessor defines the common behavior amongst all rhsm-conduit task processors.
 * TaskProcessors are expected to notify any registered listeners that a queued task is
 * has been received and is ready to be executed.
 *
 * The TaskWorker should be added as a listener in most cases.
 */
public abstract class TaskProcessor {

    private List<TaskListener> taskListeners;

    public TaskProcessor() {
        taskListeners = new LinkedList<>();
    }

    /**
     * Adds a TaskListener to the processor so that it will be notified when a
     * TaskDescriptor has been received and should be processed.
     *
     * @param listener the listener to be added.
     */
    public void addTaskListener(TaskListener listener) {
        this.taskListeners.add(listener);
    }

    /**
     * Notifies all TaskListeners that a task has been received.
     *
     * @param taskDescriptor the task descriptor to be notified.
     */
    protected void notifyTaskReceived(TaskDescriptor taskDescriptor) {
        this.taskListeners.forEach(listener -> listener.onTaskReceived(taskDescriptor));
    }

    /**
     * Start processing tasks that this processor is responsible for.
     */
    public abstract void start();

    /**
     * Stop processing tasks that this processor is responsible for.
     */
    public abstract void stop();

}
