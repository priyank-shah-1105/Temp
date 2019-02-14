package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class FirmwareDevice.
 * id : string
 devices : [FirmwareDevice
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmwareDevice extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The deviceType. */
    @JsonProperty
    public String type;

    /** The serviceTag. */
    @JsonProperty
    public String servicetag;

    /** The template. */
    @JsonProperty
    public String template;

    /** The version. */
    @JsonProperty
    public String version;

    /** The status. */
    @JsonProperty
    public String status;

    /** The model. */
    @JsonProperty
    public String model;

    /** The name. */
    @JsonProperty
    public String name;

    /** The repository. */
    @JsonProperty
    public String repository;

    @JsonProperty
    public String profile;

    @JsonProperty
    public List<String> components;

    @JsonProperty
    public List<UIFirmwareDevice> devices;


    /**
     * Instantiates a new uI device summary.
     */
    public UIFirmwareDevice() {
        super();
        devices = new ArrayList<UIFirmwareDevice>();
        components = new ArrayList<String>();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIDevice [id=" + id + ", name=" + name + ", type=" + type + ", serviceTag=" + servicetag
                + ", profile=" + profile + "]";
    }

}
