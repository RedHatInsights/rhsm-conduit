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

public class TaskFactory {

    public Task build(TaskDescriptor taskDescriptor) {
        if (taskDescriptor.getTaskType() == TaskType.UPDATE_ORG_INVENTORY) {
            return new UpdateOrgInventoryTask(taskDescriptor.getArg("ord_id"));
        }
        throw new RuntimeException("Could not build task. Unknown task type: " + taskDescriptor.getTaskType());
    }
}
