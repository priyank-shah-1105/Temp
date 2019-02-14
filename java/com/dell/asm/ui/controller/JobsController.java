/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.JobsServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIJobs;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.jraf.client.jobmgr.JrafJobInfo;

@RestController
@RequestMapping(value = "/jobs/")
public class JobsController extends BaseController {

    private static final Logger log = Logger.getLogger(JobsController.class);

    private final Map<String, String> m_exportContent = new HashMap<>();

    private JobsServiceAdapter jobsServiceAdapter;
    private DeploymentServiceAdapter deploymentServiceAdapter;

    @Autowired
    public JobsController(JobsServiceAdapter jobsServiceAdapter,
                          DeploymentServiceAdapter deploymentServiceAdapter) {
        this.jobsServiceAdapter = jobsServiceAdapter;
        this.deploymentServiceAdapter = deploymentServiceAdapter;
    }

    //Gets the jobs.
    @RequestMapping(value = "getjoblist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getJobs(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();
        List<UIJobs> responseObj = new ArrayList<>();

        try {
            jobResponse.criteriaObj = request.criteriaObj;

            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_JOBS, "name");

            ResourceList<JrafJobInfo> jobs = jobsServiceAdapter.getJobs(options.sortedColumns,
                                                                        options.filterList,
                                                                        options.offset < 0 ? null : options.offset,
                                                                        options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);
            if (jobs != null) {
                DateTime current = DateTime.now();
                for (JrafJobInfo jrafJobInfo : jobs.getList()) {
                    responseObj.add(new UIJobs(jrafJobInfo, current));
                }
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) jobs.getTotalRecords();
                }
            }
        } catch (Throwable t) {
            log.error("getJobs() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    @RequestMapping(value = "getselectedjobid", method = RequestMethod.POST)
    public @ResponseBody
    void getSelectedJobId(@RequestBody JobRequest request) {
    }

    //cancels selected jobs
    @RequestMapping(value = "deletejob", method = RequestMethod.POST)
    public @ResponseBody
    void deleteJobs(@RequestBody JobStringsRequest request) {
        try {
            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_JOBS, "name");
            ResourceList<JrafJobInfo> jobs = jobsServiceAdapter.getJobs(options.sortedColumns,
                                                                        options.filterList,
                                                                        options.offset < 0 ? null : options.offset,
                                                                        options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);
            for (String id : request.requestObj) {
                try {
                    jobsServiceAdapter.deleteJobs(id);
                    JrafJobInfo job = null;
                    for (JrafJobInfo jrafJobInfo : jobs.getList()) {
                        if (id.equals(jrafJobInfo.getJobKey().getName())) {
                            job = jrafJobInfo;
                            break;
                        }
                    }
                    if (job == null) {
                        break;
                    }
                    if (job.getDeploymentId() != null || !job.getDeploymentId().equals("")) {
                        deploymentServiceAdapter.deleteDeployment(job.getDeploymentId(), null, null);
                    }
                } catch (Throwable t) {
                    log.error("deleteJobs() - Exception from service call", t);
                }
            }

        } catch (Throwable t) {
            log.error("deletePool() - Exception from service call", t);
        }
    }

    @RequestMapping(value = "getjobbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getJobById(@RequestBody JobRequest request) {
        return null;
    }

    //exports jobs to a csv
    @RequestMapping(value = "exportjobs", method = RequestMethod.GET)
    public void exportJobs(HttpServletResponse response) {
        log.debug(" Entered exportJobs() ");
        ResourceList<JrafJobInfo> jobs = jobsServiceAdapter.getJobs(null, null, null, null);
        StringBuilder builder = new StringBuilder();
        try {
            if (jobs != null) {
                for (JrafJobInfo job : jobs.getList()) {
                    builder.append("\"" + "Job name:").append(job.getJobKey().getName()).append(
                            "\",");
                    builder.append("\"" + "Job group:").append(job.getJobKey().getGroup()).append(
                            "\",");
                    builder.append("\"" + "Start date:").append(
                            MappingUtils.getTime(job.getStartDate())).append("\",");
                    builder.append("\"" + "Created by:").append(job.getCreatedBy()).append("\". ");
                }
            }
            response.setStatus(200);
            response.setContentLength(builder.toString().length());
            response.setContentType("text/csv; charset=utf-8");
            response.setHeader("Content-Disposition",
                               "attachment; filename=\"" + getApplicationContext().getMessage(
                                       "JobsController.JobsFileName", null,
                                       LocaleContextHolder.getLocale())
                                       + ".csv\"");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");

            ServletOutputStream out = response.getOutputStream();
            out.println(builder.toString());
            out.flush();
            out.close();

        } catch (Throwable t) {
            log.error("exportJobs() - Exception from service call", t);
        }
    }
}