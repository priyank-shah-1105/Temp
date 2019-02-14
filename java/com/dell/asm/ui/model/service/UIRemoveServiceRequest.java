package com.dell.asm.ui.model.service;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedState;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.engine.spi.Managed;

/**
 * The Class UIDRemoveServiceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIRemoveServiceRequest extends UIBaseObject {

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public String serversInInventory;

    @JsonProperty
    public String resourceState;

    public UIRemoveServiceRequest(String serviceId) {
        super();
        this.serviceId = serviceId;
    }

    public UIRemoveServiceRequest() { super (); }
}