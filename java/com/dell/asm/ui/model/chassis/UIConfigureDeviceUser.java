package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPowerSupply.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIConfigureDeviceUser extends UIBaseObject {

    @JsonProperty
    public String username;
    @JsonProperty
    public String password;
    @JsonProperty
    public String role;
    @JsonProperty
    public String confirmpassword;
    @JsonProperty
    public String lan;
    @JsonProperty
    public String idracrole;
    @JsonProperty
    public boolean serialoverlan;
    @JsonProperty
    public boolean enabled;

    /**
     * Instantiates a new device user.
     */
    public UIConfigureDeviceUser() {
        super();
    }

}
