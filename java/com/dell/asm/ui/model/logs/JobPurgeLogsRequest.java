package com.dell.asm.ui.model.logs;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JobPurgeLogsRequest extends UIBaseObject {

    @JsonProperty
    public UIPurgeLogs requestObj;

    public JobPurgeLogsRequest() {
        super();
    }

    public JobPurgeLogsRequest(UIPurgeLogs requestObj) {
        super();
        this.requestObj = requestObj;
    }

    @Override
    public String toString() {
        return "JobPurgeLogsRequest [requestObj=" + requestObj + "]";
    }

}
