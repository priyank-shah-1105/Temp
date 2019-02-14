/**************************************************************************
 *   Copyright (c) 2012 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIRelatedComponent extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The installOrder. */
    @JsonProperty
    public Integer installOrder;

    @JsonProperty
    public String subtype;

    /**
     * Instantiates a new uI list item.
     */
    public UIRelatedComponent() {
        super();
    }

    /**
     * Instantiates a new uI list item.
     *
     * @param id
     *            the id
     * @param name
     *            the name
     */
    public UIRelatedComponent(String id, String name, Integer installOrder) {
        super();
        this.id = id;
        this.name = name;
        this.installOrder = installOrder;
    }

    @Override
    public String toString() {
        return "UIRelatedComponent{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", installOrder=" + installOrder +
                ", subtype='" + subtype + '\'' +
                '}';
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UIRelatedComponent))
            return false;
        UIRelatedComponent other = (UIRelatedComponent) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
