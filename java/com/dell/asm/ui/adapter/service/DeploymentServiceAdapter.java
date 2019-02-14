package com.dell.asm.ui.adapter.service;

import java.io.File;
import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.deployment.DeploymentFilterResponse;
import com.dell.asm.asmcore.asmmanager.client.deployment.PuppetLogEntry;
import com.dell.asm.asmcore.asmmanager.client.deployment.ServerNetworkObjects;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReport;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;

public interface DeploymentServiceAdapter {

    Deployment getDeployment(String deploymentId);

    ResourceList<Deployment> getDeployments(String sort,
                                            List<String> filter,
                                            Integer offset,
                                            Integer limit,
                                            Boolean full);

    ResourceList<Deployment> getDeploymentSummaries(String sort,
                                                    List<String> filter,
                                                    Integer offset,
                                                    Integer limit,
                                                    Boolean full);

    Deployment createDeployment(Deployment deployment);

    Deployment updateDeployment(String deploymentId, Deployment mgmtDeployment);

    Deployment cancelDeployment(String deploymentId, Deployment mgmtDeployment);

    Response deleteDeployment(String deploymentId, String serversInInventory, String serversManagedStat);

    ResourceList<PuppetLogEntry> getPuppetLogs(String deploymentId, String certName,
                                               List<String> filter);

    String getDeploymentLog(String deploymentId, String certName);

    ResourceList<Deployment> getDeploymentsFromDeviceId(String deviceId);

    void exportAllDeployments(final File downloadFile);

    DeploymentFilterResponse filterAvailableServers(ServiceTemplate template, int numDeployments);

    ServerNetworkObjects getServerNetworkObjects(String deploymentId, String serverComponentId);

    ResourceList<Deployment> getDeploymentsForNetworkId(String networkId);

    void deleteUsersFromDeployments(List<String> userIds);

    FirmwareComplianceReport[] getFirmwareComplianceReport(String deploymentId);

}