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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobCreateServiceRequest.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobAddExistingServiceRequest extends JobRequest {

    /** The request obj. */
    @JsonProperty
    public UIExistingServiceRequest requestObj;

    /**
     * Instantiates a new service request.
     */
    public JobAddExistingServiceRequest() {
        super();
    }

}
