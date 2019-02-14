package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINodeData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINodeData extends UIBaseObject {

    @JsonProperty
    public String devicetype;

    @JsonProperty
    public String networkname;

    @JsonProperty
    public String vlan;

    @JsonProperty
    public String chassisname;

    @JsonProperty
    public String chassisstatus;

    @JsonProperty
    public String chassisip;

    @JsonProperty
    public String iomname;

    @JsonProperty
    public String iomstatus;

    @JsonProperty
    public String iomip;

    @JsonProperty
    public String iom2name;

    @JsonProperty
    public String iom2status;

    @JsonProperty
    public String iom2ip;

    @JsonProperty
    public String bladename;

    @JsonProperty
    public String bladestatus;

    @JsonProperty
    public String bladeip;

    public UINodeData() {
        super();
    }
}
