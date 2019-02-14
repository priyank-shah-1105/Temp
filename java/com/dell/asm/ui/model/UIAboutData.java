package com.dell.asm.ui.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIAboutData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIAboutData extends UIBaseObject {

    public static final String ASM_SERVICE_TAG_SETTING = "asm_service_tag";

    /** The applicationname. */
    @JsonProperty
    public String applicationname;

    /** The contributors. */
    @JsonProperty
    public List<String> contributors;

    /** The copyright. */
    @JsonProperty
    public String copyright;

    /** The opensource. */
    @JsonProperty
    public String opensource;

    @JsonProperty
    public String build;

    @JsonProperty
    public String legal;

    @JsonProperty
    public String patent;

    @JsonProperty
    public String trademark;

    @JsonProperty
    public String version;

    @JsonProperty
    public String serviceTag;

    /**
     * Instantiates a new uI about data.
     */
    public UIAboutData() {
        super();
        this.contributors = new ArrayList<String>();
    }
}
