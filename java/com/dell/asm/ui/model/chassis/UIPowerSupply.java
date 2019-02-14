package com.dell.asm.ui.model.chassis;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPowerSupply.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPowerSupply extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The powerstatus. */
    @JsonProperty
    public String powerstatus;

    /** The capacity. */
    @JsonProperty
    public String capacity;

    /**
     * Instantiates a new uI power supply.
     */
    public UIPowerSupply() {
        super();
    }

    /**
     * Instantiates a new uI power supply.
     *
     * @param id
     *            the id
     * @param name
     *            the name
     * @param powerstatus
     *            the powerstatus
     * @param capacity
     *            the capacity
     */
    public UIPowerSupply(String id, String name, String powerstatus,
                         String capacity) {
        super();
        this.id = id;
        this.name = name;
        this.powerstatus = powerstatus;
        this.capacity = capacity;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIPowerSupply [id=" + id + ", name=" + name + ", powerstatus="
                + powerstatus + ", capacity=" + capacity + "]";
    }

}
