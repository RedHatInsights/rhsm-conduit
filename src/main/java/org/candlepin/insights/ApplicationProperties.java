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
package org.candlepin.insights;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * POJO to hold property values via Spring's "Type-Safe Configuration Properties" pattern
 */
@Component
@ConfigurationProperties(prefix = "rhsm-conduit")
public class ApplicationProperties {
    private String version;
    private boolean prettyPrintJson = false;
    private boolean devMode = false;
    private String hawtioBasePath;
    private String antiCsrfDomainSuffix = ".redhat.com";
    private int antiCsrfPort = 443;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isPrettyPrintJson() {
        return prettyPrintJson;
    }

    public void setPrettyPrintJson(boolean prettyPrintJson) {
        this.prettyPrintJson = prettyPrintJson;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public String getHawtioBasePath() {
        return hawtioBasePath;
    }

    public void setHawtioBasePath(String hawtioBasePath) {
        this.hawtioBasePath = hawtioBasePath;
    }

    public String getAntiCsrfDomainSuffix() {
        return antiCsrfDomainSuffix;
    }

    public void setAntiCsrfDomainSuffix(String antiCsrfDomainSuffix) {
        this.antiCsrfDomainSuffix = antiCsrfDomainSuffix;
    }

    public int getAntiCsrfPort() {
        return antiCsrfPort;
    }

    public void setAntiCsrfPort(int antiCsrfPort) {
        this.antiCsrfPort = antiCsrfPort;
    }
}
