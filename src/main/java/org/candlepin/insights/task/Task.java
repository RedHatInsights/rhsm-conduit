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

import java.util.HashMap;
import java.util.Map;

public abstract class Task {

    private String groupId;
    private TaskType taskType;
    private Map<String, String> values;

    public Task(String groupId) {
        this.groupId = groupId;
        this.values = new HashMap<>();
        this.taskType = getType();
    }

    abstract TaskType getType();

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setExtraValues(Map<String, String> extraValues) {
        this.values = extraValues;
    }

    //
    // Convenience builder methods.
    //
    public Task groupId(String groupId) {
        setGroupId(groupId);
        return this;
    }

    public String getValue(String key) {
        return values.get(key);
    }

    public Task setValue(String key, String value) {
        values.put(key, value);
        return this;
    }
}
