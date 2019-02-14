/**************************************************************************
 *   Copyright (c) 2014 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.dell.pg.asm.chassis.client.device.Chassis;
import com.dell.pg.asm.chassis.client.device.Server;

public class ChassisCache {
    private Map<Key, Value> cache = new HashMap<>();

    private String buildChassisLocation(Chassis chassis) {
        StringBuilder location = new StringBuilder();
        if (chassis.getDatacenter() != null)
            location.append(chassis.getDatacenter().trim());
        if (chassis.getAisle() != null)
            location.append("_").append(chassis.getAisle().trim());
        if (chassis.getRack() != null)
            location.append("_").append(chassis.getRack().trim());
        if (chassis.getRackslot() != null)
            location.append("_").append(chassis.getRackslot().trim());
        return location.toString();
    }

    private String buildBladeLocation(Chassis chassis,
                                      Server blade) {
        StringBuilder location = new StringBuilder();
        if (chassis.getName() != null) {
            location.append(chassis.getName().trim());
        }

        String slot = blade.getSlot();
        location.append(" slot ").append(slot);
        return location.toString();
    }

    public void addChassis(Chassis chassis) {
        Key chassisKey = new Key(chassis.getRefId(),
                                 null);
        Value chassisValue = new Value(chassis,
                                       buildChassisLocation(chassis));
        cache.put(chassisKey,
                  chassisValue);

        List<Server> blades = chassis.getServers();
        if (blades != null) {
            for (Server blade : blades) {
                Key serverKey = new Key(null,
                                        blade.getServiceTag());
                Value serverValue = new Value(chassis,
                                              buildBladeLocation(chassis,
                                                                 blade));
                cache.put(serverKey,
                          serverValue);
            }
        }
    }

    public Value getByChassisRefId(String refId) {
        Key key = new Key(refId,
                          null);
        return cache.get(key);
    }

    public Value getByBladeServiceTag(String serviceTag) {
        Key key = new Key(null,
                          serviceTag);
        return cache.get(key);
    }

    private static class Key {
        private final String chassisRefId;
        private final String bladeServiceTag;

        public Key(String chassisRefId,
                   String bladeServiceTag) {
            this.chassisRefId = chassisRefId;
            this.bladeServiceTag = bladeServiceTag;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key that = (Key) o;

            if (bladeServiceTag != null ? !bladeServiceTag.equals(
                    that.bladeServiceTag) : that.bladeServiceTag != null)
                return false;
            if (chassisRefId != null ? !chassisRefId.equals(
                    that.chassisRefId) : that.chassisRefId != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = chassisRefId != null ? chassisRefId.hashCode() : 0;
            result = 31 * result + (bladeServiceTag != null ? bladeServiceTag.hashCode() : 0);
            return result;
        }
    }

    public static class Value {
        private final Chassis chassis;
        private final String location;

        public Value(Chassis chassis,
                     String location) {
            this.chassis = chassis;
            this.location = location;
        }

        public Chassis getChassis() {
            return chassis;
        }

        public String getChassisName() {
            return StringUtils.trim(chassis.getName());
        }

        public String getLocation() {
            return location;
        }
    }
}
