/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryConfiguration;
import com.dell.asm.ui.adapter.service.ActiveDirectoryConfigMgmtServiceAdapter;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.activedirectory.JobActiveDirectoryRequest;
import com.dell.asm.ui.model.activedirectory.UIActiveDirectory;
import com.dell.asm.ui.model.activedirectory.UIActiveDirectoryProtocolType;
import com.dell.asm.ui.model.activedirectory.UIActiveDirectoryType;
import com.dell.asm.ui.model.credential.UICredential;
import com.dell.asm.ui.util.ContextUtility;
import com.dell.asm.ui.util.MappingUtils;

/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood</a>
 *
 * Oct 31, 20132:43:47 PM
 */
@RestController
@RequestMapping(value = "/users/")
public class ActiveDirectoryConfigMgmtController extends BaseController {

    private static final Logger logger = Logger.getLogger(
            ActiveDirectoryConfigMgmtController.class);


    private ActiveDirectoryConfigMgmtServiceAdapter activeDirectoryConfigMgmtServiceAdapter;

    @Autowired
    public ActiveDirectoryConfigMgmtController(
            ActiveDirectoryConfigMgmtServiceAdapter activeDirectoryConfigMgmtServiceAdapter) {
        this.activeDirectoryConfigMgmtServiceAdapter = activeDirectoryConfigMgmtServiceAdapter;
    }

    @ModelAttribute
    public void setNoCache(HttpServletResponse servletResponse) {
        ContextUtility.setCacheControlHeaders(servletResponse);
    }

    /**
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getdirectorylist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDirectoryList(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIActiveDirectory> responseObj = new ArrayList<>();

        try {
            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_DIRECTORIES,
                                                                "username");
            ActiveDirectoryConfiguration[] activeDirectoryConfigurations = activeDirectoryConfigMgmtServiceAdapter.getActiveDirectoryConfigurations(
                    options.filterList, options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? null : options.limit, options.sortedColumns);
            List<ActiveDirectoryConfiguration> list = Arrays.asList(activeDirectoryConfigurations);

            if (CollectionUtils.isNotEmpty(list)) {
                for (ActiveDirectoryConfiguration ad : list) {
                    UIActiveDirectory uiAD = getJSONObject(ad);
                    responseObj.add(uiAD);
                }
            }
            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = list.size();
            }
        } catch (Throwable t) {
            logger.error("getDirectoryList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getdirectorybyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDirectoryById(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();
        UIActiveDirectory responseObj = null;
        try {
            responseObj = getJSONObject(
                    activeDirectoryConfigMgmtServiceAdapter.get(request.requestObj.id));

        } catch (Throwable t) {
            logger.error("getDirectoryById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "savedirectory", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse saveDirectory(@RequestBody JobActiveDirectoryRequest request) {

        // Comment out for production, this dumps a clear text password
        // logger.debug("saveDirectory() - JobRequest received with following " + request.toJSON());
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = request.requestObj;

        try {
            ActiveDirectoryConfiguration activeDirectoryConfiguration = getAPIObject(
                    request.requestObj);

            if (activeDirectoryConfiguration.getSeqId() != null) {
                activeDirectoryConfigMgmtServiceAdapter.update(
                        Long.toString(activeDirectoryConfiguration.getSeqId()),
                        activeDirectoryConfiguration);

            } else {

                activeDirectoryConfigMgmtServiceAdapter.create(activeDirectoryConfiguration);

            }

        } catch (Throwable t) {
            logger.error("saveDirectory() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        // Comment out for production, this dumps a clear text password
        // logger.debug("saveDirectory() - JobResponse sent with following " + jobResponse.toJSON());
        return jobResponse;
    }


    /**
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getdirectorytype", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDirectoryType(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();

        String DirectoryTypeAD = "mad";
        String DirectoryTypeADDisplay = "Microsoft Active Directory";

        List<UIActiveDirectoryType> responseObj = new ArrayList<>();
        responseObj.add(new UIActiveDirectoryType(DirectoryTypeAD, DirectoryTypeADDisplay));
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "getprotocoltype", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getProtocolType(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        String ProtocolPlain = "plain";
        String ProtocolSSL = "ssl";
        String ProtocolPlainDisplay = "Plain";
        String ProtocolSSLDisplay = "SSL";

        List<UIActiveDirectoryProtocolType> responseObj = new ArrayList<>();
        responseObj.add(new UIActiveDirectoryProtocolType(ProtocolPlain, ProtocolPlainDisplay));
        responseObj.add(new UIActiveDirectoryProtocolType(ProtocolSSL, ProtocolSSLDisplay));
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }


    /**
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "enabledisableDirectories", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse enabledisableDirectories(@RequestBody JobStringsRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            for (int i = 0; i < request.requestObj.size(); i++) {
                ActiveDirectoryConfiguration activeDirectoryConfiguration = activeDirectoryConfigMgmtServiceAdapter
                        .get(request.requestObj.get(i));
                String status = activeDirectoryConfiguration.getStatus();
                if ("active".equalsIgnoreCase(status)) {
                    activeDirectoryConfiguration.setStatus("inactive");
                } else {
                    activeDirectoryConfiguration.setStatus("active");
                }

                activeDirectoryConfigMgmtServiceAdapter.update(
                        Long.toString(activeDirectoryConfiguration.getSeqId()),
                        activeDirectoryConfiguration);
            }

        } catch (Throwable t) {
            logger.error("enabledisableDirectories() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     *
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "deleteaduser", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse deleteADUser(@RequestBody JobStringsRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            for (int i = 0; i < request.requestObj.size(); i++) {
                activeDirectoryConfigMgmtServiceAdapter.delete(request.requestObj.get(i));
            }
        } catch (Throwable t) {
            logger.error("deleteADUser() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }


    /**
     *
     * @param activeDirectoryConfiguration
     * @return
     */
    private UIActiveDirectory getJSONObject(
            ActiveDirectoryConfiguration activeDirectoryConfiguration) {
        UIActiveDirectory activeDirectory = new UIActiveDirectory();
        activeDirectory.serverName = activeDirectoryConfiguration.getName();
        activeDirectory.id = activeDirectoryConfiguration.getSeqId();

        activeDirectory.status = activeDirectoryConfiguration.getStatus();
        activeDirectory.directoryName = "Microsoft Active Directory";

        activeDirectory.bindDN = activeDirectoryConfiguration.getConnectionSetting().getBindDn();
        activeDirectory.hostName = activeDirectoryConfiguration.getConnectionSetting().getHostName();
        activeDirectory.method = activeDirectoryConfiguration.getConnectionSetting().getMethod();
        activeDirectory.password = UICredential.CURRENT_PASSWORD;
        activeDirectory.confirmpassword = UICredential.CURRENT_PASSWORD;

        activeDirectory.protocolName = activeDirectoryConfiguration.getConnectionSetting().getProtocol();

        activeDirectory.port = Long.toString(
                activeDirectoryConfiguration.getConnectionSetting().getPort());

        activeDirectory.email = activeDirectoryConfiguration.getAttributeMapping().getEmail();
        activeDirectory.firstName = activeDirectoryConfiguration.getAttributeMapping().getFirstName();
        activeDirectory.lastName = activeDirectoryConfiguration.getAttributeMapping().getLastName();
        activeDirectory.userName = activeDirectoryConfiguration.getAttributeMapping().getUserId();

        activeDirectory.baseDN = activeDirectoryConfiguration.getFilterSetting().getBaseDn();
        activeDirectory.filterSettingType = activeDirectoryConfiguration.getFilterSetting().getFilterClass();

        return activeDirectory;
    }

