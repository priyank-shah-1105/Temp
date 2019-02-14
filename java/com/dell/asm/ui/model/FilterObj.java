package com.dell.asm.ui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class FilterObj.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterObj {

    /** The field. */
    @JsonProperty
    public String field;

    /** The op. */
    @JsonProperty
    public String op;

    /** The op target. */
    @JsonProperty
    public List<String> opTarget;

    /**
     * Instantiates a new filter obj.
     */
    public FilterObj() {
        super();
    }

    /**
     * Instantiates a new filter obj.
     *
     * @param field the field
     * @param op the op
     * @param opTarget the op target
     */
    public FilterObj(String field, String op, List<String> opTarget) {
        super();
        this.field = field;
        this.op = op;
        this.opTarget = opTarget;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FilterObj [field=" + field + ", op=" + op + ", opTarget=" + opTarget + "]";
    }

}
