package com.dell.asm.ui.model.srsconnector;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is the container of SRS connector registration data which comes
 * from UI
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIVxRackFlexSRSConnector extends UIBaseObject {

	private String serialNumber;

	private String model;

	private String srsAddress;

	private String state;

	private String status;

	private String suspendedUntil;

	@JsonProperty(value = "hostIp")
	private String omeIP;

	@JsonProperty(value = "username")
	private String omeUsername;

	private String omePassword;

	private String alertFilter;

	private int alertPollingIntervalHours;

	private int alertPollingIntervalMinutes;

	private String elmsSoftwareId;

	private String solutionSerialNumber;

	private String srsGatewayHostIp;

	@JsonProperty(value = "userId")
	private String srsUserId;

	private String srsPassword;

	private String deviceType;

	private int srsGatewayHostPort;

	private String connectionType;

	private String phoneHomeIp;

	private int phoneHomePort;

	/**
	 * Get the serial number of the device
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Set the serial number of the device
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
	 * Get the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Set the model
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * Get IP address of SRS
	 */
	public String getSrsAddress() {
		return srsAddress;
	}

	/**
	 * Set IP address of SRS
	 */
	public void setSrsAddress(String srsAddress) {
		this.srsAddress = srsAddress;
	}

	/**
	 * Get the state of connection between OME and SRS.(Enabled/Disabled/Suspended)
	 */
	public String getState() {
		return state;
	}

	/**
	 * Set the state of connection between OME and SRS.(Enabled/Disabled/Suspended)
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Get the status of the connection between OME and SRS.(Online/Offline)
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Set the status of the connection between OME and SRS.(Online/Offline)
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * Get IP Address hosting OME
	 */
	public String getOmeIP() {
		return omeIP;
	}

	/**
	 * Set IP Address hosting OME
	 */
	public void setOmeIP(String omeIP) {
		this.omeIP = omeIP;
	}

	/**
	 * Get the User name of OME
	 */
	public String getOmeUsername() {
		return omeUsername;
	}

	/**
	 * Set the User name of OME
	 */
	public void setOmeUsername(String omeUsername) {
		this.omeUsername = omeUsername;
	}

	/**
	 * Get the password of OME
	 */
	public String getOmePassword() {
		return omePassword;
	}

	/**
	 * Set the password of OME
	 */
	public void setOmePassword(String omePassword) {
		this.omePassword = omePassword;
	}

	/**
	 * Get the minimum alert level that should be forwarded to SRS
	 */
	public String getAlertFilter() {
		return alertFilter;
	}

	/**
	 * Set the minimum alert level that should be forwarded to SRS
	 */
	public void setAlertFilter(String alertFilter) {
		this.alertFilter = alertFilter;
	}

	/**
	 * Get the Alert polling interval - Hours
	 */
	public int getAlertPollingIntervalHours() {
		return alertPollingIntervalHours;
	}

	/**
	 * Set the Alert polling interval - Hours
	 */
	public void setAlertPollingIntervalHours(int alertPollingIntervalHours) {
		this.alertPollingIntervalHours = alertPollingIntervalHours;
	}

	/**
	 * Get the Alert polling interval - Minutes
	 */
	public int getAlertPollingIntervalMinutes() {
		return alertPollingIntervalMinutes;
	}

	/**
	 * Set the Alert polling interval - Minutes
	 */
	public void setAlertPollingIntervalMinutes(int alertPollingIntervalMinutes) {
		this.alertPollingIntervalMinutes = alertPollingIntervalMinutes;
	}

	/**
	 * Get the software id
	 */
	public String getElmsSoftwareId() {
		return elmsSoftwareId;
	}

	/**
	 * Set the software id
	 */
	public void setElmsSoftwareId(String elmsSoftwareId) {
		this.elmsSoftwareId = elmsSoftwareId;
	}

	/**
	 * Get the unique number associated with a device
	 */
	public String getSolutionSerialNumber() {
		return solutionSerialNumber;
	}

	/**
	 * Set the unique number associated with a device
	 */
	public void setSolutionSerialNumber(String solutionSerialNumber) {
		this.solutionSerialNumber = solutionSerialNumber;
	}

	/**
	 * Get the IP Address of the SRS host
	 */
	public String getSrsGatewayHostIp() {
		return srsGatewayHostIp;
	}

	/**
	 * Set the IP Address of the SRS host
	 */
	public void setSrsGatewayHostIp(String srsGatewayHostIp) {
		this.srsGatewayHostIp = srsGatewayHostIp;
	}

	/**
	 * Get the user id of the SRS host
	 */
	public String getSrsUserId() {
		return srsUserId;
	}

	/**
	 * Set the user id of the SRS host
	 */
	public void setSrsUserId(String srsUserId) {
		this.srsUserId = srsUserId;
	}

	/**
	 * Get the password of the SRS host
	 */
	public String getSrsPassword() {
		return srsPassword;
	}

	/**
	 * Set the password of the SRS host
	 */
	public void setSrsPassword(String srsPassword) {
		this.srsPassword = srsPassword;
	}

	/**
	 * Get the Device type for which alerts have to be sent to SRS
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * Set the Device type for which alerts have to be sent to SRS
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * Get the IP Address of the SRS host
	 */
	public int getSrsGatewayHostPort() {
		return srsGatewayHostPort;
	}

	/**
	 * Set the IP Address of the SRS host
	 */
	public void setSrsGatewayHostPort(int srsGatewayHostPort) {
		this.srsGatewayHostPort = srsGatewayHostPort;
	}

	public String getSuspendedUntil() {
		return suspendedUntil;
	}

	public void setSuspendedUntil(String suspendedUntil) {
		this.suspendedUntil = suspendedUntil;
	}

	/**
	 * Get the connectionType
	 */
	public String getConnectionType()
	{
		return connectionType;
	}

	/**
	 * Set the connectionType
	 */
	public void setConnectionType(String connectionType)
	{
		this.connectionType = connectionType;
	}

	/**
	 * Get the PhoneHomeIp
	 */
	public String getPhoneHomeIp()
	{
		return phoneHomeIp;
	}

	/**
	 * Set the PhoneHomeIp
	 */
	public void setPhoneHomeIp(String phoneHomeIp)
	{
		this.phoneHomeIp = phoneHomeIp;
	}

	/**
	 * Get the PhoneHomePort
	 */
	public int getPhoneHomePort()
	{
		return phoneHomePort;
	}

	/**
	 * Set the PhoneHomePort
	 */
	public void setPhoneHomePort(int phoneHomePort)
	{
		this.phoneHomePort = phoneHomePort;
	}

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
	@Override
	public String toString()
	{
		return "UIVxRackFlexSRSConnector [serialNumber=" + serialNumber + ", model=" + model + ", srsAddress=" + srsAddress + ", state="
				+ state + ", status=" + status + ", suspendedUntil=" + suspendedUntil + ", omeIP=" + omeIP + ", omeUsername=" + omeUsername
				+ ", alertFilter=" + alertFilter + ", alertPollingIntervalHours=" + alertPollingIntervalHours
				+ ", alertPollingIntervalMinutes=" + alertPollingIntervalMinutes + ", elmsSoftwareId=" + elmsSoftwareId
				+ ", solutionSerialNumber=" + solutionSerialNumber + ", srsGatewayHostIp=" + srsGatewayHostIp + ", srsUserId=" + srsUserId
				+ ", deviceType=" + deviceType + ", srsGatewayHostPort=" + srsGatewayHostPort + ", connectionType=" + connectionType
				+ ", phoneHomeIp=" + phoneHomeIp + ", phoneHomePort=" + phoneHomePort + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alertFilter == null) ? 0 : alertFilter.hashCode());
		result = prime * result + alertPollingIntervalHours;
		result = prime * result + alertPollingIntervalMinutes;
		result = prime * result + ((deviceType == null) ? 0 : deviceType.hashCode());
		result = prime * result + ((elmsSoftwareId == null) ? 0 : elmsSoftwareId.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		result = prime * result + ((omeIP == null) ? 0 : omeIP.hashCode());
		result = prime * result + ((omeUsername == null) ? 0 : omeUsername.hashCode());
		result = prime * result + ((serialNumber == null) ? 0 : serialNumber.hashCode());
		result = prime * result + ((solutionSerialNumber == null) ? 0 : solutionSerialNumber.hashCode());
		result = prime * result + ((srsAddress == null) ? 0 : srsAddress.hashCode());
		result = prime * result + ((srsGatewayHostIp == null) ? 0 : srsGatewayHostIp.hashCode());
		result = prime * result + srsGatewayHostPort;
		result = prime * result + ((srsUserId == null) ? 0 : srsUserId.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((suspendedUntil == null) ? 0 : suspendedUntil.hashCode());
		result = prime * result + ((connectionType == null) ? 0 : connectionType.hashCode());
		result = prime * result + ((phoneHomeIp == null) ? 0 : phoneHomeIp.hashCode());
		result = prime * result + phoneHomePort;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UIVxRackFlexSRSConnector other = (UIVxRackFlexSRSConnector) obj;
		if (alertFilter == null) {
			if (other.alertFilter != null)
				return false;
		} else if (!alertFilter.equals(other.alertFilter))
			return false;
		if (alertPollingIntervalHours != other.alertPollingIntervalHours)
			return false;
		if (alertPollingIntervalMinutes != other.alertPollingIntervalMinutes)
			return false;
		if (deviceType == null) {
			if (other.deviceType != null)
				return false;
		} else if (!deviceType.equals(other.deviceType))
			return false;
		if (elmsSoftwareId == null) {
			if (other.elmsSoftwareId != null)
				return false;
		} else if (!elmsSoftwareId.equals(other.elmsSoftwareId))
			return false;
		if (model == null) {
			if (other.model != null)
				return false;
		} else if (!model.equals(other.model))
			return false;
		if (omeIP == null) {
			if (other.omeIP != null)
				return false;
		} else if (!omeIP.equals(other.omeIP))
			return false;
		if (omeUsername == null) {
			if (other.omeUsername != null)
				return false;
		} else if (!omeUsername.equals(other.omeUsername))
			return false;
		if (serialNumber == null) {
			if (other.serialNumber != null)
				return false;
		} else if (!serialNumber.equals(other.serialNumber))
			return false;
		if (solutionSerialNumber == null) {
			if (other.solutionSerialNumber != null)
				return false;
		} else if (!solutionSerialNumber.equals(other.solutionSerialNumber))
			return false;
		if (srsAddress == null) {
			if (other.srsAddress != null)
				return false;
		} else if (!srsAddress.equals(other.srsAddress))
			return false;
		if (srsGatewayHostIp == null) {
			if (other.srsGatewayHostIp != null)
				return false;
		} else if (!srsGatewayHostIp.equals(other.srsGatewayHostIp))
			return false;
		if (srsGatewayHostPort != other.srsGatewayHostPort)
			return false;
		if (srsUserId == null) {
			if (other.srsUserId != null)
				return false;
		} else if (!srsUserId.equals(other.srsUserId))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (suspendedUntil == null) {
			if (other.suspendedUntil != null)
				return false;
		} else if (!suspendedUntil.equals(other.suspendedUntil))
			return false;
		if (connectionType == null) {
			if (other.connectionType != null)
				return false;
		} else if (!connectionType.equals(other.connectionType))
			return false;
		if (phoneHomeIp == null) {
			if (other.phoneHomeIp != null)
				return false;
		} else if (!phoneHomeIp.equals(other.phoneHomeIp))
			return false;
		if (phoneHomePort != other.phoneHomePort)
		{
			return false;
		}
		return true;
	}

}
