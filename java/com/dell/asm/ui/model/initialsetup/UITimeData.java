package com.dell.asm.ui.model.initialsetup;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UITimeData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITimeData extends UIBaseObject {

    /** The time zone. */
    @JsonProperty
    public String timeZone;

    /** The enable ntp server. */
    @JsonProperty
    public boolean enableNTPServer = true;

    /** The preferred ntp server. */
    @JsonProperty
    public String preferredNTPServer;

    /** The secondary ntp server. */
    @JsonProperty
    public String secondaryNTPServer;

    /** The current wizard step. */
    @JsonProperty
    public int currentWizardStep;

    /**
     * Instantiates a new uI time data.
     */
    public UITimeData() {
        super();
    }

}
