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
package org.candlepin.insights.controller;

import org.candlepin.insights.api.model.OrgInventory;
import org.candlepin.insights.inventory.ConduitFacts;
import org.candlepin.insights.inventory.InventoryService;
import org.candlepin.insights.pinhead.PinheadService;
import org.candlepin.insights.pinhead.client.model.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * Controller used to interact with the Inventory service.
 */
@Component
public class InventoryController {
    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private static final int KIBIBYTES_PER_GIBIBYTE = 1048576;
    private static final String COMMA_REGEX = ",\\s*";
    private static final String UUID_REGEX = "[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}";

    public static final String DMI_SYSTEM_UUID = "dmi.system.uuid";
    public static final String MAC_PREFIX = "net.interface.";
    public static final String MAC_SUFFIX = ".mac_address";

    // We should instead pull ip addresses from the following facts:
    //
    // net.interface.%.ipv4_address_list
    //      (or net.interface.%.ipv4_address if the list isn't present)
    // net.interface.%.ipv6_address.global_list
    //      (or net.interface.%.ipv6_address.global if the list isn't present)
    // net.interface.%.ipv6_address.link_list
    //      (or net.interface.%.ipv6_address.link if the list isn't present)
    public static final String IP_ADDRESS_FACT_REGEX =
        "^net\\.interface\\.[^.]*\\.ipv[46]_address(\\.global|\\.link)?(_list)?$";
    public static final String NETWORK_FQDN = "network.fqdn";
    public static final String CPU_SOCKETS = "cpu.cpu_socket(s)";
    public static final String CPU_CORES_PER_SOCKET = "cpu.core(s)_per_socket";
    public static final String MEMORY_MEMTOTAL = "memory.memtotal";
    public static final String UNAME_MACHINE = "uname.machine";
    public static final String VIRT_IS_GUEST = "virt.is_guest";
    public static final String INSIGHTS_ID = "insights_id";
    public static final String UNKNOWN = "unknown";
    public static final String TRUE = "True";
    public static final String NONE = "none";

    private final InventoryService inventoryService;

    private final PinheadService pinheadService;

    private final Validator validator;

    @Autowired
    public InventoryController(InventoryService inventoryService, PinheadService pinheadService,
        Validator validator) {
        this.inventoryService = inventoryService;
        this.pinheadService = pinheadService;
        this.validator = validator;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public ConduitFacts getFactsFromConsumer(Consumer consumer) {
        final Map<String, String> pinheadFacts = consumer.getFacts();
        ConduitFacts facts = new ConduitFacts();
        facts.setOrgId(consumer.getOrgId());

        facts.setSubscriptionManagerId(consumer.getUuid());
        facts.setInsightsId(pinheadFacts.get(INSIGHTS_ID));

        extractNetworkFacts(pinheadFacts, facts);
        extractHardwareFacts(pinheadFacts, facts);
        extractHypervisorFacts(consumer, pinheadFacts, facts);

        List<String> productIds = consumer.getInstalledProducts().stream()
            .map(installedProduct -> installedProduct.getProductId().toString()).collect(Collectors.toList());
        facts.setRhProd(productIds);

        return facts;
    }

    protected void extractHardwareFacts(Map<String, String> pinheadFacts, ConduitFacts facts) {
        String systemUuid = pinheadFacts.get(DMI_SYSTEM_UUID);
        if (!isEmpty(systemUuid)) {
            if (systemUuid.matches(UUID_REGEX)) {
                facts.setBiosUuid(systemUuid);
            }
            else {
                log.warn("Consumer {} in org {} has unparseable BIOS uuid: {}",
                    facts.getSubscriptionManagerId(), facts.getOrgId(), systemUuid);
            }
        }

        String cpuSockets = pinheadFacts.get(CPU_SOCKETS);
        String coresPerSocket = pinheadFacts.get(CPU_CORES_PER_SOCKET);
        if (!isEmpty(cpuSockets)) {
            Integer numCpuSockets = Integer.parseInt(cpuSockets);
            facts.setCpuSockets(numCpuSockets);
            if (!isEmpty(coresPerSocket)) {
                Integer numCoresPerSocket = Integer.parseInt(coresPerSocket);
                facts.setCpuCores(numCoresPerSocket * numCpuSockets);
            }
        }

        String memoryTotal = pinheadFacts.get(MEMORY_MEMTOTAL);
        if (!isEmpty(memoryTotal)) {
            try {
                int memoryBytes = Integer.parseInt(memoryTotal);
                // memtotal is a little less than accessible memory, round up to next GB
                int memoryGigabytes = (int) Math.ceil((float) memoryBytes / (float) KIBIBYTES_PER_GIBIBYTE);
                facts.setMemory(memoryGigabytes);
            }
            catch (NumberFormatException e) {
                log.warn("Bad memory.memtotal value", e);
            }
        }

        String architecture = pinheadFacts.get(UNAME_MACHINE);
        if (!isEmpty(architecture)) {
            facts.setArchitecture(architecture);
        }
    }

    protected void extractNetworkFacts(Map<String, String> pinheadFacts, ConduitFacts facts) {
        String fqdn = pinheadFacts.get(NETWORK_FQDN);
        if (!isEmpty(fqdn)) {
            facts.setFqdn(fqdn);
        }

        List<String> macAddresses = new ArrayList<>();
        pinheadFacts.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith(MAC_PREFIX) && entry.getKey().endsWith(MAC_SUFFIX))
            .forEach(entry -> {
                List<String> macs = Arrays.asList(entry.getValue().split(COMMA_REGEX));
                macAddresses.addAll(
                    macs.stream().filter(mac -> mac != null && !mac.equalsIgnoreCase(NONE) &&
                    !mac.equalsIgnoreCase(UNKNOWN)).collect(Collectors.toList())
                );
            });

        if (!macAddresses.isEmpty()) {
            facts.setMacAddresses(macAddresses);
        }
        extractIpAddresses(pinheadFacts, facts);
    }

