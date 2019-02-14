package com.dell.asm.ui.model.appliance;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UICurrentUsersAndJobs.
 */
public class UICurrentUsersAndJobs extends UIBaseObject {

    /** The currentusers. */
    @JsonProperty
    public int currentusers;

    /** The pendingjobs. */
    @JsonProperty
    public int pendingjobs;

}
