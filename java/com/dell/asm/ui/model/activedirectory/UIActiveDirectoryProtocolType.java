package com.dell.asm.ui.model.activedirectory;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood</a>
 *
 * Date Nov 26, 2013 @3:25:09 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIActiveDirectoryProtocolType extends UIBaseObject {

    /** Name of directory. */
    @JsonProperty
    public String id;

    /** AD protocol. */
    @JsonProperty
    public String protocolName;

    /**
     *
     * @param id
     * @param protocolName
     */
    public UIActiveDirectoryProtocolType(String id, String protocolName) {
        this.id = id;
        this.protocolName = protocolName;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the protocolName
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * @param protocolName the protocolName to set
     */
    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }


}