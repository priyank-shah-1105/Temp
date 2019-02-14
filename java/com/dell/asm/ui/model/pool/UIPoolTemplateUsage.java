/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.model.pool;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIPoolTemplateUsage.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPoolTemplateUsage extends UIBaseObject {

    /** The template name. */
    @JsonProperty
    public String templateName;

    /**
     * Instantiates a new uI pool template usage.
     */
    public UIPoolTemplateUsage() {
        super();
    }

    /**
     * Instantiates a new uI pool template usage.
     *
     * @param name
     *            the name
     */
    public UIPoolTemplateUsage(String name) {
        this.templateName = name;
    }

}