    protected void extractIpAddresses(Map<String, String> pinheadFacts, ConduitFacts facts) {
        Set<String> ipAddresses = new HashSet<>();
        pinheadFacts.entrySet().stream()
            .filter(entry -> entry.getKey().matches(IP_ADDRESS_FACT_REGEX) && !isEmpty(entry.getValue()))
            .forEach(entry -> {
                List<String> items = Arrays.asList(entry.getValue().split(COMMA_REGEX));
                ipAddresses.addAll(items.stream()
                    .filter(addr -> !isEmpty(addr) && !addr.equalsIgnoreCase(UNKNOWN))
                    .collect(Collectors.toList())
                );
            });

        if (!ipAddresses.isEmpty()) {
            facts.setIpAddresses(new ArrayList(ipAddresses));
        }

    }

    protected void extractHypervisorFacts(Consumer consumer, Map<String, String> pinheadFacts,
        ConduitFacts facts) {

        String isGuest = pinheadFacts.get(VIRT_IS_GUEST);
        if (!isEmpty(isGuest) && !isGuest.equalsIgnoreCase(UNKNOWN)) {
            facts.setIsVirtual(isGuest.equalsIgnoreCase(TRUE));

            if (Boolean.TRUE.equals(facts.getIsVirtual())) {
                facts.setIsHypervisorUnknown(isEmpty(consumer.getHypervisorUuid()));
            }
        }

        String vmHost = consumer.getHypervisorName();
        if (!isEmpty(vmHost)) {
            // In case some system has a hypervisor name but no hypervisor UUID we still want to mark
            // it as having a known hypervisor.
            facts.setIsHypervisorUnknown(false);
            facts.setVmHost(vmHost);
        }
    }

    protected List<ConduitFacts> getValidatedConsumers(String orgId) {
        List<ConduitFacts> conduitFactsForOrg = new LinkedList<>();
        Set<String> hypervisorUuids = new HashSet<>();

        for (Consumer consumer : pinheadService.getOrganizationConsumers(orgId)) {
            if (!isEmpty(consumer.getHypervisorUuid())) {
                hypervisorUuids.add(consumer.getHypervisorUuid());
            }

            ConduitFacts facts;
            try {
                facts = getFactsFromConsumer(consumer);
            }
            catch (Exception e) {
                log.warn(String.format("Skipping consumer %s due to exception", consumer.getUuid()), e);
                continue;
            }

            facts.setAccountNumber(consumer.getAccountNumber());

            Set<ConstraintViolation<ConduitFacts>> violations = validator.validate(facts);
            if (violations.isEmpty()) {
                conduitFactsForOrg.add(facts);
            }
            else if (log.isInfoEnabled()) {
                log.info("Consumer {} failed validation: {}", consumer.getName(),
                    violations.stream().map(this::buildValidationMessage).collect(Collectors.joining("; "))
                );
            }

        }

        conduitFactsForOrg.stream().forEach(
            cf -> cf.setIsHypervisor(hypervisorUuids.contains(cf.getSubscriptionManagerId()))
        );

        return conduitFactsForOrg;
    }

    public void updateInventoryForOrg(String orgId) {
        List<ConduitFacts> conduitFactsForOrg = getValidatedConsumers(orgId);
        inventoryService.sendHostUpdate(conduitFactsForOrg);
        log.info("Host inventory update completed for org: {}", orgId);
    }

    public OrgInventory getInventoryForOrg(String orgId) {
        List<ConduitFacts> conduitFactsForOrg = getValidatedConsumers(orgId);
        return inventoryService.getInventoryForOrgConsumers(conduitFactsForOrg);
    }

    private String buildValidationMessage(ConstraintViolation<ConduitFacts> x) {
        return String.format("%s: %s: %s", x.getPropertyPath(), x.getMessage(), x.getInvalidValue());
    }
}
