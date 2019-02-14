package com.dell.asm.ui.model.configure;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIUplink.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUplink extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public String uplinkId;
    @JsonProperty
    public String uplinkName;
    @JsonProperty
    public List<String> networks;
    @JsonProperty
    public String portChannel;

    /**
     * Constructor.
     */
    public UIUplink() {
        super();
        networks = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof UIUplink)) return false;
        if (((UIUplink) o).uplinkId == null && this.uplinkId != null) return false;
        if (((UIUplink) o).uplinkId == null && this.uplinkId == null) return true;
        return ((UIUplink) o).uplinkId.equals(this.uplinkId);
    }

}
