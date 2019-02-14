package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class VirtualDisk.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIVirtualDisk extends UIBaseObject {
    @JsonProperty
    public String id;

    @JsonProperty
    public String raidlevel;

    @JsonProperty
    public String comparator;

    @JsonProperty
    public int numberofdisks;

    @JsonProperty
    public String disktype;

    public UIVirtualDisk() {
        super();
    }
}
