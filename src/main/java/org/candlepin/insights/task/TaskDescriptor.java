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

import java.util.HashMap;
import java.util.Map;


/**
 * A TaskDescriptor describes a Task that is to be stored in a TaskQueue and is to be
 * eventually executed by a TaskWorker.
 *
 * When describing a Task that is the be queued, a descriptor must at least define the
 * task group that a task belongs to, as well as the TaskType of the Task.
 *
 * A TaskDescritor requires two key pieces of data; a groupId and a TaskType.
 *
 * A groupId should be specified so that the TaskQueue can use it for task organization within the queue.
 *
 * A TaskType should be specified so that the TaskFactory can use it to build an associated Task object
 * that defines the actual work that is to be done.
 *
 * A descriptor can also specify any task arguments to customize task execution.
 */
public class TaskDescriptor {

    private String groupId;
    private TaskType taskType;
    private Map<String, String> taskArgs;

    public TaskDescriptor() {
        this.taskArgs = new HashMap<>();
    }

    public TaskDescriptor(String groupId, TaskType taskType) {
        this();
        this.groupId = groupId;
        this.taskType = taskType;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType type) {
        this.taskType = type;
    }

    public Map<String, String> getTaskArgs() {
        return taskArgs;
    }

    public void setTaskArgs(Map<String, String> args) {
        this.taskArgs = args;
    }

    //
    // Convenience builder methods.
    //
    public TaskDescriptor groupId(String groupId) {
        setGroupId(groupId);
        return this;
    }

    public TaskDescriptor taskType(TaskType taskType) {
        this.taskType = taskType;
        return this;
    }

    public String getArg(String key) {
        return taskArgs.get(key);
    }

    public TaskDescriptor setArg(String key, String value) {
        taskArgs.put(key, value);
        return this;
    }
}
