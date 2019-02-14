package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * The Class JobSaveTemplateRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobExportTemplateRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIExportTemplate requestObj;


    /**
     * Constructor.
     */
    public JobExportTemplateRequest() {
        super();
    }

}
    

