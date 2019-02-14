package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Component Status
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIComponentStatus extends UIBaseObject {

    @JsonProperty
    public String componentid;

    @JsonProperty
    public String deviceid;

    @JsonProperty
    public String devicetype;

    @JsonProperty
    public String status;

    @JsonProperty
    public String statustime;

    @JsonProperty
    public String statusmessage;

    @JsonProperty
    public String resourcestate;

    public UIComponentStatus() {
        super();
    }

    public UIComponentStatus(String id, String status, String deviceType) {
        super();
        this.componentid = id;
        this.status = status;
        this.deviceid = id;
        this.devicetype = deviceType;
        this.resourcestate = status;
    }

}
