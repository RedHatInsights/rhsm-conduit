/*
 * Copyright (c) 2009 - 2019 Red Hat, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Red Hat trademarks are not licensed under GPLv3. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */
package org.candlepin.insights.task.queue.inmemory;

import org.candlepin.insights.task.TaskDescriptor;
import org.candlepin.insights.task.TaskExecutionException;
import org.candlepin.insights.task.TaskWorker;
import org.candlepin.insights.task.queue.TaskQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * An in-memory TaskQueue implementation that uses a single worker thread to execute tasks.
 *
 * Does not block while executing a task.
 */
public class InMemoryTaskQueue implements TaskQueue {
    private final BlockingQueue<TaskDescriptor> tasks;
    private final Thread workerThread;

    public InMemoryTaskQueue(TaskWorker worker) {
        this.tasks = new LinkedBlockingDeque<>();
        workerThread = new Thread(() -> {
            while (true) {
                try {
                    TaskDescriptor task = tasks.take();
                    try {
                        worker.executeTask(task);
                    }
                    catch (TaskExecutionException e) {
                        throw new RuntimeException("Error while executing task", e);
                    }
                }
                catch (InterruptedException e) {
                    throw new RuntimeException("Worker interrupted", e);
                }
            }
        });
        workerThread.setName("InMemoryTaskQueueWorker");
        workerThread.start();
    }

    @Override
    public void enqueue(TaskDescriptor taskDescriptor) {
        tasks.add(taskDescriptor);
    }
}
