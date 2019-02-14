package com.dell.asm.ui.model.users;

import com.dell.asm.ui.model.CriteriaObj;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class JobRequest and JobResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobMessage {

    /**
     * The criteria obj.
     */
    @JsonProperty
    public CriteriaObj criteriaObj;

    /**
     * Instantiates a new job request.
     */
    public JobMessage() {
        super();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Message [criteriaObj=" + criteriaObj + "]";
    }

}
