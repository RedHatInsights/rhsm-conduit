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
package org.candlepin.insights.queue;


/**
 * An object that queues work that is to be done in the inventory.
 */
public interface InventoryWorkQueue<P extends WorkItemProcessor> {

    /**
     * Enqueues the work needed to be done to update the specified organization's
     * consumer inventory.
     *
     * @param orgId the id of the organization that should have its consumer inventory updated.
     */
    void enqueueOrgUpdatedWork(String orgId);

    /**
     * Adds an WorkItemProcessor to this queue that will process any work items added to the queue.
     *
     * @param processor the work item processor to add to the queue.
     */
    void addProcessor(P processor);
}
