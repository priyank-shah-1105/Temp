package com.dell.asm.ui.model;

import org.joda.time.DateTime;

import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.jraf.client.jobmgr.JrafJobInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Generic job.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIJobs extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The name. */
    @JsonProperty
    public String name;

    /** The status. */
    @JsonProperty
    public String status;

    @JsonProperty
    public String statusmessage;

    @JsonProperty
    public String progress;

    @JsonProperty
    public String startDate;

    @JsonProperty
    public String endDate;


    /** The createdBy. */
    @JsonProperty
    public String createdBy;

    /** The startDate. */
    @JsonProperty
    public String description;

    /** The currentDate this object was created that lets us calculate time elapsed**/
    @JsonProperty
    public String currentDate;

    /**
     *
     * @param job */


    public UIJobs(JrafJobInfo job) {
        this(job, DateTime.now());
    }

    public UIJobs(JrafJobInfo job, DateTime current) {
        this.id = job.getJobKey().getName(); //set name as id
        this.name = job.getName();
        this.startDate = MappingUtils.getTime(job.getStartDate());
        this.status = job.getStatus();
        this.endDate = MappingUtils.getTime(job.getEndDate());
        this.createdBy = job.getCreatedBy();
        this.statusmessage = job.getStatus();
        this.currentDate = current.toString();
    }
}

