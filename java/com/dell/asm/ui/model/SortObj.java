package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class SortObj.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SortObj {

    /** The field. */
    @JsonProperty
    public String field;

    /** The order. */
    @JsonProperty
    public int order;

    /**
     * Instantiates a new sort obj.
     */
    public SortObj() {
        super();
    }

    /**
     * Instantiates a new sort obj.
     *
     * @param field the field
     * @param order the order
     */
    public SortObj(String field, int order) {
        super();
        this.field = field;
        this.order = order;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SortObj [field=" + field + ", order=" + order + "]";
    }

}
