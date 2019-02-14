package com.dell.asm.ui.adapter.service;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.srsconnector.SRSConnectorSettings;

/**
 * This class provides APIs to make a rest call to the SRS connector back end
 */
public interface SRSConnectorAdapter {
    /**
     * Method to get SRS connector configuration details
     *
     * @return An object of type {@link SRSConnectorSettings}
     */
	SRSConnectorSettings getConfiguration();

    /**
     * Method to register alert connector with SRS
     *
     * @param request an instance of type {@link SRSConnectorSettings}
     * @return An object of type {@link String}
     */
    String register(SRSConnectorSettings request);
    
    /**
     * Method to to suspend the alerts for specified amount of time
     *
     *@param duration - Time in hours for which alerts should be suspended
     * @return An object of type {@link String}
     */
    String suspend(String duration);
    
    /**
     * Method to de register SRS Connector
     *
     * @return An object of type {@link Response}
     */
    String deregister(String connectionType);
    
    /**
     * Method to to enable SNMP and IPMI
     *
     * @return An object of type {@link Response}
     */
    Response enableSnmpAndIpmi();
    
}
