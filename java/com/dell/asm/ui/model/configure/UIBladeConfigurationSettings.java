package com.dell.asm.ui.model.configure;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIBladeConfigurationSettings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIBladeConfigurationSettings extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public String idForDelete;

    @JsonProperty
    public String serverId;
    @JsonProperty
    public String svctag;
    @JsonProperty
    public String slot;
    @JsonProperty
    public String managementIP;
    @JsonProperty
    public String iDRACName;


    /**
     * Constructor.
     */
    public UIBladeConfigurationSettings() {
        super();
    }

}
