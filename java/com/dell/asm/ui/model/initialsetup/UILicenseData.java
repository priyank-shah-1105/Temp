package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UILicenseData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UILicenseData extends UIBaseObject {

    /** The licensefile. */
    @JsonProperty
    public String licensefile;

    /** The type. */
    @JsonProperty
    public String type;

    /** The totalnodes. */
    @JsonProperty
    public int totalnodes;

    /** The usednodes. */
    @JsonProperty
    public int usednodes;

    /** The availablenodes. */
    @JsonProperty
    public int availablenodes;

    /** The activationdate. */
    @JsonProperty
    public String activationdate;

    /** The expirationdate. */
    @JsonProperty
    public String expirationdate;

    /** The softwareservicetag. */
    @JsonProperty
    public String softwareservicetag;

    /** The current wizard step. */
    @JsonProperty
    public int currentWizardStep;

    /** The is valid. */
    @JsonProperty
    public boolean isValid;

    /** The signature. */
    @JsonProperty
    public String signature;

    /** The warningmessage. */
    @JsonProperty
    public String warningmessage;

    /** The expired. */
    @JsonProperty
    public boolean expired;

    /** The expiressoon. */
    @JsonProperty
    public boolean expiressoon;

    /** The expiressoonmessage. */
    @JsonProperty
    public String expiressoonmessage;

    /** The force. */
    @JsonProperty
    public boolean force;

}
