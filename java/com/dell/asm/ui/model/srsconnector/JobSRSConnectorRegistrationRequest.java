package com.dell.asm.ui.model.srsconnector;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class maps to the request received from the SRS connector UI
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobSRSConnectorRegistrationRequest extends JobRequest {

	@JsonProperty
	private UIVxRackFlexSRSConnector requestObj;

	/**
	 * Constructs {@link JobSRSConnectorRegistrationRequest} using
	 * {@link UIVxRackFlexSRSConnector} passed
	 * 
	 * @param requestObj-
	 *            Contains srs connector registration data
	 */
	public JobSRSConnectorRegistrationRequest(UIVxRackFlexSRSConnector requestObj) {
		super();
		this.requestObj = requestObj;
	}

	/**
	 * Default constructor
	 */
	public JobSRSConnectorRegistrationRequest() {
		super();
	}

	/**
	 * Get the {@link UIVxRackFlexSRSConnector} associated with this request object
	 */
	public UIVxRackFlexSRSConnector getRequestObj() {
		return requestObj;
	}

	/**
	 * Set the {@link UIVxRackFlexSRSConnector} associated with this request object
	 */
	public void setRequestObj(UIVxRackFlexSRSConnector requestObj) {
		this.requestObj = requestObj;
	}
	
}
