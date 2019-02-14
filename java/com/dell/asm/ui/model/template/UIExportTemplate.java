package com.dell.asm.ui.model.template;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIExportTemplate.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIExportTemplate extends UIBaseObject {

    @JsonProperty
    public String templateId;

    @JsonProperty
    public String fileName;

    @JsonProperty
    public boolean useEncPwdFromBackup;

    @JsonProperty
    public String encryptionPassword;


    public UIExportTemplate() {
        super();
    }

}
