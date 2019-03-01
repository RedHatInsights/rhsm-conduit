/*
 * Copyright (c) 2006 - 2019 Red Hat, Inc.
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

public class TaskDescriptor {

    private String groupId;
    private TaskType taskType;
    private Map<String, String> taskArgs;

    public TaskDescriptor(String groupId, TaskType taskType) {
        this.groupId = groupId;
        this.taskArgs = new HashMap<>();
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

    public String getArg(String key) {
        return taskArgs.get(key);
    }

    public TaskDescriptor setArg(String key, String value) {
        taskArgs.put(key, value);
        return this;
    }
}
