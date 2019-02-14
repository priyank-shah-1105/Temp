package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIApplianceCertificateInfo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIApplianceCertificateInfo extends UIBaseObject {

    /** The issued to. */
    @JsonProperty
    public String issuedTo;

    /** The issued by. */
    @JsonProperty
    public String issuedBy;

    /** The valid from. */
    @JsonProperty
    public String validFrom;

    /** The distinguished name. */
    @JsonProperty
    public String distinguishedName;

    /** The business name. */
    @JsonProperty
    public String businessName;

    /** The department name. */
    @JsonProperty
    public String departmentName;

    /** The locality. */
    @JsonProperty
    public String locality;

    /** The state. */
    @JsonProperty
    public String state;

    /** The country. */
    @JsonProperty
    public String country;

    /** The email. */
    @JsonProperty
    public String email;

    /** The download csr display. */
    @JsonProperty
    public boolean downloadCSRDisplay;

    /** The certificate sig req. */
    @JsonProperty
    public String certificateSigReq;

    /** The valid to. */
    @JsonProperty
    public String validTo;

    /**
     * Instantiates a new uI appliance certificate info.
     */
    public UIApplianceCertificateInfo() {
        super();
    }

}
