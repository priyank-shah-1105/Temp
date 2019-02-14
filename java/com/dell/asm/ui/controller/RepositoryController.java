/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2014 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.osrepository.OSRepository;
import com.dell.asm.ui.adapter.service.OSRepositoryServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.repository.JobRepositoryRequest;
import com.dell.asm.ui.model.repository.UIRepository;

/**
 * The Class Repository Controller.
 */
@RestController
@RequestMapping(value = "/repository/")
public class RepositoryController extends BaseController {
    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(RepositoryController.class);

    private OSRepositoryServiceAdapter osRepositoryServiceAdapter;

    @Autowired
    public RepositoryController(OSRepositoryServiceAdapter osRepositoryServiceAdapter) {
        this.osRepositoryServiceAdapter = osRepositoryServiceAdapter;
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
    @RequestMapping(value = "testfilerepository", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse testRepository(@RequestBody JobRepositoryRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {

            osRepositoryServiceAdapter.testConnection(request.requestObj.toOSRepository());

        } catch (Throwable t) {
            log.error("testOSRepository() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Get all OS repositories.
     *
     * @param request
     *            the request
     * @return the list of OS repos
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getrepositorylist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getRepositoryList(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UIRepository> responseObj = new ArrayList<>();

        try {
            jobResponse.criteriaObj = request.criteriaObj;

            ResourceList<OSRepository> repos = osRepositoryServiceAdapter.getAll(null, null, null,
                                                                                 null);
            if (repos != null) {
                for (OSRepository repo : repos.getList()) {
                    // hide RCM repos
                    if (OSRepository.RepoType.ISO.equals(repo.getRepoType()) || repo.getRepoType() == null) {
                        responseObj.add(new UIRepository(repo));
                    }
                }
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) repos.getTotalRecords();
                }
            }
        } catch (Throwable t) {
            log.error("getRepositoryList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Create a new Repository.
     *
     * @param request
     *            the request
     * @return the OS repo object created
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "saverepository", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse createRepository(@RequestBody JobRepositoryRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            if (StringUtils.isNotBlank(request.requestObj.id)) {
                jobResponse.responseObj = osRepositoryServiceAdapter.update(request.requestObj.id,
                                                                            request.requestObj.toOSRepository(),
                                                                            false);
            } else {
                jobResponse.responseObj = osRepositoryServiceAdapter.create(
                        request.requestObj.toOSRepository());
            }
        } catch (Throwable t) {
            log.error("createRepository() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Get a Repository by Id.
     *
     * @param request
     *            the request
     * @return the OS repo object fetched
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getrepositorybyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getRepositoryById(@RequestBody JobRepositoryRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            OSRepository osRepo = osRepositoryServiceAdapter.getById(request.requestObj.id);
            jobResponse.responseObj = new UIRepository(osRepo);

        } catch (Throwable t) {
            log.error("createRepository() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }


    /**
     * Delete an OS repository.
     *
     * @param request
     *            the request
     * @return the OS repo object fetched
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "deleterepository", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse deleteRepository(@RequestBody JobRepositoryRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            osRepositoryServiceAdapter.delete(request.requestObj.id);

        } catch (Throwable t) {
            log.error("createRepository() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Sync an OS repository with its associated ISO download
     *
     * @param request
     *            the request
     * @return the OS repo object fetched
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "syncrepository", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse syncRepositoryById(@RequestBody JobRepositoryRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            jobResponse.responseObj = osRepositoryServiceAdapter.sync(request.requestObj.id,
                                                                      request.requestObj.toOSRepository());
        } catch (Throwable t) {
            log.error("syncRepositoryById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

}
