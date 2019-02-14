package com.dell.asm.ui.model.srsconnector;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class maps to the deregistration request received from the Alert connector UI
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobAlertConnectorDeregistrationRequest extends JobRequest
{

    @JsonProperty
    private UIVxRackFlexAlertConnectorDeregistrationRequest requestObj;

    public JobAlertConnectorDeregistrationRequest(UIVxRackFlexAlertConnectorDeregistrationRequest requestObj)
    {
        super();
        this.requestObj = requestObj;
    }

    public JobAlertConnectorDeregistrationRequest()
    {
        super();
    }

    public UIVxRackFlexAlertConnectorDeregistrationRequest getRequestObj()
    {
        return requestObj;
    }

    public void setRequestObj(UIVxRackFlexAlertConnectorDeregistrationRequest requestObj)
    {
        this.requestObj = requestObj;
    }

}
