package com.dell.asm.ui.adapter.service;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReport;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareUpdateRequest;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.UpdateDeviceInventoryResponse;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareDeviceInventory;

public interface DeviceInventoryServiceAdapter {

    public ResourceList<ManagedDevice> getAllDeviceInventory(String sort,
                                                             List<String> filter,
                                                             Integer offset,
                                                             Integer limit);

    public ResourceList<ManagedDevice> getAllDeviceInventoryWithCompliance(String sort,
                                                                           List<String> filter,
                                                                           Integer offset,
                                                                           Integer limit);

    public ManagedDevice getDeviceInventory(String refId);

    public UpdateDeviceInventoryResponse updateDeviceInventory(String refId,
                                                               ManagedDevice newDeviceInventory);

    public UpdateDeviceInventoryResponse[] updateDeviceInventory(List<String> refIds);

    public Response deleteDeviceInventory(String refId, boolean forceDelete);

    public Response scheduleUpdate(FirmwareUpdateRequest updateRequest);

    public Integer getTotalCount(List<String> filter);

    public void exportAllDevices(final File downloadFile);

    public FirmwareComplianceReport getFirmwareComplianceReportForDevice(String refId);

    public FirmwareDeviceInventory[] getFirmwareDeviceInventory(String refId);

    public Response processServiceMode(String refId, String mode);

    public Response validateSoftwareComponentsForService(FirmwareUpdateRequest updateRequest);
}
