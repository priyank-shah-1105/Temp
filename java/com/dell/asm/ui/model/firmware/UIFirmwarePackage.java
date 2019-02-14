package com.dell.asm.ui.model.firmware;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareRepository;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareBundle;
import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UIFirmwarePackage extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String description;

    @JsonProperty
    public String source;

    @JsonProperty
    public String state;

    @JsonProperty
    public boolean defaultpackage;

    @JsonProperty
    public String filepath;

    @JsonProperty
    public String username;

    @JsonProperty
    public String password;

    @JsonProperty
    public boolean deployed;

    @JsonProperty
    public int bundles;

    @JsonProperty
    public int customBundles;

    @JsonProperty
    public int components;

    @JsonProperty
    public String created;

    @JsonProperty
    public String updated;

    @JsonProperty
    public String packageSource;

    @JsonProperty
    public String packageLocation;

    @JsonProperty
    public List<UIListItem> services;

    @JsonProperty
    public List<UIFirmwareBundle> firmwarebundles;

    @JsonProperty
    public List<UIFirmwareBundle> userbundles;

    @JsonProperty
    public List<UIFirmwareBundle> softwarebundles;

    @JsonProperty
    public String selectedRcm;

    public UIFirmwarePackage() {
        super();
        services = new ArrayList<UIListItem>();
        firmwarebundles = new ArrayList<UIFirmwareBundle>();
        userbundles = new ArrayList<UIFirmwareBundle>();
        softwarebundles = new ArrayList<UIFirmwareBundle>();
    }

    public UIFirmwarePackage(FirmwareRepository repository) {
        this.id = repository.getId();
        this.services = new ArrayList<UIListItem>();
        this.name = repository.getName();
        this.source = repository.getSourceLocation();
        this.state = repository.getState().toString();
        this.packageSource = repository.getSourceLocation().toLowerCase();
        this.defaultpackage = repository.isDefaultCatalog();
        this.created = repository.getCreatedDate().toString();
        this.updated = repository.getUpdatedDate().toString();
        this.firmwarebundles = new ArrayList<UIFirmwareBundle>();
        this.userbundles = new ArrayList<UIFirmwareBundle>();

        this.components = repository.getComponentCount();
        this.bundles = repository.getBundleCount();
        this.customBundles = repository.getUserBundleCount();

        if (repository.getSoftwareBundles() != null) {

            for (SoftwareBundle bundle : repository.getSoftwareBundles()) {
                if (bundle.getUserBundle()) {
                    this.userbundles.add(new UIFirmwareBundle(bundle));
                } else {
                    this.firmwarebundles.add(new UIFirmwareBundle(bundle));
                }
            }
        }
        this.deployed = (repository.getDeployments() != null && repository.getDeployments().size() > 0);
        if (repository.getDeployments() != null) {
            for (Deployment deployment : repository.getDeployments()) {
                this.services.add(
                        new UIListItem(deployment.getId(), deployment.getDeploymentName()));
            }
        }
    }

}
