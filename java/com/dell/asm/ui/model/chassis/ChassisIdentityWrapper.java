package com.dell.asm.ui.model.chassis;

import com.dell.asm.asmcore.asmmanager.client.applyMgtTemplate.ChassisIdentity;
import com.dell.asm.ui.model.UIBaseObject;

/**
 * ServerIdentity misses ID, this wrapper adds it.
 */
public class ChassisIdentityWrapper extends UIBaseObject {

    /** The id. */
    public String id;

    /** The identity. */
    public ChassisIdentity identity;

    /**
     * Instantiates a new uI firmware.
     */
    public ChassisIdentityWrapper() {
        super();
        this.identity = new ChassisIdentity();
    }

    /**
     * Instantiates a new wrapper.
     * @param id
     * @param identity
     */
    public ChassisIdentityWrapper(String id, ChassisIdentity identity) {
        super();
        this.id = id;
        this.identity = identity;
    }
}
