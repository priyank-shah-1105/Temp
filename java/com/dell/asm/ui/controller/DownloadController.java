package com.dell.asm.ui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.LogServiceAdapter;
import com.dell.asm.ui.adapter.service.NetworkServiceAdapter;
import com.dell.asm.ui.adapter.service.TemplateServiceAdapter;
import com.dell.asm.ui.asynchronous.deployment.DeploymentDeviceListCallable;
import com.dell.asm.ui.asynchronous.deployment.model.DeviceInfo;
import com.dell.asm.ui.asynchronous.networking.NetworkIpAddressListCallable;
import com.dell.asm.ui.download.DownloadFileManager;
import com.dell.asm.ui.download.DownloadFileManager.DownloadFile;
import com.dell.asm.ui.download.DownloadFileManager.DownloadFile.DownloadFileStatus;
import com.dell.asm.ui.download.DownloadType;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.download.JobDownloadRequest;
import com.dell.asm.ui.model.download.UIDownload;
import com.dell.asm.ui.model.network.UIStaticIPAddressDetails;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.identitypool.api.network.model.IpRange;
import com.dell.pg.asm.identitypool.api.network.model.Network;
import com.dell.pg.asm.identitypool.api.network.model.StaticNetworkConfiguration;
import com.dell.pg.asm.identitypool.api.network.model.UsageIdList;

@RestController
@RequestMapping(value = "/downloads/")
public class DownloadController extends BaseController {

    private static final Logger LOG = Logger.getLogger(DownloadController.class);

    private static final String NETWORK_ROW_TEMPLATE = "\"{0}\",\"{1}\",\"{2}\",\"{3}\",\"{4}\",\"{5}\",\"{6}\",\"{7}\",\"{8}\",\"{9}\",\"{10}\",\"{11}\",\"{12}\",\"{13}\"";
    private static final String IPADDRESS_ROW_TEMPLATE = "\"{0}\",\"{1}\",\"{2}\",\"{3}\",\"{4}\",\"{5}\",\"{6}\"";

    private LogServiceAdapter logServiceAdapter;
    private DeploymentServiceAdapter deploymentServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private TemplateServiceAdapter templateServiceAdapter;
    private NetworkServiceAdapter networkServiceAdapter;

    private ApplicationContext context;

    @Autowired
    public DownloadController(LogServiceAdapter logServiceAdapter,
                              DeploymentServiceAdapter deploymentServiceAdapter,
                              DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                              TemplateServiceAdapter templateServiceAdapter,
                              NetworkServiceAdapter networkServiceAdapter,
                              ApplicationContext context) {
        this.logServiceAdapter = logServiceAdapter;
        this.deploymentServiceAdapter = deploymentServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.templateServiceAdapter = templateServiceAdapter;
        this.networkServiceAdapter = networkServiceAdapter;
        this.context = context;
    }

    /**
     *
     * @param downloadType
     * @throws Exception
     */
    @RequestMapping(value = "create/{type}", method = RequestMethod.GET)
    public JobResponse handleCreateDownloadFileRest(
            @PathVariable("type") final DownloadType downloadType,
            final HttpServletRequest request) throws Exception {
        return createDownloadFile(downloadType, request, null);
    }

