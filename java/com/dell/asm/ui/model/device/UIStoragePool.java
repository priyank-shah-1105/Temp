package com.dell.asm.ui.model.device;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonProperty;


public class UIStoragePool extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String size;

    public UIStoragePool() {
        super();
    }

}
