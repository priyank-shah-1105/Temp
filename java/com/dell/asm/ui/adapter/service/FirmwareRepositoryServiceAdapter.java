package com.dell.asm.ui.adapter.service;

import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareRepository;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareBundle;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareComponent;
import com.dell.asm.asmcore.asmmanager.client.firmwarerepository.ESRSFile;

public interface FirmwareRepositoryServiceAdapter {

    public FirmwareRepository create(FirmwareRepository firmwareRepository);

    ;

    public FirmwareRepository update(FirmwareRepository firmwareRepository);

    public Response delete(String id);

    public ResourceList<FirmwareRepository> getAll(String sort, List<String> filter, Integer offset,
                                                   Integer limit);

    public ResourceList<FirmwareRepository> getAll(String sort, List<String> filter, Integer offset,
                                                   Integer limit, boolean bundles,
                                                   boolean components);

    public FirmwareRepository getById(String id);

    public FirmwareRepository getById(String id, boolean bundles, boolean components);

    public SoftwareBundle getBundleById(String id);

    public ResourceList<FirmwareRepository> getByEqualsAttributes(
            HashMap<String, Object> attributeMap);

    public List<SoftwareComponent> getSoftwareComponents(String componentId,
                                                         String deviceId, String subDeviceId,
                                                         String vendorId,
                                                         String subVendorId, String type,
                                                         String systemId);

    public Response testConnection(FirmwareRepository firmwareRepository);

    public List<ESRSFile> getESRSRepositories();

    public static enum FirmwareRepositoryType {
        EMBEDED("embeded"), DEFAULT("default");
        String value;

        FirmwareRepositoryType(String value) {
            this.value = value;
        }

        public String toString() {
            return this.value;
        }
    }

}
