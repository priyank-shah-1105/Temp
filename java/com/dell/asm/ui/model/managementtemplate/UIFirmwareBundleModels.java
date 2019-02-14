package com.dell.asm.ui.model.managementtemplate;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIFirmwareBundleModels.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIFirmwareBundleModels extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The model name. */
    @JsonProperty
    public String modelName;

    /** The model type. */
    @JsonProperty
    public String modelType;

    /** The firmware bundle list. */
    @JsonProperty
    public List<UIFirmwareBundles> firmwareBundleList;

    /** The selected bundle id. */
    @JsonProperty
    public String selectedBundleId;

    /**
     * Instantiates a new uI firmware bundle models.
     */
    public UIFirmwareBundleModels() {
        super();
        this.firmwareBundleList = new ArrayList<UIFirmwareBundles>();
    }

}