    /**
     *
     * @param downloadRequest
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "create", method = RequestMethod.POST)
    public JobResponse handleCreateDownloadFileRequest(
            @RequestBody JobDownloadRequest downloadRequest,
            final HttpServletRequest request) {
        return createDownloadFile(
                DownloadType.findFromUrlIgnoreCase(downloadRequest.requestObj.type), request,
                downloadRequest);
    }

    private JobResponse createDownloadFile(final DownloadType downloadType,
                                           final HttpServletRequest request,
                                           final JobDownloadRequest downloadRequest) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = new UIDownload();
        try {
            if (downloadType == null) {
                throw new IllegalArgumentException("Download type cannot be null!");
            }
            final DownloadFile downloadFile = DownloadFileManager.addDownloadFile(downloadType,
                                                                                  downloadType.getFileNameWithCurrentTimeStamp());

            final SecurityContext currentcontext = SecurityContextHolder.getContext();
            // start file export
            final Thread exportToFileThread = new Thread(() -> {

                SecurityContextHolder.setContext(currentcontext);
                try {
                    switch (downloadType) {
                    case LOGS:
                        logServiceAdapter.exportAllUserLogMessages(
                                downloadFile.getDownloadPath().toFile());
                        break;
                    case SERVICES:
                        deploymentServiceAdapter.exportAllDeployments(
                                downloadFile.getDownloadPath().toFile());
                        break;
                    case TEMPLATES:
                        templateServiceAdapter.exportAllTemplates(
                                downloadFile.getDownloadPath().toFile());
                        break;
                    case NETWORKS:
                        networkServiceAdapter.exportAllNetworks(
                                downloadFile.getDownloadPath().toFile());
                        break;
                    case NETWORK_DETAILS:
                        createNetworkDetailsFile(downloadFile.getDownloadPath().toFile(),
                                                 downloadRequest.requestObj.id, request);
                        break;
                    case DEVICES:
                        deviceInventoryServiceAdapter.exportAllDevices(
                                downloadFile.getDownloadPath().toFile());
                        break;
                    default:
                        throw new IllegalArgumentException(
                                String.format("DownloadType %s is not implemented!",
                                              downloadType));
                    }
                    downloadFile.setStatus(DownloadFileStatus.READY);
                } catch (Throwable t) {
                    LOG.error(t);
                    downloadFile.setStatus(DownloadFileStatus.ERROR);
                }
            });
            exportToFileThread.setDaemon(true);
            exportToFileThread.start();

            ((UIDownload) jobResponse.responseObj).id = downloadFile.getDownloadFileKey().toString();
            ((UIDownload) jobResponse.responseObj).status = downloadFile.getStatus().name();
        } catch (final Throwable t) {
            LOG.error("Error creating download file to tmp location", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     *
     * @param downloadId
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "status/{id}", method = RequestMethod.GET)
    public JobResponse handleDownloadFileStatusRest(@PathVariable("id") final UUID downloadId,
                                                    final HttpServletRequest request,
                                                    final HttpServletResponse response) throws Exception {
        return handleDownloadFileStatus(downloadId);
    }

    /**
     *
     * @param downloadRequest
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "status", method = RequestMethod.POST)
    public JobResponse handleDownloadFileStatusRequest(
            @RequestBody JobDownloadRequest downloadRequest,
            final HttpServletRequest request) throws Exception {
        return handleDownloadFileStatus(UUID.fromString(downloadRequest.requestObj.id));
    }

    private JobResponse handleDownloadFileStatus(final UUID downloadId) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = new UIDownload();
        try {
            final DownloadFile downloadFile = DownloadFileManager.getDownloadFile(downloadId);
            if (downloadFile != null) {
                ((UIDownload) jobResponse.responseObj).id = downloadFile.getDownloadFileKey().toString();
                ((UIDownload) jobResponse.responseObj).status = downloadFile.getStatus().name();
            }
        } catch (final Throwable t) {
            LOG.error("Error gettting download status", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     *
     * @param downloadId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "getfile/{id}", method = RequestMethod.GET)
    public void handleDownloadFileRest(@PathVariable("id") final UUID downloadId,
                                       final HttpServletRequest request,
                                       final HttpServletResponse response) throws Exception {
        handleDownloadFile(downloadId, response);
    }

    /**
     *
     * @param downloadRequest
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "getfile", method = RequestMethod.POST)
    public void handleDownloadFileRequest(@RequestBody JobDownloadRequest downloadRequest,
                                          final HttpServletResponse response) {
        handleDownloadFile(UUID.fromString(downloadRequest.requestObj.id), response);
    }

    private void handleDownloadFile(final UUID downloadId, final HttpServletResponse response) {
        ServletOutputStream responseOutputStream = null;
        try {
            responseOutputStream = response.getOutputStream();

            final DownloadFile downloadFile = DownloadFileManager.getDownloadFile(downloadId);
            File dFile;
            FileInputStream downloadFileInputStream;
            if (downloadFile != null) {
                dFile = downloadFile.getDownloadPath().toFile();
                downloadFileInputStream = new FileInputStream(dFile);

                response.setStatus(Response.SC_OK);
                response.setHeader("Content-Disposition",
                                   "attachment; filename=\"" + dFile.getName() + "\"");
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                response.setContentLength(
                        IOUtils.copyAndCloseInput(downloadFileInputStream, responseOutputStream));
            }
        } catch (final Throwable t) {
            response.setStatus(Response.SC_INTERNAL_SERVER_ERROR);
            LOG.error("Error gettting download", t);
        } finally {
            if (responseOutputStream != null) {
                try {
                    responseOutputStream.flush();
                } catch (Exception ignore) {
                }
                try {
                    responseOutputStream.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    private void createNetworkDetailsFile(final File downloadFile, String networkId,
                                          HttpServletRequest request) {

        long currenttime = System.currentTimeMillis();

        final String networkFileName = "network.csv";
        final String ipAddressesFileName = "ipaddresses.csv";
        final String foldername = "networkDetails_" + currenttime;
        final String tmpDirectory = File.separator + "tmp" + File.separator + foldername + File.separator;

        Path tempFolder = Paths.get(tmpDirectory);
        Path networkFilePath = Paths.get(tmpDirectory + networkFileName);
        Path ipAddressesFilePath = Paths.get(tmpDirectory + ipAddressesFileName);

        if (Files.exists(tempFolder)) {
            try {
                Files.delete(tempFolder);
            } catch (IOException e) {
                LOG.error(e);
                throw new WebApplicationException(e,
                                                  javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

        try {
            Files.createDirectory(tempFolder);
        } catch (IOException e) {
            LOG.error(e);
            throw new WebApplicationException(e,
                                              javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
        }

        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        ZipOutputStream zipOutputStream = null;
        try {
            //Retrieve a list of deployment ids that use this network
            UsageIdList deploymentIds = networkServiceAdapter.getAllDeploymentIdsForNetwork(
                    networkId);
            Network network = networkServiceAdapter.getNetwork(networkId);

            // Retrieve the list of ip addresses on this static network asynchronously
            NetworkIpAddressListCallable refIdNetworkIpAddressList = new NetworkIpAddressListCallable(networkId,
                                                                                                 network.getType().name(),
                                                                                                 NetworkIpAddressListCallable.DEVICE_REF_SEARCH,
                                                                                                 deploymentIds,
                                                                                                 deviceInventoryServiceAdapter,
                                                                                                 networkServiceAdapter,
                                                                                                 SecurityContextHolder.getContext());
            Future refIdNetworkIpAddressesFuture = threadPool.submit(refIdNetworkIpAddressList);

            // Retrieve the list of ip addresses on this static network asynchronously
            NetworkIpAddressListCallable serviceTagNetworkIpAddressList = new NetworkIpAddressListCallable(networkId,
                                                                                                           network.getType().name(),
                                                                                                           NetworkIpAddressListCallable.DEVICE_SERVICE_TAG_SEARCH,
                                                                                                           deploymentIds,
                                                                                                           deviceInventoryServiceAdapter,
                                                                                                           networkServiceAdapter,
                                                                                                           SecurityContextHolder.getContext());
            Future serviceTagNetworkIpAddressesFuture = threadPool.submit(serviceTagNetworkIpAddressList);


            //Retrieve the list of in use ip addresses and the deployment and device information associated with them.
            DeploymentDeviceListCallable deviceList = new DeploymentDeviceListCallable(networkId,
                                                                                       deploymentIds,
                                                                                       deploymentServiceAdapter,
                                                                                       SecurityContextHolder.getContext());
            Future deviceListFuture = threadPool.submit(deviceList);



            if (network != null) {
                List<String> lines = new ArrayList<>();
                final Locale locale = request.getLocale();

                //adding headers for table.  using same values as table in ui so if ui is updated these are updated.
                lines.add(MessageFormat.format(NETWORK_ROW_TEMPLATE,
                                               context.getMessage("networks.table.name", null,
                                                                  locale),
                                               context.getMessage("networks.table.desc", null,
                                                                  locale),
                                               context.getMessage("networks.table.type", null,
                                                                  locale),
                                               context.getMessage("networks.table.vlan", null,
                                                                  locale),
                                               context.getMessage("networks.ipaddresssetting", null,
                                                                  locale),
                                               context.getMessage("networkdetails.subnetmask", null,
                                                                  locale),
                                               context.getMessage("networkdetails.gateway", null,
                                                                  locale),
                                               context.getMessage("networkdetails.primarydns", null,
                                                                  locale),
                                               context.getMessage("networkdetails.secondarydns",
                                                                  null, locale),
                                               context.getMessage("networkdetails.dnssuffix", null,
                                                                  locale),
                                               context.getMessage("networkdetails.lastupdatedby",
                                                                  null, locale),
                                               context.getMessage("networkdetails.datelastupdated",
                                                                  null, locale),
                                               context.getMessage("networkdetails.createdby", null,
                                                                  locale),
                                               context.getMessage("networkdetails.datecreated",
                                                                  null, locale)));
                StaticNetworkConfiguration staticConfig = network.isStatic() ? network.getStaticNetworkConfiguration() : null;

                String networkType = com.dell.pg.asm.identitypoolmgr.network.util.NetworkType.fromValue(network.getType().name()).getLabel();;
                lines.add(MessageFormat.format(NETWORK_ROW_TEMPLATE,
                                               network.getName() != null ? network.getName() : StringUtils.EMPTY,
                                               network.getDescription() != null ? network.getDescription() : StringUtils.EMPTY,
                                               network.getType() != null ? networkType : StringUtils.EMPTY,
                                               network.getVlanId() != null ? network.getVlanId() : StringUtils.EMPTY,
                                               network.isStatic() ? "Static" : "DHCP",
                                               staticConfig != null && staticConfig.getSubnet() != null ? staticConfig.getSubnet() : StringUtils.EMPTY,
                                               staticConfig != null && staticConfig.getGateway() != null ? staticConfig.getGateway() : StringUtils.EMPTY,
                                               staticConfig != null && staticConfig.getPrimaryDns() != null ? staticConfig.getPrimaryDns() : StringUtils.EMPTY,
                                               staticConfig != null && staticConfig.getSecondaryDns() != null ? staticConfig.getSecondaryDns() : StringUtils.EMPTY,
                                               staticConfig != null && staticConfig.getDnsSuffix() != null ? staticConfig.getDnsSuffix() : StringUtils.EMPTY,
                                               network.getUpdatedBy() != null ? network.getUpdatedBy() : StringUtils.EMPTY,
                                               network.getUpdatedDate() != null ? MappingUtils.getTime(
                                                       network.getUpdatedDate()) : StringUtils.EMPTY,
                                               network.getCreatedBy() != null ? network.getCreatedBy() : StringUtils.EMPTY,
                                               network.getCreatedDate() != null ? MappingUtils.getTime(
                                                       network.getCreatedDate()) : StringUtils.EMPTY));

                if (Files.exists(networkFilePath)) {
                    Files.delete(networkFilePath);
                }

                Files.write(networkFilePath, lines, Charset.forName("UTF-8"));

                if (network.isStatic() && network.getStaticNetworkConfiguration() != null) {
                    Map<String, IpRange> ipRanges = new HashMap<>();
                    StaticNetworkConfiguration staticNetwork = network.getStaticNetworkConfiguration();
                    for (IpRange range : staticNetwork.getIpRange()) {
                        ipRanges.put(range.getId(), range);
                    }


                    Map<Long, UIStaticIPAddressDetails> staticIPAddressDetailsMap = (Map<Long, UIStaticIPAddressDetails>) refIdNetworkIpAddressesFuture.get();
                    //serviceTagNetworkIpAddressesFuture results must be added second always
                    staticIPAddressDetailsMap.putAll((Map<Long, UIStaticIPAddressDetails>) serviceTagNetworkIpAddressesFuture.get());
                    Map<Long, DeviceInfo> deviceInfoMap = (Map<Long, DeviceInfo>) deviceListFuture.get();

                    for (Map.Entry<Long, DeviceInfo> entry : deviceInfoMap.entrySet()) {
                        UIStaticIPAddressDetails details = staticIPAddressDetailsMap.get(entry.getKey());
                        DeviceInfo deviceInfo = entry.getValue();
                        if (details != null && deviceInfo != null) {
                            if (deviceInfo.getDeviceServiceTag() != null) {
                                details.deviceName = deviceInfo.getDeviceServiceTag();
                            }
                            if (deviceInfo.getDeploymentName() != null) {
                                details.serviceName = deviceInfo.getDeploymentName();
                            }
                        }
                    }

                    String name = network.getName() != null ? network.getName() : StringUtils.EMPTY;

                    lines = new ArrayList<>();
                    lines.add(MessageFormat.format(IPADDRESS_ROW_TEMPLATE,
                                                   context.getMessage("networks.table.name", null,
                                                                      locale),
                                                   context.getMessage("networks.startingipaddress",
                                                                      null, locale),
                                                   context.getMessage("networks.endingipaddress",
                                                                      null, locale),
                                                   context.getMessage("networkdetails.state", null,
                                                                      locale),
                                                   context.getMessage("networkdetails.ipaddress",
                                                                      null, locale),
                                                   context.getMessage(
                                                           "networkdetails.serviceusingipaddress",
                                                           null, locale),
                                                   context.getMessage(
                                                           "networkdetails.resourceusingipaddress",
                                                           null, locale)));

                    for (UIStaticIPAddressDetails details : staticIPAddressDetailsMap.values()) {
                        IpRange range = ipRanges.get(details.ipRangeId);
                        String start = StringUtils.EMPTY;
                        String end = StringUtils.EMPTY;
                        if (range != null) {
                            start = range.getStartingIp() != null ? range.getStartingIp() : StringUtils.EMPTY;
                            end = range.getEndingIp() != null ? range.getEndingIp() : StringUtils.EMPTY;
                        }
                        lines.add(MessageFormat.format(IPADDRESS_ROW_TEMPLATE,
                                                       name,
                                                       start,
                                                       end,
                                                       details.state.toUpperCase(),
                                                       details.ipAddress,
                                                       details.serviceName != null ? details.serviceName : StringUtils.EMPTY,
                                                       details.deviceName != null ? details.deviceName : StringUtils.EMPTY));
                    }

                    if (Files.exists(ipAddressesFilePath)) {
                        Files.delete(ipAddressesFilePath);
                    }
                    Files.write(ipAddressesFilePath, lines, Charset.forName("UTF-8"));
                }

                zipOutputStream = new ZipOutputStream(new FileOutputStream(downloadFile));
                for (File file : tempFolder.toFile().listFiles()) {
                    if (file.isFile()) {
                        final ZipEntry zipEntry = new ZipEntry(
                                file.getPath().substring(tmpDirectory.length()));
                        zipOutputStream.putNextEntry(zipEntry);
                        try (FileInputStream inputStream = new FileInputStream(file)) {
                            IOUtils.copy(inputStream, zipOutputStream);
                        }
                        zipOutputStream.closeEntry();
                    }
                }
            }
        } catch (Throwable t) {
            LOG.error("Exception from service call", t);
            throw new WebApplicationException(t,
                                              javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    LOG.error("Exception closing output stream", e);
                }
            }
            if (threadPool != null) {
                threadPool.shutdown();
            }
            try {
                Files.walkFileTree(tempFolder, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file,
                                                     BasicFileAttributes attrs) throws IOException {

                        LOG.debug("Deleting file: " + file);
                        Files.deleteIfExists(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir,
                                                              IOException exc) throws IOException {

                        LOG.debug("Deleting dir: " + dir);
                        if (exc == null) {
                            Files.deleteIfExists(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw exc;
                        }
                    }

                });
            } catch (IOException e) {
                LOG.error("Exception removing temp directory output stream", e);
            }
        }
    }

}
