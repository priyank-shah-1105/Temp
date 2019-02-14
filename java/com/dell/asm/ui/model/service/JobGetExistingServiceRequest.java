/**************************************************************************
 *   Copyright (c) 2015 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.template.UITemplateBuilderComponent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This request is used to get an existing service via brownfield discovery.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobGetExistingServiceRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIExistingServiceRequest requestObj;

    /**
     * Instantiates a new service request.
     */
    public JobGetExistingServiceRequest() {
        super();
    }
}
