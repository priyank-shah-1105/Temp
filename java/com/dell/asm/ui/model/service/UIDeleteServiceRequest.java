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
public class UIDeleteServiceRequest extends UIBaseObject {

    @JsonProperty
    public String serviceId;

    @JsonProperty
    public boolean deleteServers;

    @JsonProperty
    public List<String> serverList;

    @JsonProperty
    public boolean deleteVMs;

    @JsonProperty
    public List<String> vmList;

    @JsonProperty
    public boolean deleteClusters;

    @JsonProperty
    public List<String> clusterList;

    @JsonProperty
    public boolean deleteStorageVolumes;

    @JsonProperty
    public List<String> volumeList;

    @JsonProperty
    public boolean deleteScaleios;

    @JsonProperty
    public List<String> scaleioList;

    public UIDeleteServiceRequest() {
        super();
        serverList = new ArrayList<>();
        vmList = new ArrayList<>();
        clusterList = new ArrayList<>();
        volumeList = new ArrayList<>();
        scaleioList = new ArrayList<>();
    }

}
