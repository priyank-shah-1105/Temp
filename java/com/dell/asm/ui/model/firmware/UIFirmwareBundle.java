package com.dell.asm.ui.model.firmware;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareBundle;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareComponent;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIFirmwareBundles.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmwareBundle extends UIBaseObject implements Comparable {

    /** The id. */
    @JsonProperty
    public String id;

    /** The bundle name. */
    @JsonProperty
    public String bundleName;

    /** The bundle description. */
    @JsonProperty
    public String bundleDescription;

    @JsonProperty
    public String bundleVersion;

    @JsonProperty
    public String bundleVendor;

    @JsonProperty
    public String bundleDate;

    @JsonProperty
    public String bundleSize;

    @JsonProperty
    public String deviceType;

    @JsonProperty
    public String deviceModel;

    @JsonProperty
    public String filename;

    @JsonProperty
    public List<UIFirmware> firmwarecomponents;

    @JsonProperty
    public List<UISoftware> softwarecomponents;

    /**
     * Constructor.
     */
    public UIFirmwareBundle() {
        super();
        firmwarecomponents = new ArrayList<UIFirmware>();
        softwarecomponents = new ArrayList<UISoftware>();
    }

    /**
     * Constructor.
     */
    public UIFirmwareBundle(SoftwareBundle softwareBundle) {
        super();
        firmwarecomponents = new ArrayList<>();
        this.bundleName = softwareBundle.getName();
        this.bundleVersion = softwareBundle.getVersion();
        if (softwareBundle.getBundleDate() != null)
            this.bundleDate = softwareBundle.getBundleDate().toString();
        if (softwareBundle.getSoftwareComponents() != null)
            for (SoftwareComponent sc : softwareBundle.getSoftwareComponents())
                this.firmwarecomponents.add(new UIFirmware(sc));


    }

    @Override
    public int compareTo(Object o) {
        if (o == null)
            return -1;
        UIFirmwareBundle bundle = (UIFirmwareBundle) o;
        if (this.bundleName == null) {
            if (bundle.bundleName == null) {
                return 0;
            } else {
                return -1;
            }
        } else if (bundle.bundleName == null) {
            return 1;
        } else {
            return this.bundleName.compareToIgnoreCase(bundle.bundleName);
        }
    }
}
