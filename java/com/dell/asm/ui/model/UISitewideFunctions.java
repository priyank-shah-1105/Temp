package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UISitewideFunctions.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UISitewideFunctions {

    /** The Alarm scoreboard. */
    @JsonProperty
    public UIAlarmScoreboard AlarmScoreboard;

    /** The Tasks scoreboard. */
    @JsonProperty
    public UITasksScoreboard TasksScoreboard;

    /** The Sitewide search. */
    @JsonProperty
    public UISitewideSearch SitewideSearch;

    /**
     * Instantiates a new uI sitewide functions.
     */
    public UISitewideFunctions() {
        super();
        AlarmScoreboard = new UIAlarmScoreboard();
        TasksScoreboard = new UITasksScoreboard();
        SitewideSearch = new UISitewideSearch();
    }

    /**
     * Instantiates a new uI sitewide functions.
     *
     * @param alarmScoreboard the alarm scoreboard
     * @param tasksScoreboard the tasks scoreboard
     * @param sitewideSearch the sitewide search
     */
    public UISitewideFunctions(UIAlarmScoreboard alarmScoreboard, UITasksScoreboard tasksScoreboard,
                               UISitewideSearch sitewideSearch) {
        super();
        AlarmScoreboard = alarmScoreboard;
        TasksScoreboard = tasksScoreboard;
        SitewideSearch = sitewideSearch;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UISitewideFunctions [AlarmScoreboard=" + AlarmScoreboard + ", TasksScoreboard=" + TasksScoreboard + ", SitewideSearch="
                + SitewideSearch + "]";
    }

}
