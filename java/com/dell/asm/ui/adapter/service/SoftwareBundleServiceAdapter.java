package com.dell.asm.ui.adapter.service;

import java.util.List;

import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareBundle;

public interface SoftwareBundleServiceAdapter {
    ResourceList<SoftwareBundle> getAllSoftwareBundles(String sort, List<String> filter,
                                                       Integer offset, Integer limit,
                                                       String fwRepoId);

    SoftwareBundle getSoftwareBundle(String id);

    void updateSoftwareBundle(String id, SoftwareBundle softwareBundle);

    void deleteSoftwareBundle(String id);

    SoftwareBundle addSoftwareBundle(SoftwareBundle softwareBundle);
}
