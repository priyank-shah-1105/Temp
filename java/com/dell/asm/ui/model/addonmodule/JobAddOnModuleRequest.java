package com.dell.asm.ui.model.addonmodule;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobAddOnModuleRequest extends JobRequest {

    @JsonProperty
    public UIAddOnModule requestObj;

    public JobAddOnModuleRequest(final UIAddOnModule requestObj) {
        super();
        this.requestObj = requestObj;
    }

    public JobAddOnModuleRequest() {
        super();
    }
}
