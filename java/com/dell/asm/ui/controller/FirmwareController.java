/**************************************************************************
 *   Copyright (c) 2010 - 2016 Dell Inc. All rights reserved.             *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.CompliantState;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.FirmwareComplianceReport;
import com.dell.asm.asmcore.asmmanager.client.firmware.BundleType;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareDeviceType;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareDeviceType.DeviceModelStorage;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareDeviceType.DeviceModelSwitch;
import com.dell.asm.asmcore.asmmanager.client.firmware.FirmwareRepository;
import com.dell.asm.asmcore.asmmanager.client.firmware.RepositoryState;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareBundle;
import com.dell.asm.asmcore.asmmanager.client.firmware.SoftwareComponent;
import com.dell.asm.asmcore.asmmanager.client.firmwarerepository.ESRSFile;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceInventoryServiceAdapter;
import com.dell.asm.ui.adapter.service.FirmwareRepositoryServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.SoftwareBundleServiceAdapter;
import com.dell.asm.ui.exception.InvalidUploadFileFormat;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.firmware.JobFirmwareBundleRequest;
import com.dell.asm.ui.model.firmware.JobFirmwarePackageRequest;
import com.dell.asm.ui.model.firmware.JobRemoveFirmwareBundleRequest;
import com.dell.asm.ui.model.firmware.UIFirmware;
import com.dell.asm.ui.model.firmware.UIFirmwareBundle;
import com.dell.asm.ui.model.firmware.UIFirmwarePackage;
import com.dell.asm.ui.model.firmware.UIFirmwareReport;
import com.dell.asm.ui.model.firmware.UIFirmwareReportDevice;
import com.dell.asm.ui.model.firmware.UIHierarchicalListItem;
import com.dell.asm.ui.model.firmware.UIRemoveFirmwareBundle;
import com.dell.asm.ui.model.firmware.UISoftware;
import com.dell.asm.ui.upload.UploadFileManager;
import com.dell.asm.ui.upload.UploadFileManager.UploadedFile;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.asm.ui.util.UploadFileUtil;

/**
 * The Class Firmware Controller.
 */
