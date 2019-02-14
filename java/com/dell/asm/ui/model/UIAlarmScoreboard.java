package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIAlarmScoreboard.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIAlarmScoreboard {

    /** The Should show alarm scoreboard. */
    @JsonProperty
    public boolean ShouldShowAlarmScoreboard;

    /** The Critial alerts total. */
    @JsonProperty
    public int CritialAlertsTotal;

    /** The Warning alerts total. */
    @JsonProperty
    public int WarningAlertsTotal;

    /**
     * Instantiates a new uI alarm scoreboard.
     */
    public UIAlarmScoreboard() {
        super();
    }

    /**
     * Instantiates a new uI alarm scoreboard.
     *
     * @param shouldShowAlarmScoreboard the should show alarm scoreboard
     * @param critialAlertsTotal the critial alerts total
     * @param warningAlertsTotal the warning alerts total
     */
    public UIAlarmScoreboard(boolean shouldShowAlarmScoreboard, int critialAlertsTotal,
                             int warningAlertsTotal) {
        super();
        ShouldShowAlarmScoreboard = shouldShowAlarmScoreboard;
        CritialAlertsTotal = critialAlertsTotal;
        WarningAlertsTotal = warningAlertsTotal;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UIAlarmScoreboard [ShouldShowAlarmScoreboard=" + ShouldShowAlarmScoreboard + ", CritialAlertsTotal=" + CritialAlertsTotal
                + ", WarningAlertsTotal=" + WarningAlertsTotal + "]";
    }

}
