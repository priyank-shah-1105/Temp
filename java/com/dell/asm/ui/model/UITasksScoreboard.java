package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UITasksScoreboard.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UITasksScoreboard {

    /** The Should show tasks scoreboard. */
    @JsonProperty
    public boolean ShouldShowTasksScoreboard;

    /** The Running tasks total. */
    @JsonProperty
    public int RunningTasksTotal;

    /** The Errors total. */
    @JsonProperty
    public int ErrorsTotal;

    /**
     * Instantiates a new uI tasks scoreboard.
     */
    public UITasksScoreboard() {
        super();
    }

    /**
     * Instantiates a new uI tasks scoreboard.
     *
     * @param shouldShowTasksScoreboard the should show tasks scoreboard
     * @param runningTasksTotal the running tasks total
     * @param errorsTotal the errors total
     */
    public UITasksScoreboard(boolean shouldShowTasksScoreboard, int runningTasksTotal,
                             int errorsTotal) {
        super();
        ShouldShowTasksScoreboard = shouldShowTasksScoreboard;
        RunningTasksTotal = runningTasksTotal;
        ErrorsTotal = errorsTotal;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UITasksScoreboard [ShouldShowTasksScoreboard=" + ShouldShowTasksScoreboard + ", RunningTasksTotal=" + RunningTasksTotal
                + ", ErrorsTotal=" + ErrorsTotal + "]";
    }

}
