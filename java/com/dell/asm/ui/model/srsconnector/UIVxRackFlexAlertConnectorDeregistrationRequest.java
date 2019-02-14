package com.dell.asm.ui.model.srsconnector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class is the container of Alert connector deregistration request data which comes
 * from UI
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIVxRackFlexAlertConnectorDeregistrationRequest
{

    private String connectionType;

    public String getConnectionType()
    {
        return connectionType;
    }

    public void setConnectionType(String connectionType)
    {
        this.connectionType = connectionType;
    }
}
