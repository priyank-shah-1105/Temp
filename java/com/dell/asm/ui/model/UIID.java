package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIID.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIID extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The type. */
    @JsonProperty
    public String type;


    /**
     * Instantiates a new uIID.
     */
    public UIID() {
        super();
    }

    /**
     * Instantiates a new uIID.
     *
     * @param id
     *            the id
     */
    public UIID(String id, String type) {
        super();
        this.id = id;
        this.type = type;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIID [id=" + id + "]";
    }

}
