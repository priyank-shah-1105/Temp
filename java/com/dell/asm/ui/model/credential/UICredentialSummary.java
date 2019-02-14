package com.dell.asm.ui.model.credential;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Credential Summary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UICredentialSummary extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The typeName. */
    @JsonProperty
    public String typeName;

    /** The credentialsName. */
    @JsonProperty
    public String credentialsName;

    /** The numberOfDevices. */
    @JsonProperty
    public int numberOfDevices;

    /** The createdBy. */
    @JsonProperty
    public Boolean candelete;

    /** The creationTime. */
    @JsonProperty
    public Boolean canedit;

    @JsonProperty
    public String domain;

    /**
     * Constructor.
     */
    public UICredentialSummary() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UICredentialSummary [id=" + id + ", typeName=" + typeName + ", credentialsName=" + credentialsName
                + ", numberOfDevices=" + numberOfDevices + ", candelete=" + candelete
                + ", canedit=" + canedit + "]";
    }

}
