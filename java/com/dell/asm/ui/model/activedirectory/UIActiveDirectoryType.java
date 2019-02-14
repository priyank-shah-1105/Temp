package com.dell.asm.ui.model.activedirectory;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood</a>
 *
 * Date Nov 26, 2013 @3:26:56 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIActiveDirectoryType extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String typeId;

    /** Name of directory. */
    @JsonProperty
    public String directoryName;

    /**
     *
     * @param typeId
     * @param directoryName
     */
    public UIActiveDirectoryType(String typeId, String directoryName) {
        this.directoryName = directoryName;
        this.typeId = typeId;
    }

    /**
     * @return the typeId
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * @param typeId the typeId to set
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /**
     * @return the directoryName
     */
    public String getDirectoryName() {
        return directoryName;
    }

    /**
     * @param directoryName the directoryName to set
     */
    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }


}