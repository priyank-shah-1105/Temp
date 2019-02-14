package com.dell.asm.ui.model.users;

import java.util.List;

import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.activedirectory.UIActiveDirectoryUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobStringsRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSaveImportUsersRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public List<UIActiveDirectoryUser> requestObj;

}
