package com.dell.asm.ui.adapter.service;

import java.util.List;

import com.dell.pg.asm.chassis.client.device.Chassis;

/**
 * Interface for chassis
 */
public interface ChassisServiceAdapter {
    Chassis createChassis(Chassis chassisDevice);

    void deleteChassis(String s);

    Chassis getChassis(String s);

    ResourceList<Chassis> getChassises(String sort, List<String> filter, Integer offset,
                                       Integer limit);

    void updateChassis(String s, Chassis chassisDevice);

    Chassis getChassisByServiceTag(String serviceTag, String type);
}