@RestController
@RequestMapping(value = "/firmware/")
public class FirmwareController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(FirmwareController.class);
    /*
     *  Used for uploading User Bundles
     */
    private final static String userBundlePathPrefix = "USERBUNDLES";
    private static final String RCM_PREFIX = "RCM/";
    private SoftwareBundleServiceAdapter softwareBundleServiceAdapter;
    private FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter;
    private DeviceInventoryServiceAdapter deviceInventoryServiceAdapter;
    private DeploymentServiceAdapter deploymentServiceAdapter;
    private String userBundlePath;
    private String userBundleHashMd5;

    @Autowired
    public FirmwareController(SoftwareBundleServiceAdapter softwareBundleServiceAdapter,
                              FirmwareRepositoryServiceAdapter firmwareRepositoryServiceAdapter,
                              DeviceInventoryServiceAdapter deviceInventoryServiceAdapter,
                              DeploymentServiceAdapter deploymentServiceAdapter) {
        this.softwareBundleServiceAdapter = softwareBundleServiceAdapter;
        this.firmwareRepositoryServiceAdapter = firmwareRepositoryServiceAdapter;
        this.deviceInventoryServiceAdapter = deviceInventoryServiceAdapter;
        this.deploymentServiceAdapter = deploymentServiceAdapter;
    }

    @RequestMapping(value = "getfirmwarereport", method = RequestMethod.POST)
    public JobResponse getFirmwareReport(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();

        try {
            UIFirmwareReport report = new UIFirmwareReport();
            boolean isEmbeddedRepo = false;
            final ArrayList<UIFirmwareReportDevice> devices = new ArrayList<>();
            if ("service".equals(request.requestObj.type)) {
                FirmwareComplianceReport[] serviceFirmwareCompReports = deploymentServiceAdapter.getFirmwareComplianceReport(
                        request.requestObj.id);
                if (serviceFirmwareCompReports != null && serviceFirmwareCompReports.length > 0) {
                    isEmbeddedRepo = serviceFirmwareCompReports[0].isEmbededRepo();
                    Arrays.sort(serviceFirmwareCompReports,
                                FirmwareComplianceReport.SORT_BY_TYPE_COMPLIANCE_AND_IP_ADDRESS);
                    for (FirmwareComplianceReport firmwareComplianceReport : serviceFirmwareCompReports) {
                        UIFirmwareReportDevice device = MappingUtils.getUIFirmwareDeviceComplianceReport(
                                firmwareComplianceReport);
                        devices.add(device);
                    }
                }
            } else { // it's for a device
                FirmwareComplianceReport firmwareComplianceReport = this.deviceInventoryServiceAdapter.getFirmwareComplianceReportForDevice(
                        request.requestObj.id);
                isEmbeddedRepo = firmwareComplianceReport.isEmbededRepo();
                UIFirmwareReportDevice device = MappingUtils.getUIFirmwareDeviceComplianceReport(
                        firmwareComplianceReport);
                devices.add(device);
            }
            report.setDevices(devices);
            setCollectiveFimwareCompliant(report, isEmbeddedRepo);
            jobResponse.responseObj = report;
        } catch (Throwable t) {
            log.error("getFirmwareReport() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private void setCollectiveFimwareCompliant(UIFirmwareReport report, boolean isEmbeddedRepo) {
        boolean collectivelyFirmwareCompliant = true;
        String collectivelyFirmwareCompliantText = "";
        if (report.getDevices() != null && report.getDevices().size() > 0) {
            int i = 0;
            while (collectivelyFirmwareCompliant && i < report.getDevices().size()) {
                if (!report.getDevices().get(i).firmwareCompliant) {
                    collectivelyFirmwareCompliant = false;
                    collectivelyFirmwareCompliantText = report.getDevices().get(i).repositoryName;
                }
                ++i;
            }
        }
        if (collectivelyFirmwareCompliant) {
            collectivelyFirmwareCompliantText = report.getDevices().get(0).repositoryName;
            report.setFirmwareCompliant(CompliantState.COMPLIANT.getLabel());
        } else if (isEmbeddedRepo) {
            report.setFirmwareCompliant(CompliantState.UPDATEREQUIRED.getLabel());
        } else {
            report.setFirmwareCompliant(CompliantState.NONCOMPLIANT.getLabel());
        }
        report.setFirmwareCompliantText(collectivelyFirmwareCompliantText);
    }


    @RequestMapping(value = "getfirmwarebundledevices", method = RequestMethod.POST)
    public JobResponse getFirmwareBundleDevices(@RequestBody JobIDRequest request) {
        log.debug("getFirmwareBundleDevices call");
        JobResponse jobResponse = new JobResponse();
        List<UIHierarchicalListItem> result = new ArrayList<>();
        List<UIListItem> itemListSwitch = new ArrayList<>();
        UIHierarchicalListItem item = new UIHierarchicalListItem();

        List<DeviceModelSwitch> s = Arrays.asList(FirmwareDeviceType.DeviceModelSwitch.values());
        for (DeviceModelSwitch swit : s) {
            UIListItem listItem = new UIListItem();
            listItem.id = swit.name();
            listItem.name = swit.toString();
            itemListSwitch.add(listItem);

        }

        UIHierarchicalListItem itemStorage = new UIHierarchicalListItem();
        List<UIListItem> itemListStorage = new ArrayList<>();

        List<DeviceModelStorage> storage;
        storage = Arrays.asList(FirmwareDeviceType.DeviceModelStorage.values());
        for (DeviceModelStorage stor : storage) {
            UIListItem listItem = new UIListItem();
            listItem.id = stor.name();
            listItem.name = stor.toString();
            itemListStorage.add(listItem);

        }
        itemStorage.children = itemListStorage;
        itemStorage.id = FirmwareDeviceType.DeviceTypes.Storage.name();
        itemStorage.name = FirmwareDeviceType.DeviceTypes.Storage.name();
        item.children = itemListSwitch;
        item.id = FirmwareDeviceType.DeviceTypes.Switch.name();
        item.name = FirmwareDeviceType.DeviceTypes.Switch.name();
        result.add(itemStorage);
        result.add(item);

        jobResponse.responseObj = result;
        return jobResponse;
    }

    @RequestMapping(value = "removefirmwarebundle", method = RequestMethod.POST)
    public JobResponse removeFirmwareBundle(@RequestBody JobRemoveFirmwareBundleRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            UIRemoveFirmwareBundle fwBundle = request.requestObj;
            String id = fwBundle.bundleId;
            softwareBundleServiceAdapter.deleteSoftwareBundle(id);
        } catch (Throwable t) {
            log.error("removeFirmwareBundle() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;

    }

    @RequestMapping(value = "savefirmwarebundle", method = RequestMethod.POST)
    public JobResponse saveFirmwareBundle(MultipartHttpServletRequest request,
                                          HttpServletResponse response) {
        JobResponse jobResponse = new JobResponse();
        try {
            SoftwareBundle softwareBundle = new SoftwareBundle();

            // Assumption is that any bundle being saved via this interface is a user bundle.
            softwareBundle.setUserBundle(true);

            softwareBundle.setFwRepositoryId(request.getParameter("packageId"));
            softwareBundle.setName(request.getParameter("bundleName"));
            softwareBundle.setDescription(request.getParameter("bundleDescription"));
            softwareBundle.setVersion(request.getParameter("bundleVersion"));
            softwareBundle.setDeviceType(request.getParameter("deviceType"));
            softwareBundle.setDeviceModel(request.getParameter("deviceModel"));
            softwareBundle.setId(request.getParameter("bundleId"));
            softwareBundle.setCriticality(request.getParameter("criticality"));

            // Try to get a file upload
            //
            MultipartFile file = request.getFile("file");

            // Move uploaded file to firmware repository location
            //
            if (file != null) {
                saveUploadedFileToBundle(file, softwareBundle);
            } else {
                // Default magic string indicating no file upload
                this.userBundlePath = "VxFMNoOp";
                this.userBundleHashMd5 = null;
            }

            SoftwareBundle bundle = new SoftwareBundle();
            if (softwareBundle.getId() == null || softwareBundle.getId().isEmpty()) {
                //  Alwways set this on create.
                softwareBundle.setUserBundlePath(userBundlePath);
                softwareBundle.setUserBundleHashMd5(userBundleHashMd5);
                bundle = softwareBundleServiceAdapter.addSoftwareBundle(softwareBundle);
            } else {
                //  Only set this path if file was uploaded
                if (file != null) {
                    softwareBundle.setUserBundlePath(userBundlePath);
                    softwareBundle.setUserBundleHashMd5(userBundleHashMd5);
                }
                softwareBundleServiceAdapter.updateSoftwareBundle(softwareBundle.getId(),
                                                                  softwareBundle);
            }

            if (bundle != null) {
                jobResponse.responseObj = "Success!";
                jobResponse.responseCode = 0;
            }

        } catch (Throwable t) {
            log.error("savefirmwarebundle() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Get firmware package by ID.
     *
     * @param request
     *            the request
     * @return device
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getfirmwarepackagebyid", method = RequestMethod.POST)
    public JobResponse getFirmwarePackagebyId(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            FirmwareRepository fw = firmwareRepositoryServiceAdapter.getById(request.requestObj.id,
                                                                             true, false);
            UIFirmwarePackage uiPackage = null;
            if (fw != null) {
                uiPackage = new UIFirmwarePackage();
                uiPackage.name = fw.getName();
                if (fw.getCreatedDate() != null) {
                    uiPackage.created = fw.getCreatedDate().toString();
                }
                if (fw.getUpdatedDate() != null) {
                    uiPackage.updated = fw.getUpdatedDate().toString();
                }
                uiPackage.id = fw.getId();
                uiPackage.services = new ArrayList<>();
                uiPackage.state = fw.getState().toString();

                Set<SoftwareBundle> bundles = fw.getSoftwareBundles();

                if (bundles != null) {
                    uiPackage.bundles = bundles.size();
                    int scTotal = 0;
                    for (SoftwareBundle bundle : bundles) {
                        UIFirmwareBundle uiBundle = new UIFirmwareBundle();

                        uiBundle.id = bundle.getId();
                        if (bundle.getBundleDate() != null) {
                            uiBundle.bundleDate = bundle.getBundleDate().toString();
                        }
                        uiBundle.bundleName = bundle.getName();
                        uiBundle.bundleDescription = bundle.getDescription();
                        uiBundle.bundleVersion = bundle.getVersion();
                        uiBundle.firmwarecomponents = new ArrayList<>();
                        uiBundle.deviceType = bundle.getDeviceType();
                        uiBundle.deviceModel = bundle.getDeviceModel();

                        if (bundle.getSoftwareComponents() != null) {
                            scTotal += bundle.getSoftwareComponents().size();
                            for (SoftwareComponent sc : bundle.getSoftwareComponents()) {
                                if (BundleType.SOFTWARE.equals(bundle.getBundleType())) {
                                    UISoftware uisw = new UISoftware();
                                    uisw.id = sc.getId();
                                    uisw.softwarename = sc.getName();
                                    uisw.vendor = sc.getVendorId();
                                    uisw.softwaretype = sc.getCategory();
                                    uisw.softwareversion = sc.getVendorVersion();
                                    uisw.criticality = bundle.getCriticality();
                                    /**
                                     *  For User Bundles we need to add the filename so it will be shown on view details page
                                     */
                                    if (bundle.getUserBundle()) {
                                        uisw.filename = sc.getPath();
                                    }
                                    if (sc.getUpdatedDate() != null) {
                                        uisw.softwarelastupdatetime = MappingUtils.getTime(
                                                sc.getUpdatedDate());
                                    } else if (bundle.getBundleDate() != null) {
                                        uisw.softwarelastupdatetime = MappingUtils.getTime(
                                                bundle.getBundleDate());
                                    }

                                    uiBundle.softwarecomponents.add(uisw);
                                } else {
                                    UIFirmware uifw = new UIFirmware();
                                    uifw.id = sc.getId();
                                    uifw.firmwarename = sc.getName();
                                    uifw.firmwaretype = sc.getCategory();
                                    uifw.firmwareversion = sc.getVendorVersion();
                                    uifw.criticality = bundle.getCriticality();
                                    /**
                                     *  For User Bundles we need to add the filename so it will be shown on view details page
                                     */
                                    if (bundle.getUserBundle()) {
                                        uifw.filename = sc.getPath();
                                    }
                                    if (sc.getUpdatedDate() != null) {
                                        uifw.firmwarelastupdatetime = MappingUtils.getTime(
                                                sc.getUpdatedDate());
                                    } else if (bundle.getBundleDate() != null) {
                                        uifw.firmwarelastupdatetime = MappingUtils.getTime(
                                                bundle.getBundleDate());
                                    }

                                    uiBundle.firmwarecomponents.add(uifw);
                                }
                            }
                        }
                        Collections.sort(uiBundle.firmwarecomponents);
                        Collections.sort(uiBundle.softwarecomponents);

                        /**
                         *  The UI currently gets the filename from the software component above.
                         *  However, we also add it to the bundle for symmetry.
                         */
                        uiPackage.components = scTotal;
                        if (bundle.getUserBundle()) {
                            uiBundle.filename = bundle.getUserBundlePath();
                            // Add to user bundles collection
                            uiPackage.userbundles.add(uiBundle);
                        } else if (BundleType.SOFTWARE == bundle.getBundleType()) {
                            uiPackage.softwarebundles.add(uiBundle);
                        } else {
                            uiPackage.firmwarebundles.add(uiBundle);
                        }
                    }
                }

                Collections.sort(uiPackage.userbundles);
                Collections.sort(uiPackage.firmwarebundles);
                Collections.sort(uiPackage.softwarebundles);

            }
            jobResponse.responseObj = uiPackage;

        } catch (Throwable t) {
            log.error("getFirmwarePackagebyId() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Gets the list of Available RCM on ESRS.
     *
     * @return the list of available firmware packages ( repositories )
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getavailablercms", method = RequestMethod.POST)
    public JobResponse getAvailableRCMs() {
        JobResponse jobResponse = new JobResponse();
        List<UIListItem> list = new ArrayList<>();

        try {
            List<ESRSFile> repos = firmwareRepositoryServiceAdapter.getESRSRepositories();
            if (CollectionUtils.isNotEmpty(repos)) {
                repos.stream().filter(repo -> repo.getFileName().startsWith(RCM_PREFIX))
                .forEach(repo -> list.add(new UIListItem(repo.getFileName(), repo.getFileName())));
            }
            jobResponse.responseObj = list;
        } catch (Throwable t) {
            log.error("getAvailableRCMs() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Gets the list of Available firmware packages.
     *
     * @return the list of available firmware packages ( repositories )
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getavailablefirmwarepackages", method = RequestMethod.POST)
    public JobResponse getAvailableFirmwarePackages(@RequestBody JobRequest request) {
        return getFirmwarePackagesInternal(request, true);
    }

    /**
     * Gets the list of ALL firmware packages.
     *
     * @return the device list
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getfirmwarepackages", method = RequestMethod.POST)
    public JobResponse getFirmwarePackages(@RequestBody JobRequest request) {
        return getFirmwarePackagesInternal(request, false);
    }

    private JobResponse getFirmwarePackagesInternal(@RequestBody JobRequest request,
                                                    boolean filterOnState) {
        JobResponse jobResponse = new JobResponse();
        List<UIFirmwarePackage> responseObj = new ArrayList<>();

        try {
            jobResponse.criteriaObj = request.criteriaObj;
            ResourceList<FirmwareRepository> packages = firmwareRepositoryServiceAdapter.getAll(null,
                                                                                                null,
                                                                                                null,
                                                                                                null);
            if (packages != null) {
                for (FirmwareRepository repo : packages.getList()) {
                    boolean add = false;
                    if (!repo.isEmbedded()) {
                        if (!filterOnState || (filterOnState && RepositoryState.AVAILABLE.equals(repo.getState()))) {
                            add = true;
                        }
                    }
                    if (add) {
                        responseObj.add(new UIFirmwarePackage(repo));
                    }
                }
            }
            responseObj.sort(Comparator.comparing(fp -> fp.name));
        } catch (Throwable t) {
            log.error("getFirmwarePackages() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Get firmware bundle by package and bundle ID.
     *
     * @param request
     *            the request
     * @return device
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getfirmwarebundlebyid", method = RequestMethod.POST)
    public JobResponse getFirmwareBundlebyId(@RequestBody JobFirmwareBundleRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {

            UIFirmwareBundle uiBundle = new UIFirmwareBundle();
            jobResponse.responseObj = uiBundle;

            SoftwareBundle bundle = firmwareRepositoryServiceAdapter.getBundleById(
                    request.requestObj.firmwareBundleId);
            if (bundle != null) {
                uiBundle.id = bundle.getId();
                if (bundle.getBundleDate() != null) {
                    uiBundle.bundleDate = bundle.getBundleDate().toString();
                }
                uiBundle.bundleName = bundle.getName();
                uiBundle.bundleDescription = bundle.getName();
                uiBundle.bundleVersion = bundle.getVersion();
                uiBundle.firmwarecomponents = new ArrayList<>();

                HashSet<String> seenSC = new HashSet<>();

                //Our Software component does not map directly to catalog software components.
                //Our Software component is a combination of supported device and software component to enable ease of access in the application workflow.
                for (SoftwareComponent sc : bundle.getSoftwareComponents()) {
                    String constraint = sc.getName() + sc.getCategory() + sc.getVendorVersion();
                    if (!seenSC.contains(constraint)) {
                        if (BundleType.SOFTWARE == bundle.getBundleType()) {
                            UISoftware uisw = new UISoftware();
                            uisw.id = sc.getId();
                            uisw.softwarename = sc.getName();
                            uisw.softwaretype = sc.getCategory();
                            uisw.softwareversion = sc.getVendorVersion();
                            uisw.criticality = bundle.getCriticality();
                            uisw.vendor = sc.getVendorId();
                            if (sc.getUpdatedDate() != null) {
                                uisw.softwarelastupdatetime = MappingUtils.getTime(
                                        sc.getUpdatedDate());
                            } else if (bundle.getBundleDate() != null) {
                                uisw.softwarelastupdatetime = MappingUtils.getTime(
                                        bundle.getBundleDate());
                            }

                            uiBundle.softwarecomponents.add(uisw);
                        } else {
                            UIFirmware uifw = new UIFirmware();
                            uifw.id = sc.getId();
                            uifw.firmwarename = sc.getName();
                            uifw.firmwaretype = sc.getCategory();
                            uifw.firmwareversion = sc.getVendorVersion();
                            if (sc.getUpdatedDate() != null) {
                                uifw.firmwarelastupdatetime = MappingUtils.getTime(
                                        sc.getUpdatedDate());
                            } else if (bundle.getBundleDate() != null) {
                                uifw.firmwarelastupdatetime = MappingUtils.getTime(
                                        bundle.getBundleDate());
                            }

                            uiBundle.firmwarecomponents.add(uifw);
                        }
                        seenSC.add(constraint);
                    }
                }
            }
        } catch (Throwable t) {
            log.error("getFirmwareBundlebyId() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Get default firmware package.
     *
     * @param request
     *            empty request
     * @return device
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getdefaultfirmwarepackage", method = RequestMethod.POST)
    public JobResponse getDefaultFirmwarePackagebyId(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            ResourceList<FirmwareRepository> fwRepos = firmwareRepositoryServiceAdapter.getAll(null,
                                                                                               null,
                                                                                               null,
                                                                                               null);
            jobResponse.responseObj = getDefaultRepository(fwRepos);
        } catch (Throwable t) {
            log.error("getDefaultFirmwarePackagebyId() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "uploadfirmwarepackage", method = RequestMethod.POST)
    public JobResponse uploadFirmwarePackage(@RequestParam("file") MultipartFile multipartFile) {

        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = new UIFirmwarePackage();

        if (!multipartFile.isEmpty()) {
            try {
                // NOTE[fcarta] - In reference to ASM-3813 - DELL-14080-1-8: Arbitrary File Upload to /tmp Directory
                // The following will fail for MockMultipartFile - Most likely only affects tests :(
                if (!(multipartFile instanceof CommonsMultipartFile)) {
                    throw new IllegalArgumentException(
                            "Only CommonsMultipartFile file uploads are supported at this time!");
                }
                // NOTE[fcarta] - In reference to ASM-3813 - DELL-14080-1-8: Arbitrary File Upload to /tmp Directory
                // Parse the upload to make sure it is well-formed xml 
                final FileItem uploadFile = ((CommonsMultipartFile) multipartFile).getFileItem();
                if (!UploadFileUtil.isUploadWellFormedXml(uploadFile.getInputStream())) {
                    throw new InvalidUploadFileFormat(
                            "Firmware uploads only support well-formed xml files at this time!");
                }
                // NOTE[fcarta] - In reference to ASM-3813 - DELL-14080-1-8: Arbitrary File Upload to /tmp Directory
                // Store FileItem in the UploadManager so we can keep it in memory/disk until the next request
                final UploadedFile uploadedFile = UploadFileManager.addUploadedFile(uploadFile);

                // not setting it to true filename here because used for display
                ((UIFirmwarePackage) jobResponse.responseObj).packageSource = multipartFile.getName();
                // NOTE[fcarta] - In reference to ASM-3813 - DELL-14080-1-8: Arbitrary File Upload to /tmp Directory
                // Instead of returning the path to where the upload is stored on disk use the packageLocation for 
                // passing the key to the uploaded file in the UploadFileManager back to the UI
                //((UIFirmwarePackage) jobResponse.responseObj).packageLocation = tmpFile.getCanonicalPath();
                ((UIFirmwarePackage) jobResponse.responseObj).packageLocation =
                        uploadedFile.getUploadFileKey().toString();
                ((UIFirmwarePackage) jobResponse.responseObj).id = UUID.randomUUID().toString();
            } catch (Exception e) {
                ((UIFirmwarePackage) jobResponse.responseObj).packageSource = "ERROR";
                log.error("Error uploading file to tmp location", e);
                jobResponse = addFailureResponseInfo(e);
            }

        } else {
            ((UIFirmwarePackage) jobResponse.responseObj).packageSource = "ERROR empty file";
        }
        return jobResponse;
    }

    /**
     * Get firmware bundle by package and bundle ID.
     *
     * @param request
     *            the request
     * @return device
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "savefirmwarepackage", method = RequestMethod.POST)
    public JobResponse saveFirmwarePackage(@RequestBody JobFirmwarePackageRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            jobResponse.responseObj =
                    firmwareRepositoryServiceAdapter.create(
                            createFirmwareFromRequest(request.requestObj));
        } catch (Throwable t) {
            log.error("saveFirmwarePackage() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private FirmwareRepository createFirmwareFromRequest(UIFirmwarePackage uiFirmwarePackage) {
        FirmwareRepository firmwareRepository = new FirmwareRepository();
        if (StringUtils.isNotBlank(uiFirmwarePackage.id)) {
            firmwareRepository.setId(uiFirmwarePackage.id);
        }
        firmwareRepository.setDefaultCatalog(uiFirmwarePackage.defaultpackage);
        switch (uiFirmwarePackage.packageSource) {
        case "network":
            firmwareRepository.setUsername(uiFirmwarePackage.username);
            firmwareRepository.setPassword(uiFirmwarePackage.password);
            firmwareRepository.setSourceLocation(uiFirmwarePackage.filepath);
            break;
        case "file":
            try {
                // NOTE[fcarta] - In reference to ASM-3813 - DELL-14080-1-8: Arbitrary File Upload to /tmp Directory
                // Get the upload file key from the UI in packageLocation. Use this key to get the file path and name
                // from the upload file manager
                final UUID uploadedFileKey = UUID.fromString(uiFirmwarePackage.packageLocation);
                final UploadedFile uploadedFile = UploadFileManager.getUploadedFile(
                        uploadedFileKey);

                if (uploadedFile != null) {
                    firmwareRepository.setDiskLocation(uploadedFile.getUploadPath().toString());
                    firmwareRepository.setFilename(
                            uploadedFile.getUploadPath().getFileName().toString());
                    firmwareRepository.setSourceLocation(FirmwareRepository.DISK);
                    firmwareRepository.setName(
                            uploadedFile.getUploadPath().getFileName().toString());
                }
            } catch (Exception e) {
                throw new RuntimeException("Error creating firmware from request", e);
            }
            break;
        case "import":
            firmwareRepository.setSourceLocation(FirmwareRepository.ASM_REPOSITORY_LOCATION);
            firmwareRepository.setFilename(uiFirmwarePackage.selectedRcm);
            break;
        }
        return firmwareRepository;
    }

    /**
     * Set default firmware package.
     *
     * @param request
     *            request
     * @return device
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "setdefaultfirmwarepackage", method = RequestMethod.POST)
    public JobResponse setDefaultFirmwarePackagebyId(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            String id = request.requestObj.id;
            FirmwareRepository repoToSetDefault = firmwareRepositoryServiceAdapter.getById(id);
            repoToSetDefault.setDefaultCatalog(true);
            firmwareRepositoryServiceAdapter.update(repoToSetDefault);
        } catch (Throwable t) {
            log.error("setDefaultFirmwarePackagebyId() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }


    /**
     * Set default firmware package.
     *
     * @param request
     *            request
     * @return device
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public JobResponse removePackage(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UIFirmwarePackage> responseObj = new ArrayList<>();
        try {
            for (String id : request.requestObj) {
                FirmwareRepository repoToDelete = firmwareRepositoryServiceAdapter.getById(id);

                if (!repoToDelete.isEmbedded()) {
                    firmwareRepositoryServiceAdapter.delete(id);
                }

                ResourceList<FirmwareRepository> packages = firmwareRepositoryServiceAdapter.getAll(
                        null, null, null, null);
                if (packages != null)
                    for (FirmwareRepository repo : packages.getList()) {
                        responseObj.add(new UIFirmwarePackage(repo));
                    }
                jobResponse.responseObj = responseObj;
            }
        } catch (Throwable t) {
            log.error("removeDevices() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Get firmware package name by ID.
     *
     * @param request
     *            the request
     * @return device
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getfirmwarepackagenamebyid", method = RequestMethod.POST)
    public JobResponse getFirmwarePackageNameById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            FirmwareRepository fr = firmwareRepositoryServiceAdapter.getById(request.requestObj.id);

            if (fr != null)
                jobResponse.responseObj = fr.getName();

        } catch (Throwable t) {
            log.error("getFirmwarePackageNameById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Test repository.
     *
     * @param request
     *            the request
     * @return device
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "testfirmwarepackage", method = RequestMethod.POST)
    public JobResponse testRepository(@RequestBody JobFirmwarePackageRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            firmwareRepositoryServiceAdapter.testConnection(
                    createFirmwareFromRequest(request.requestObj));
        } catch (Throwable t) {
            log.error("Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Utility method to copy uploaded file to user bundle directory.
     *  This has the side effect of calculating and setting the instance variables:
     *   userBundlePath and userBundleHashMd5
     */
    private void saveUploadedFileToBundle(MultipartFile file,
                                          SoftwareBundle softwareBundle) throws IOException {

        String userBundleName = softwareBundle.getName();

        // Replace spaces with underscores in bundle name part of directory path
        String userBundleDir = userBundlePathPrefix + File.separator + userBundleName.replace(' ',
                                                                                              '_');

        // Build userBundlePath as <user_bundle_prefix>/<bundle_name>/<uploaded file name>
        String userBundlePath = userBundleDir + File.separator + file.getOriginalFilename();

        try {
            // Fetch the FirmwareRepository so we can find where to copy the file
            String repositoryId = softwareBundle.getFwRepositoryId();
            FirmwareRepository fw = firmwareRepositoryServiceAdapter.getById(repositoryId);

            // Build the absolute path to where we want to copy
            String absRepoDir = fw.getDiskLocation();
            String absBundleDir = absRepoDir + File.separator + userBundleDir;
            String absBundlePath = absRepoDir + File.separator + userBundlePath;

            // Assert the directory exists
            File targetDir = new File(absBundleDir);
            if (!(targetDir.exists() && targetDir.isDirectory())) {
                targetDir.getCanonicalFile().mkdirs();
            } else {
                throw new IOException(
                        "Failed to create directory for User bundle: " + targetDir.getCanonicalPath());
            }

            log.debug("Copying user bundle upload file to: " + absBundlePath);
            File newFile = new File(
                    targetDir.getCanonicalFile() + File.separator + file.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(newFile);

            /*
             *  Need to compute an MD5 hash on the uploaded file
             *  Use input stream decorator, java.security.DigestInputStream
             */
            MessageDigest md5Digest;
            md5Digest = MessageDigest.getInstance("MD5");
            DigestInputStream dis = new DigestInputStream(file.getInputStream(), md5Digest);

            IOUtils.copy(dis, fos);

            // Convert byte array to BigInt using sign,magnitude
            BigInteger bi = new BigInteger(1, md5Digest.digest());
            // Convert BigInt to hex string
            this.userBundleHashMd5 = bi.toString(16);
            this.userBundlePath = userBundlePath;

            fos.close();
            dis.close();
        } catch (Exception e) {
            log.error("Caught exception while copying uploaded file to user bundle", e);
            throw new IOException(e.getMessage());
        } finally {
            IOUtils.closeQuietly(file.getInputStream());
        }
    }

    /**
     * Look up the default firmware repository.
     */
    private FirmwareRepository getDefaultRepository(ResourceList<FirmwareRepository> fwRepos) {
        // TODO Use the filter argument when/if filtering and pagination is implemented for fw repos
        // Currently this is not an issue as this is only used on demand when a user views the 
        // compliance report.  This is not called when the resource page refreshes
        for (FirmwareRepository fwRepo : fwRepos.getList()) {
            if (fwRepo.isDefaultCatalog()) {
                return fwRepo;
            }
        }
        return null;
    }
}
