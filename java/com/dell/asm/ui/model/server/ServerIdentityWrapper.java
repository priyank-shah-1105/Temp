package com.dell.asm.ui.model.server;

import com.dell.asm.asmcore.asmmanager.client.applyMgtTemplate.ServerIdentity;
import com.dell.asm.ui.model.UIBaseObject;

/**
 * ServerIdentity misses ID, this wrapper adds it.
 */
public class ServerIdentityWrapper extends UIBaseObject {

    /** The id. */
    public String id;

    /** The identity. */
    public ServerIdentity identity;

    /**
     * Instantiates a new uI firmware.
     */
    public ServerIdentityWrapper() {
        super();
        this.identity = new ServerIdentity();
    }

    /**
     * Instantiates a new wrapper.
     * @param id
     * @param identity
     */
    public ServerIdentityWrapper(String id, ServerIdentity identity) {
        super();
        this.id = id;
        this.identity = identity;
    }
}
