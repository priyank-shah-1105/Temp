package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIListItem.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIListItem extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /**
     * Instantiates a new uI list item.
     */
    public UIListItem() {
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
    public UIListItem(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIListItem [id=" + id + ", name=" + name + "]";
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
        if (obj == null || !(obj instanceof UIListItem))
            return false;
        UIListItem other = (UIListItem) obj;
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
