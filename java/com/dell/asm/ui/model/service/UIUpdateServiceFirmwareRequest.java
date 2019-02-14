package com.dell.asm.ui.model.service;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIUpdateServiceFirmwareRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUpdateServiceFirmwareRequest extends UIBaseObject {

    @JsonProperty
    public List<String> idList;

    @JsonProperty
    public String scheduleType;

    @JsonProperty
    public boolean exitMaintenanceMode;

    @JsonProperty
    public String scheduleDate;

    public UIUpdateServiceFirmwareRequest() {
        super();
        idList = new ArrayList<>();
    }

}
