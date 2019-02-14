package com.dell.asm.ui.model.activedirectory;

import java.util.List;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobActiveDirectoryUserRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobActiveDirectoryGroupListRequest extends JobRequest {
    /** The request obj. */
    @JsonProperty
    public List<UIImportGroup> requestObj;
}




