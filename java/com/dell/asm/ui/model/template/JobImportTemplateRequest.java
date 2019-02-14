package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobSaveTemplateRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobImportTemplateRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIImportTemplate requestObj;


    public JobImportTemplateRequest(UIImportTemplate requestObj) {
        super();
        this.requestObj = requestObj;
    }

    /**
     * Constructor.
     */
    public JobImportTemplateRequest() {
        super();
    }

}
    

