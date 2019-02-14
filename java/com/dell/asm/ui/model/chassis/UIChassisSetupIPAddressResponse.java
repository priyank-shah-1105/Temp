package com.dell.asm.ui.model.chassis;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIChassisSetupIPAddressResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIChassisSetupIPAddressResponse extends UIBaseObject {

    /** The ipaddresses_chassis. */
    @JsonProperty
    public List<String> ipaddresses_chassis;

    /** The ipaddresses_server. */
    @JsonProperty
    public List<String> ipaddresses_server;

    /** The ipaddresses_iom. */
    @JsonProperty
    public List<String> ipaddresses_iom;

    /**
     * Instantiates a new uI chassis setup ip address response.
     */
    public UIChassisSetupIPAddressResponse() {
        super();
        this.ipaddresses_chassis = new ArrayList<String>();
        this.ipaddresses_server = new ArrayList<String>();
        this.ipaddresses_iom = new ArrayList<String>();
    }

}
