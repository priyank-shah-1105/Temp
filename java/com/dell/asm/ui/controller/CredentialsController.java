/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.credential.AsmCredentialDTO;
import com.dell.asm.asmcore.asmmanager.client.credential.AsmCredentialListDTO;
import com.dell.asm.encryptionmgr.client.CredentialType;
import com.dell.asm.ui.adapter.service.CredentialServiceAdapter;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.credential.JobCredentialRequest;
import com.dell.asm.ui.model.credential.UICredential;
import com.dell.asm.ui.model.credential.UICredentialSummary;
import com.dell.asm.ui.util.ContextUtility;
import com.dell.asm.ui.util.MappingUtils;

/**
 * CredentialsController. Calls AsmManager credential services.
 */
@RestController
@RequestMapping(value = "/credentials/")
public class CredentialsController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(CredentialsController.class);

    /** The credential service adapter. */
    private CredentialServiceAdapter credentialServiceAdapter;

    @Autowired
    public CredentialsController(CredentialServiceAdapter credentialServiceAdapter) {
        this.credentialServiceAdapter = credentialServiceAdapter;
    }

    @ModelAttribute
    public void setNoCache(HttpServletResponse servletResponse) {
        ContextUtility.setCacheControlHeaders(servletResponse);
    }

    /**
     * Get credential by ID.
     *
     * @param request
     *            the request
     * @return credential
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getcredentialbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getCredentialById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            AsmCredentialDTO ccResponse = credentialServiceAdapter.getCredential(
                    request.requestObj.id);
            if (ccResponse != null) {
                UICredential uic = MappingUtils.parseAsmCredentialDTO(ccResponse);
                jobResponse.responseObj = uic;
            }
        } catch (Throwable t) {
            log.error("createCredential() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Create new credential.
     *
     * @param request
     *            the request
     * @return new credential
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "savecredential", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse saveCredential(@RequestBody JobCredentialRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            AsmCredentialDTO ccResponse;

            // new or update?
            if (request.requestObj.id != null) {
                // update
                ccResponse = credentialServiceAdapter.updateCredential(request.requestObj.id,
                                                                       request.requestObj);
            } else {
                // create new
                ccResponse = credentialServiceAdapter.createCredential(request.requestObj);
            }

            request.requestObj.id = ccResponse.getCredential().getId();
            jobResponse.responseObj = request.requestObj;

        } catch (Throwable t) {
            log.error("saveCredential() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Gets Credential List.
     *
     * @return the Credential List
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getcredentialsummaries", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse getCredentialSummaryList(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UICredentialSummary> responseObj = new ArrayList<>();

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_CREDENTIALS,
                                                            "credentialsName");

        try {

            AsmCredentialListDTO cList = credentialServiceAdapter.getAllCredentials
                    (null,
                     options.sortedColumns,
                     options.filterList,
                     options.offset < 0 ? null : options.offset,
                     options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);

            if (cList != null) {
                if (cList.getCredentialList() != null && cList.getCredentialList().size() > 0) {
                    for (AsmCredentialDTO dto : cList.getCredentialList()) {
                        responseObj.add(MappingUtils.parseAsmCredentialDTO(dto,
                                                                           this.getApplicationContext()));
                    }
                } else {
                    if (cList.getTotalRecords() > 0 && request.criteriaObj != null &&
                            request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    cList.getTotalRecords());
                        return getCredentialSummaryList(newRequest);
                    }
                }
            }

            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null && cList != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = cList.getTotalRecords();
            }


        } catch (Throwable t) {
            log.error("getCredentialSummaryList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Delete credential by ID.
     *
     * @param request
     *            the request
     * @return none
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "deletecredential", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse deleteCredential(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            credentialServiceAdapter.deleteCredential(request.requestObj.id);
        } catch (Throwable t) {
            log.error("deleteCredential() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the credential by type.
     *
     * @param request
     *            the request
     * @return the credential by type
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getcredentialsbytype", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse getCredentialByType(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UICredential> responseObj = new ArrayList<>();

        CredentialType asmType = MappingUtils.getAsmDTOType(request.requestObj.id);

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_CREDENTIALS,
                                                            "username");

        try {
            AsmCredentialListDTO cList = credentialServiceAdapter.getAllCredentials(asmType, null,
                                                                                    options.filterList,
                                                                                    null, null);
            if (cList != null) {
                for (AsmCredentialDTO dto : cList.getCredentialList()) {
                    responseObj.add(MappingUtils.parseAsmCredentialDTO(dto));
                }
            }
            Collections.reverse(responseObj);
            log.debug("reverse order of responseObj " + responseObj);
            jobResponse.responseObj = responseObj;

        } catch (Throwable t) {
            log.error("getCredentialByType() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        log.debug("getCredentialsByType() - JobResponse sent");
        return jobResponse;
    }

    /**
     * Gets Credential List.
     *
     * @return the Credential List
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getcredentiallist", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse getCredentialList() {
        JobResponse jobResponse = new JobResponse();
        List<UICredential> responseObj = new ArrayList<>();

        try {
            AsmCredentialListDTO cList = credentialServiceAdapter.getAllCredentials(null, null,
                                                                                    null, null,
                                                                                    null);
            if (cList != null) {
                UICredential uic;
                for (AsmCredentialDTO dto : cList.getCredentialList()) {
                    uic = MappingUtils.parseAsmCredentialDTO(dto);
                    responseObj.add(uic);
                }
            }
        } catch (Throwable t) {
            log.error("getCredentialList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }
}
