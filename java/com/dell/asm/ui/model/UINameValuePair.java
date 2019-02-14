package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINameValuePair.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINameValuePair extends UIBaseObject {

    /** The label. */
    @JsonProperty
    public String label;

    /** The value. */
    @JsonProperty
    public String value;

    /**
     * Instantiates a new uI name value pair.
     */
    public UINameValuePair() {
        super();
    }

    /**
     * Instantiates a new uI name value pair.
     *
     * @param label the label
     * @param value the value
     */
    public UINameValuePair(String label, String value) {
        super();
        this.label = label;
        this.value = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UINameValuePair [label=" + label + ", value=" + value + "]";
    }

}
