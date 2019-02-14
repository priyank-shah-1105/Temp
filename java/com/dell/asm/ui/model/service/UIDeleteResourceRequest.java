package com.dell.asm.ui.model.service;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIDeleteServiceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIDeleteResourceRequest extends UIBaseObject {

    @JsonProperty
    public String serviceId;

    /*
     * Lists of components ID to be removed from service.
     */

    @JsonProperty
    public List<String> applicationList;
    @JsonProperty
    public List<String> serverList;
    @JsonProperty
    public List<String> vmList;
    @JsonProperty
    public List<String> clusterList;
    @JsonProperty
    public List<String> volumeList;
    @JsonProperty
    public List<String> scaleioList;


    public UIDeleteResourceRequest() {
        super();
        serverList = new ArrayList<>();
        vmList = new ArrayList<>();
        clusterList = new ArrayList<>();
        volumeList = new ArrayList<>();
        applicationList = new ArrayList<>();
        scaleioList = new ArrayList<>();
    }

}
