package com.dell.asm.ui.model.download;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDownload extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String type;

    @JsonProperty
    public String status;

    public UIDownload() {
        super();
    }
}
