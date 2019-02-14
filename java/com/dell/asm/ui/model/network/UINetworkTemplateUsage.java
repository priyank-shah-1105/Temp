/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.network;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINetworkTemplateUsage.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINetworkTemplateUsage extends UIBaseObject {

    /** The template name. */
    @JsonProperty
    public String templateName;

    /** The template type. */
    @JsonProperty
    public String templateType;

    /** The template id. */
    public String templateId;


    /**
     * Instantiates a new uI network template usage.
     */
    public UINetworkTemplateUsage() {
        super();
    }

    /**
     * Instantiates a new uI network template usage.
     *
     * @param templateName the template name
     * @param templateType the template type
     */
    public UINetworkTemplateUsage(String templateName, String templateType) {
        super();
        this.templateName = templateName;
        this.templateType = templateType;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UINetworkTemplateUsage [templateName=" + templateName + ", templateType=" + templateType + "]";
    }

}
