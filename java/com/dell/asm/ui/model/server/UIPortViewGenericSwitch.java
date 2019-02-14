package com.dell.asm.ui.model.server;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPortViewTorSwitch.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortViewGenericSwitch extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String hostname;

    @JsonProperty
    public String model;

    @JsonProperty
    public String assettag;

    @JsonProperty
    public String ipaddress;

    @JsonProperty
    public String ipaddressurl;

    @JsonProperty
    public String deviceType;

    @JsonProperty
    public String statusMessage = "";

    @JsonProperty
    public String health = "unknown";

    @JsonProperty
    public List<UIPortViewSwitchPort> downlinkPorts;

    public int rank = 0;

    /**
     * Instantiates a new uI port view tor switch.
     */
    public UIPortViewGenericSwitch() {
        super();
        downlinkPorts = new ArrayList<>();
        id = UUID.randomUUID().toString();
    }

    public boolean matches(List<String> filterList) {
        if (CollectionUtils.isNotEmpty(filterList)) {
            for (String filter : filterList) {
                String[] farr = filter.split(",");
                if (farr.length < 3) continue;

                if (farr[0].equals("eq") && farr[1].equals("health")) {
                    if (!filter.contains(this.health))
                        return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UIPortViewGenericSwitch))
            return false;

        return id.equals(((UIPortViewGenericSwitch) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

}
