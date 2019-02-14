package com.dell.asm.ui.model.activedirectory;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Soo</a>
 *
 * Date Nov 29, 2013 @12:55:47 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIActiveDirectory extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public Long id;

    /** Name of directory. */
    @JsonProperty
    public String directoryName;

    /** AD protocol. */
    @JsonProperty
    public String protocolName;

    @JsonProperty
    public String serverName;

    /** Directory Status - Enable/Disable. */
    @JsonProperty
    public String status;

    /** Directory Type */
    @JsonProperty
    public String typeId;

    /** Host name or IP Address */
    @JsonProperty
    public String hostName;

    /** Port */
    @JsonProperty
    public String port;

    /** UserName **/
    @JsonProperty
    public String bindDN;

    /** method */
    @JsonProperty
    public String method;

    /** Password for UserName */
    @JsonProperty
    public String password;

    @JsonProperty
    public String confirmpassword;

    /** Filter type */
    @JsonProperty
    public String filterSettingType;

    /** Base DN */
    @JsonProperty
    public String baseDN;


    /** Username Attribute */
    @JsonProperty
    public String userName;

    /** Email Attribute */
    @JsonProperty
    public String email;

    /** Lastname Attribute */
    @JsonProperty
    public String lastName;

    /** FirstName Attribute */
    @JsonProperty
    public String firstName;


}