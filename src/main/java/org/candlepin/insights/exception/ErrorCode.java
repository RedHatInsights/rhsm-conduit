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
package org.candlepin.insights.exception;

/**
 * Represents the various application codes.
 *
 * RHSMCONDUIT1XXX: General rhsm-conduit application error code space.
 * RHSMCONDUIT2XXX: Insights Inventory API related application error code space.
 * RHSMCONDUIT3XXX: Pinhead API related application error code space.
 *
 */
public enum ErrorCode {

    /**
     * An unhandled exception has occurred. This typically implies that the
     * exception was unexpected and likely due to a bug or coding error.
     */
    UNHANDLED_EXCEPTION_ERROR(1000, "An unhandled exception occurred"),

    /**
     * An exception was thrown by Jersey while processing a request to the
     * rhsm-conduit API. This typically means that an HTTP client error has
     * occurred (HTTP 4XX).
     */
    REQUEST_PROCESSING_ERROR(1001, "An error occurred while processing a request."),

    /**
     * An unexpected exception was thrown by the inventory service client.
     */
    INVENTORY_SERVICE_ERROR(2000, "Inventory Service Error"),

    /**
     * The inventory service is unavailable. This typically means that the
     * inventory service is either down, or the configured service URL is
     * not correct, and a connection can not be made.
     */
    INVENTORY_SERVICE_UNAVAILABLE(2001, "The inventory service is unavailable"),

    /**
     * An exception was thrown by the inventory service when a request was made.
     * This typically means that an HTTP client error has occurred (HTTP 4XX)
     * when rhsm-conduit made the request.
     */
    INVENTORY_SERVICE_REQUEST_ERROR(2002, "Inventory API Error"),

    /**
     * An unexpected exception was thrown by the Pinhead service client.
     */
    PINHEAD_SERVICE_ERROR(3000, "Pinhead Service Error"),

    /**
     * The pinhead service is unavailable. This typically means that the
     * pinhead service is either down, or the configured service URL is
     * not correct, and a connection can not be made.
     */
    PINHEAD_SERVICE_UNAVAILABLE(3001, "The inventory service is unavailable"),

    /**
     * An exception was thrown by the pinhead service when a request was made.
     * This typically means that an HTTP client error has occurred (HTTP 4XX)
     * when rhsm-conduit made the request.
     */
    PINHEAD_SERVICE_REQUEST_ERROR(3002, "Inventory API Error");



    private final String CODE_PREFIX = "RHSMCONDUIT";

    private String code;
    private String description;

    ErrorCode(int intCode, String description) {
        this.code = CODE_PREFIX + intCode;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", code, description);
    }

}
