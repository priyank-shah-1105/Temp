package com.dell.asm.ui.model.chassis;

import java.util.List;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobChassisConnectionCheckRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobChassisConnectionCheckRequest extends JobRequest {

    /**
     * The request obj.
     */
    @JsonProperty
    public List<UIChassisConnectionCheck> requestObj;

    /**
     * Constructor.
     */
    public JobChassisConnectionCheckRequest() {
        super();
    }

}
