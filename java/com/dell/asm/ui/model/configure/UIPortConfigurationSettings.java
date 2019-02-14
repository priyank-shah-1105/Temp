package com.dell.asm.ui.model.configure;

import java.util.UUID;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * UIPortConfigurationSettings.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIPortConfigurationSettings extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public String portId;
    @JsonProperty
    public String portType;
    @JsonProperty
    public String portLabel;
    @JsonProperty
    public String uplinkId;
    @JsonProperty
    public String uplinkName;

    public int portNumber;

    /**
     * Constructor.
     */
    public UIPortConfigurationSettings() {
        super();
        id = UUID.randomUUID().toString();
        portId = id;
        portType = "";
        portLabel = "";
        uplinkId = "";
        uplinkName = "";
    }

    /**
     * Label: like 'Te 0/43'
     * @param label
     */
    public UIPortConfigurationSettings(String label) {
        super();
        id = UUID.randomUUID().toString();
        portId = label;
        String[] arr = label.split(" ");
        if (arr != null && arr.length == 2) {
            portType = arr[0];
            arr = arr[1].split("/");
            if (arr != null && arr.length == 2) {
                try {
                    portNumber = Integer.parseInt(arr[1]);
                } catch (NumberFormatException ne) {
                    portNumber = 0;
                }
            }
        }
        portLabel = String.valueOf(portNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof UIPortConfigurationSettings)) return false;
        UIPortConfigurationSettings uiport = (UIPortConfigurationSettings) o;

        if (this.portType == null && uiport.portType != null) return false;
        if (this.portLabel == null && uiport.portLabel != null) return false;
        if (this.uplinkId == null && uiport.uplinkId != null) return false;
        if (this.uplinkName == null && uiport.uplinkName != null) return false;

        if (this.portType != null && !this.portType.equals(uiport.portType))
            return false;

        if (this.portLabel != null && !this.portLabel.equals(uiport.portLabel))
            return false;

        if (this.uplinkId != null && !this.uplinkId.equals(uiport.uplinkId))
            return false;

        if (this.uplinkName != null && !this.uplinkName.equals(uiport.uplinkName))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = portType.hashCode();
        result = 31 * result + portLabel.hashCode();
        result = 31 * result + uplinkId.hashCode();
        result = 31 * result + uplinkName.hashCode();
        return result;
    }
}
