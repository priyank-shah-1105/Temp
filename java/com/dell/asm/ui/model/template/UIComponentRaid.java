package com.dell.asm.ui.model.template;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class Component Raid
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIComponentRaid extends UIBaseObject {

    @JsonProperty
    public String raidtype;

    @JsonProperty
    public String id;

    @JsonProperty
    public String basicraidlevel;

    @JsonProperty
    public boolean enableglobalhotspares;

    @JsonProperty
    public int globalhotspares;

    @JsonProperty
    public int minimumssd;

    @JsonProperty
    public List<UIVirtualDisk> virtualdisks;

    @JsonProperty
    public int minimumssdexternal;

    @JsonProperty
    public boolean enableglobalhotsparesexternal;

    @JsonProperty
    public int globalhotsparesexternal;

    @JsonProperty
    public List<UIVirtualDisk> externalvirtualdisks;

    public UIComponentRaid() {
        super();
        virtualdisks = new ArrayList();
        externalvirtualdisks = new ArrayList();
    }

}
