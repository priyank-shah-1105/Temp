package com.dell.asm.ui.model.configuretemplate;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIListItem;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIStorageSettings extends UIConfigureTemplateCategory {

    @JsonProperty
    public List<UIListItem> storageArrays;

    @JsonProperty
    public String storageArrayId;

    @JsonProperty
    public List<UIConfigureTemplateListItem> storagePools;

    @JsonProperty
    public String storagePoolId;


    @JsonProperty
    public List<UIListItem> numVolumesList;

    @JsonProperty
    public String numStorageVolumes;

    @JsonProperty
    public Integer storageVolumeSize;

    @JsonProperty
    public List<UIListItem> storageVolumeUnits;

    @JsonProperty
    public String storageVolumeUnit;

    @JsonProperty
    public String volumeName;

    public UIStorageSettings() {
        super();
        this.storageArrays = new ArrayList<>();
        this.storagePools = new ArrayList<>();
        this.numVolumesList = new ArrayList<>();
        this.storageVolumeUnits = new ArrayList<>();
    }
}
