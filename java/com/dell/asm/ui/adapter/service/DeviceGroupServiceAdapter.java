package com.dell.asm.ui.adapter.service;

import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.devicegroup.DeviceGroup;

public interface DeviceGroupServiceAdapter {

    public DeviceGroup createDeviceGroup(DeviceGroup group);

    public ResourceList<DeviceGroup> getAllDeviceGroups(String sort, List<String> filter,
                                                        Integer offset, Integer limit);

    public DeviceGroup getDeviceGroup(String refId);

    public DeviceGroup updateDeviceGroup(String refId, DeviceGroup newDeviceGroup);

    public Response deleteDeviceGroup(String refId);
}
