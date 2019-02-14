package com.dell.asm.ui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobStringsRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobStringsRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public List<String> requestObj;

}
