package com.dell.asm.ui.model.troubleshootingbundle;

import com.dell.asm.ui.model.JobRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class maps to the request received from the UI to generate troubleshooting bundle tar.gz file
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobTroubleshootingBundleRequest extends JobRequest {

	@JsonProperty
	private UITroubleshootingBundle requestObj;

	/**
	 * Constructs {@link JobTroubleshootingBundleRequest} using
	 * {@link UITroubleshootingBundle} passed
	 *
	 * @param requestObj-
	 *            Contains srs connector registration data
	 */
	public JobTroubleshootingBundleRequest(UITroubleshootingBundle requestObj) {
		super();
		this.requestObj = requestObj;
	}

	/**
	 * Default constructor
	 */
	public JobTroubleshootingBundleRequest() {
		super();
	}

	/**
	 * Get the {@link UITroubleshootingBundle} associated with this request object
	 */
	public UITroubleshootingBundle getRequestObj() {
		return requestObj;
	}

	/**
	 * Set the {@link UITroubleshootingBundle} associated with this request object
	 */
	public void setRequestObj(UITroubleshootingBundle requestObj) {
		this.requestObj = requestObj;
	}
	
}
