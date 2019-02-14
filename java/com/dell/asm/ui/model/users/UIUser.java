/**************************************************************************
 *   Copyright (c) 2013 - 2015 Dell Inc. All rights reserved.             *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.users;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.credential.UICredential;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIUser.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUser extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The username. */
    @JsonProperty
    public String username;

    /** The role id. */
    @JsonProperty
    public String roleId;

    /** The lastname. */
    @JsonProperty
    public String lastname;

    /** The firstname. */
    @JsonProperty
    public String firstname;
    ;

    /** The enabled. */
    @JsonProperty
    public boolean enabled;

    /** The state. */
    @JsonProperty
    public String state;

    /** The email. */
    @JsonProperty
    public String email;

    /** The phone. */
    @JsonProperty
    public String phone;

    /** The password. */
    @JsonProperty
    public String password = UICredential.CURRENT_PASSWORD;

    /** The rolename. */
    @JsonProperty
    public String rolename;

    /** The locale. */
    @JsonProperty
    public String locale;
    /** The locale. */
    @JsonProperty
    public String serverName;

    @JsonProperty
    public boolean showdefaultpasswordprompt;

    // Password of current admin/authorized user editing user resources. Used for double confirmation/authorization.
    @JsonProperty
    public String currentpassword;

    @JsonProperty
    public String userPreference;
}