    /**
     *
     * @param uiActiveDirectory
     * @return
     */
    private ActiveDirectoryConfiguration getAPIObject(UIActiveDirectory uiActiveDirectory) {

        ActiveDirectoryConfiguration activeDirectoryConfiguration = new ActiveDirectoryConfiguration();

        activeDirectoryConfiguration.setName(uiActiveDirectory.serverName);
        activeDirectoryConfiguration.setSeqId(uiActiveDirectory.id);
        activeDirectoryConfiguration.getConnectionSetting().setBindDn(uiActiveDirectory.bindDN);
        activeDirectoryConfiguration.getConnectionSetting().setHostName(uiActiveDirectory.hostName);
        activeDirectoryConfiguration.getConnectionSetting().setMethod("simple");
        activeDirectoryConfiguration.getConnectionSetting().setPassword(uiActiveDirectory.password);
        activeDirectoryConfiguration.getConnectionSetting().setProtocol(
                uiActiveDirectory.protocolName.toLowerCase());
        activeDirectoryConfiguration.getConnectionSetting().setPort(
                Integer.parseInt(uiActiveDirectory.port));

        activeDirectoryConfiguration.getAttributeMapping().setEmail(uiActiveDirectory.email);
        activeDirectoryConfiguration.getAttributeMapping().setFirstName(
                uiActiveDirectory.firstName);
        activeDirectoryConfiguration.getAttributeMapping().setLastName(uiActiveDirectory.lastName);
        activeDirectoryConfiguration.getAttributeMapping().setUserId(uiActiveDirectory.userName);

        activeDirectoryConfiguration.getFilterSetting().setBaseDn(uiActiveDirectory.baseDN);
        activeDirectoryConfiguration.getFilterSetting().setFilterClass(
                uiActiveDirectory.filterSettingType);

        return activeDirectoryConfiguration;
    }

}
