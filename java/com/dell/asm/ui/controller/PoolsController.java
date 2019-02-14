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
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.ui.adapter.service.PoolServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.ErrorObj;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.pool.JobCreatePoolRequest;
import com.dell.asm.ui.model.pool.JobUpdatePoolRequest;
import com.dell.asm.ui.model.pool.UICreatePool;
import com.dell.asm.ui.model.pool.UIPools;
import com.dell.asm.ui.model.pool.UIPoolsSummary;
import com.dell.asm.ui.util.MappingUtils;
import com.dell.pg.asm.identitypool.api.common.model.VirtualIdentityType;
import com.dell.pg.asm.identitypool.api.iopool.model.Pool;
import com.dell.pg.asm.identitypool.api.iopool.model.VirtualIdentityPool;

@RestController
@RequestMapping(value = "/pools/")
public class PoolsController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(PoolsController.class);
    private final Map<String, String> m_exportContent = new HashMap<>();
    private PoolServiceAdapter poolServiceAdapter;

    @Autowired
    public PoolsController(PoolServiceAdapter poolServiceAdapter) {
        this.poolServiceAdapter = poolServiceAdapter;
    }

    /**
     * Gets the pools.
     *
     * @param request
     *            the request
     * @return the pools
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getpools", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getPools(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIPoolsSummary> responseObj = new ArrayList<>();
        try {
            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_POOLS, "name");
            ResourceList<Pool> resourcePoollist = poolServiceAdapter.getPools(options.sortedColumns,
                                                                              options.filterList,
                                                                              options.offset < 0 ? null : options.offset,
                                                                              options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);

            if (resourcePoollist != null && resourcePoollist.getList() != null) {
                if (resourcePoollist.getList().length > 0) {
                    for (Pool net : resourcePoollist.getList()) {
                        UIPoolsSummary pool = parsePool(net);
                        responseObj.add(pool);
                    }
                } else {
                    if (resourcePoollist.getTotalRecords() > 0 && request.criteriaObj != null &&
                            request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) resourcePoollist.getTotalRecords());
                        return getPools(newRequest);
                    }
                }
            }
            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                int totalcount = (int) ((resourcePoollist != null) ? resourcePoollist.getTotalRecords() : 0);
                jobResponse.criteriaObj.paginationObj.totalItemsCount = totalcount;
            }

        } catch (Throwable t) {
            log.error("getPools() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;

    }

    /**
     * Gets available prefix list.
     *
     * @param request
     *            the request
     * @return the pools
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getpoolprefixlist", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getPoolPrefixList(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIListItem> responseObj = new ArrayList<>();
        try {
            ResourceList<Pool> resourcePoollist = poolServiceAdapter.getPools(null, null, null,
                                                                              null);

            List<String> prefixes = new ArrayList<>();
            if (resourcePoollist != null && resourcePoollist.getList() != null && resourcePoollist.getList().length > 0) {
                for (Pool net : resourcePoollist.getList()) {
                    UIPools uip = parsePools(poolServiceAdapter.getPool(net.getId()));
                    if (uip.virtualWWPNUserPrefixSelection != null && !prefixes.contains(
                            uip.virtualWWPNUserPrefixSelection.toUpperCase()))
                        prefixes.add(uip.virtualWWPNUserPrefixSelection.toUpperCase());

                    if (uip.virtualMACUserPrefixSelection != null && !prefixes.contains(
                            uip.virtualMACUserPrefixSelection.toUpperCase()))
                        prefixes.add(uip.virtualMACUserPrefixSelection.toUpperCase());
                }
            }

            for (int i = 0; i <= 255; i++) {
                String prefixNum = Integer.toHexString(i).toUpperCase();

                if (prefixNum.length() == 1) {
                    prefixNum = "0" + prefixNum;
                }

                if (!prefixes.contains(prefixNum))
                    responseObj.add(new UIListItem(prefixNum, prefixNum));
                else
                    log.debug("skipped prefix: " + prefixNum);
            }

            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getPoolPrefixList() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;

    }

    private UIPoolsSummary parsePool(Pool pool) {
        UIPoolsSummary sum = new UIPoolsSummary();
        if (pool != null) {
            sum.id = pool.getId();
            sum.name = pool.getName();

            UIPools pools = this.parsePools(pool);
            sum.assignedAddressPools = (int) pool.getAssigned();
            sum.availableAddressPools = (int) pool.getAvailable();
            sum.reservedAddressPools = (int) pool.getReserved();
            sum.canDelete = pools.candelete;
            sum.deploymentTemplateCount = pools.deploymentTemplateCount;
            sum.description = pool.getDescription();
            sum.createdby = pool.getCreatedBy();
            sum.createddate = MappingUtils.getTime(pool.getCreatedDate());
        }
        return sum;
    }

    /**
     * Submit change pool.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "createpool", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse createPool(@RequestBody JobCreatePoolRequest request) {
        JobResponse jobResponse;
        boolean isCreated = false;
        Pool configuration = null;

        try {
            if (request.requestObj != null) {
                configuration = createPoolModel(request.requestObj);
                if (configuration.getId() == null) {
                    configuration = poolServiceAdapter.addPool(configuration);
                    isCreated = true;
                    if (request.requestObj.virtualMACIdentityCount > 0) {
                        poolServiceAdapter.generateVirtualIdentity(configuration.getId(),
                                                                   VirtualIdentityType.MAC.toString(),
                                                                   request.requestObj.virtualMACIdentityCount);
                    }
                    if (request.requestObj.virtualIQNIdentityCount > 0) {
                        poolServiceAdapter.generateVirtualIdentity(configuration.getId(),
                                                                   VirtualIdentityType.IQN.toString(),
                                                                   request.requestObj.virtualIQNIdentityCount);
                    }
                    if (request.requestObj.virtualWWPNIdentityCount > 0) {
                        poolServiceAdapter.generateVirtualIdentity(configuration.getId(),
                                                                   VirtualIdentityType.WWPN.toString(),
                                                                   request.requestObj.virtualWWPNIdentityCount);
                    }
                    if (request.requestObj.virtualWWNNIdentityCount > 0) {
                        poolServiceAdapter.generateVirtualIdentity(configuration.getId(),
                                                                   VirtualIdentityType.WWNN.toString(),
                                                                   request.requestObj.virtualWWNNIdentityCount);
                    }


                } else {
                    poolServiceAdapter.updatePool(configuration.getId(), configuration);
                }


            }
            jobResponse = new JobResponse();
        } catch (Throwable t) {
            if (isCreated && configuration != null) {
                // delete pool because the transaction failed
                try {
                    poolServiceAdapter.deletePool(configuration.getId());
                } catch (Throwable t2) {
                    log.error("createPool:deletePool() - Exception from service call", t2);
                }
            }
            log.error("createPool() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;

    }

    private Pool createPoolModel(UICreatePool req) {
        Pool pool = new Pool();
        pool.setName(req.name);
        pool.setDescription(req.description);
        VirtualIdentityPool virtualIdentityPool;
        virtualIdentityPool = createVirtualIdentityPoolModel(req, pool, VirtualIdentityType.MAC);
        pool.setMacPool(virtualIdentityPool);
        virtualIdentityPool = createVirtualIdentityPoolModel(req, pool, VirtualIdentityType.IQN);
        pool.setIqnPool(virtualIdentityPool);
        virtualIdentityPool = createVirtualIdentityPoolModel(req, pool, VirtualIdentityType.WWPN);
        pool.setWwpnPool(virtualIdentityPool);
        virtualIdentityPool = createVirtualIdentityPoolModel(req, pool, VirtualIdentityType.WWNN);
        pool.setWwnnPool(virtualIdentityPool);


        return pool;
    }

    private VirtualIdentityPool createVirtualIdentityPoolModel(UICreatePool req, Pool pool,
                                                               VirtualIdentityType iType) {
        VirtualIdentityPool virtualIdentityPool = new VirtualIdentityPool();

        switch (iType) {
        case MAC:
            virtualIdentityPool.setType(VirtualIdentityType.MAC);
            virtualIdentityPool.setAutoGenerateIdentities(req.virtualMACAutoGenerateOnDeploy);
            virtualIdentityPool.setPrefix(req.virtualMACUserPrefixSelection);
            break;
        case IQN:
            virtualIdentityPool.setType(VirtualIdentityType.IQN);
            virtualIdentityPool.setAutoGenerateIdentities(req.virtualIQNAutoGenerateOnDeploy);
            virtualIdentityPool.setPrefix(req.virtualIQNUserPrefix);
            break;
        case WWNN:
            virtualIdentityPool.setType(VirtualIdentityType.WWNN);
            virtualIdentityPool.setAutoGenerateIdentities(req.virtualWWNNAutoGenerateOnDeploy);
            virtualIdentityPool.setPrefix(req.virtualWWNNUserPrefixSelection);
            break;
        case WWPN:
            virtualIdentityPool.setType(VirtualIdentityType.WWPN);
            virtualIdentityPool.setAutoGenerateIdentities(req.virtualWWPNAutoGenerateOnDeploy);
            virtualIdentityPool.setPrefix(req.virtualWWPNUserPrefixSelection);
            break;
        }
        return virtualIdentityPool;
    }

    /**
     * Gets the pool by id.
     *
     * @param request
     *            the request
     * @return the pool by id
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getpoolbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getPoolByID(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();
        UIPools responseObj;
        try {
            responseObj = parsePools(poolServiceAdapter.getPool(request.requestObj.id));
            jobResponse.responseObj = responseObj;
        } catch (Throwable t) {
            log.error("getPoolByID() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;

    }

    private UIPools parsePools(Pool pool) {
        UIPools uipools = new UIPools();
        uipools.id = pool.getId();
        uipools.name = pool.getName();
        uipools.candelete = !pool.getId().equals("-1");
        uipools.description = pool.getDescription();
        uipools.createdby = pool.getCreatedBy();
        uipools.createddate = MappingUtils.getTime(pool.getCreatedDate());

        uipools.assignedAddressPools = (int) pool.getAssigned();
        uipools.availableAddressPools = (int) pool.getAvailable();
        uipools.reservedAddressPools = (int) pool.getReserved();
        List<VirtualIdentityPool> virtualIdentityPools = new ArrayList<>();
        virtualIdentityPools.add(pool.getIqnPool());
        virtualIdentityPools.add(pool.getMacPool());
        virtualIdentityPools.add(pool.getWwnnPool());
        virtualIdentityPools.add(pool.getWwpnPool());

        if (pool.getMacPool() != null)
            uipools.virtualMACUserPrefixSelection = pool.getMacPool().getPrefix();

        if (pool.getWwnnPool() != null)
            uipools.virtualWWNNUserPrefixSelection = pool.getWwnnPool().getPrefix();

        if (pool.getWwpnPool() != null)
            uipools.virtualWWPNUserPrefixSelection = pool.getWwpnPool().getPrefix();

        if (pool.getIqnPool() != null)
            uipools.virtualIQNUserPrefix = pool.getIqnPool().getPrefix();

        for (VirtualIdentityPool virtualIdentityPool : virtualIdentityPools) {
            if (virtualIdentityPool != null) {
                switch (virtualIdentityPool.getType()) {
                case MAC:
                    uipools.lanAvailableAddressPools = (int) virtualIdentityPool.getAvailable();
                    uipools.lanAssignedAddressPools = (int) virtualIdentityPool.getAssigned();
                    uipools.lanReservedAddressPools = (int) virtualIdentityPool.getReserved();
                    uipools.virtualMACAutoGenerateOnDeploy = virtualIdentityPool.isAutoGenerateIdentities();
                    break;
                case IQN:
                    uipools.iscsiAssignedAddressPools = (int) virtualIdentityPool.getAssigned();
                    uipools.iscsiReservedAddressPools = (int) virtualIdentityPool.getReserved();
                    uipools.iscsiAvailableAddressPools = (int) virtualIdentityPool.getAvailable();
                    uipools.virtualIQNAutoGenerateOnDeploy = virtualIdentityPool.isAutoGenerateIdentities();
                    break;
                case WWNN:
                    uipools.wwnnAvailableAddressPools = (int) virtualIdentityPool.getAvailable();
                    uipools.wwnnAssignedAddressPools = (int) virtualIdentityPool.getAssigned();
                    uipools.wwnnReservedAddressPools = (int) virtualIdentityPool.getReserved();
                    uipools.virtualWWNNAutoGenerateOnDeploy = virtualIdentityPool.isAutoGenerateIdentities();
                    break;
                case WWPN:
                    uipools.wwpnAvailableAddressPools = (int) virtualIdentityPool.getAvailable();
                    uipools.wwpnAssignedAddressPools = (int) virtualIdentityPool.getAssigned();
                    uipools.wwpnReservedAddressPools = (int) virtualIdentityPool.getReserved();
                    uipools.virtualWWPNAutoGenerateOnDeploy = virtualIdentityPool.isAutoGenerateIdentities();
                    break;
                }
            }
        }
        return uipools;
    }

    /**
     * Delete pool.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "deletepools", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse deletePool(@RequestBody JobStringsRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            for (String id : request.requestObj) {
                try {
                    poolServiceAdapter.deletePool(id);
                } catch (Throwable t) {
                    log.error("deletePool() - Exception from service call", t);
                    jobResponse = addFailureResponseInfo(t);
                }
            }

        } catch (Throwable t) {
            log.error("deletePool() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Validate pool.
     *
     * @param request
     *            the request     * @return the job response
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "validatepool", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse validatePool(@RequestBody JobCreatePoolRequest request) {

        JobResponse jobResponse = null;

        if (request.requestObj != null) {
            Pool pool = createPoolModel(request.requestObj);
            ErrorObj errObj = new ErrorObj();

            try {
                List<String> validationErrors = new ArrayList<>();
                List<String> filter = new ArrayList<>();
                if (pool.getName() != null) {
                    filter.add("eq,name," + pool.getName());
                }

                ResourceList<Pool> result = poolServiceAdapter.getPools(null, filter, null, null);
                if (result != null && result.getList() != null && result.getTotalRecords() > 0) {
                    errObj.errorMessage = this.getApplicationContext().getMessage(
                            "validationError.duplicatePoolName", new Object[] { pool.getName() },
                            LocaleContextHolder.getLocale());
                    validationErrors.add(errObj.errorMessage);
                }

                jobResponse = new JobResponse();
                jobResponse.responseObj = validationErrors;
                if (validationErrors.size() > 0) {
                    jobResponse.responseCode = -1;
                    jobResponse.errorObj = errObj;
                }

            } catch (Throwable t) {
                log.error("validatePool() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }
        return jobResponse;
    }

    /**
     * Update pool.
     *
     * @param request
     *
     *            the request     * @return the job response
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updatepool", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updatePool(@RequestBody JobUpdatePoolRequest request) {

        JobResponse jobResponse = new JobResponse();

        if (request.requestObj != null) {
            try {

                Pool pool = poolServiceAdapter.getPool(request.requestObj.id);
                if (pool != null) {
                    switch (request.requestObj.type) {
                    case "mac":
                        pool.getMacPool().setAutoGenerateIdentities(
                                request.requestObj.autogenerate);
                        if (request.requestObj.identitycount > 0)
                            poolServiceAdapter.generateVirtualIdentity(request.requestObj.id,
                                                                       VirtualIdentityType.MAC.toString(),
                                                                       request.requestObj.identitycount);
                        break;
                    case "iqn":
                        pool.getIqnPool().setAutoGenerateIdentities(
                                request.requestObj.autogenerate);
                        if (request.requestObj.identitycount > 0)
                            poolServiceAdapter.generateVirtualIdentity(request.requestObj.id,
                                                                       VirtualIdentityType.IQN.toString(),
                                                                       request.requestObj.identitycount);
                        break;
                    case "wwpn":
                        pool.getWwpnPool().setAutoGenerateIdentities(
                                request.requestObj.autogenerate);
                        if (request.requestObj.identitycount > 0)
                            poolServiceAdapter.generateVirtualIdentity(request.requestObj.id,
                                                                       VirtualIdentityType.WWPN.toString(),
                                                                       request.requestObj.identitycount);
                        break;
                    case "wwnn":
                        pool.getWwnnPool().setAutoGenerateIdentities(
                                request.requestObj.autogenerate);
                        if (request.requestObj.identitycount > 0)
                            poolServiceAdapter.generateVirtualIdentity(request.requestObj.id,
                                                                       VirtualIdentityType.WWNN.toString(),
                                                                       request.requestObj.identitycount);
                        break;
                    }
                    poolServiceAdapter.updatePool(pool.getId(), pool);
                } else {
                    throw new ControllerException(
                            "Pool with id " + request.requestObj.id + " not found.");
                }
            } catch (Throwable t) {
                log.error("updatePool() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }
        return jobResponse;
    }

    /**
     * Export pools.
     *
     * @param request: array of pool ids
     * @return the file URL
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "exportpools", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse exportPools(@RequestBody JobStringsRequest request, HttpSession session) {

        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = "pools/pools.txt";

        StringBuilder builder = new StringBuilder();

        Locale locale = LocaleContextHolder.getLocale();

        if (request.requestObj != null) {
            try {
                for (String id : request.requestObj) {
                    Pool poolDTO = poolServiceAdapter.getPool(id);
                    if (poolDTO != null) {
                        UIPools pools = parsePools(poolDTO);

                        builder.append(getApplicationContext().getMessage("ExportPool.Name", null,
                                                                          locale)).append(
                                ":\t").append(pools.name).append("\n");
                        if (pools.description != null)
                            builder.append(
                                    getApplicationContext().getMessage("ExportPool.Description",
                                                                       null, locale)).append(
                                    ":\t").append(pools.description).append("\n");
                        if (pools.createdby != null)
                            builder.append(
                                    getApplicationContext().getMessage("ExportPool.CreatedBy", null,
                                                                       locale)).append(
                                    ":\t").append(pools.createdby).append("\n");
                        if (pools.createddate != null)
                            builder.append(
                                    getApplicationContext().getMessage("ExportPool.CreatedDate",
                                                                       null, locale)).append(
                                    ":\t").append(DateTime.parse(pools.createddate).toString(
                                    "MMMMM dd yyyy h:mm a", locale)).append("\n");

                        builder.append("\n");
                        printIdentityHeader(builder,
                                            getApplicationContext().getMessage("ExportPool.MAC",
                                                                               null, locale),
                                            pools.virtualMACUserPrefixSelection,
                                            pools.lanReservedAddressPools,
                                            pools.lanAssignedAddressPools,
                                            pools.lanAvailableAddressPools,
                                            pools.virtualMACAutoGenerateOnDeploy, locale);
                        builder.append("\n");
                        printIdentityHeader(builder,
                                            getApplicationContext().getMessage("ExportPool.IQN",
                                                                               null, locale),
                                            pools.virtualIQNUserPrefix,
                                            pools.iscsiReservedAddressPools,
                                            pools.iscsiAssignedAddressPools,
                                            pools.iscsiAvailableAddressPools,
                                            pools.virtualIQNAutoGenerateOnDeploy, locale);
                        builder.append("\n");
                        printIdentityHeader(builder,
                                            getApplicationContext().getMessage("ExportPool.WWPN",
                                                                               null, locale),
                                            pools.virtualWWPNUserPrefixSelection,
                                            pools.wwpnReservedAddressPools,
                                            pools.wwpnAssignedAddressPools,
                                            pools.wwpnAvailableAddressPools,
                                            pools.virtualWWPNAutoGenerateOnDeploy, locale);
                        builder.append("\n");
                        printIdentityHeader(builder,
                                            getApplicationContext().getMessage("ExportPool.WWNN",
                                                                               null, locale),
                                            pools.virtualWWNNUserPrefixSelection,
                                            pools.wwnnReservedAddressPools,
                                            pools.wwnnAssignedAddressPools,
                                            pools.wwnnAvailableAddressPools,
                                            pools.virtualWWNNAutoGenerateOnDeploy, locale);

                        builder.append("\n");
                        builder.append("\n");
                    }
                }
            } catch (Throwable t) {
                log.error("exportPools() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }
        }

        String sessionId = session.getId();
        synchronized (m_exportContent) {
            m_exportContent.put(sessionId, builder.toString());
        }


        return jobResponse;
    }

    private void printIdentityHeader(StringBuilder builder, String type, String selectedPrefix,
                                     int reserved, int assigned, int available,
                                     boolean autoGenerate, Locale locale) {
        builder.append(type).append("\n");
        builder.append(getApplicationContext().getMessage("ExportPool.SelectedPrefix", null,
                                                          locale)).append(":\t\t");
        builder.append(selectedPrefix).append("\n");
        builder.append(
                getApplicationContext().getMessage("ExportPool.Reserved", null, locale)).append(
                ":\t\t\t\t");
        builder.append(reserved).append("\n");
        builder.append(
                getApplicationContext().getMessage("ExportPool.Assigned", null, locale)).append(
                ":\t\t\t\t");
        builder.append(assigned).append("\n");
        builder.append(
                getApplicationContext().getMessage("ExportPool.Available", null, locale)).append(
                ":\t\t\t\t");
        builder.append(available).append("\n");
        builder.append(
                getApplicationContext().getMessage("ExportPool.AutoGenerate", null, locale)).append(
                ":\t\t\t");
        builder.append(autoGenerate).append("\n");
    }

    /**
     * Get export file.
     *
     * @param response
     * @param session
     * @throws javax.servlet.ServletException
     *             the servlet exception
     * @throws java.io.IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "pools.txt", method = RequestMethod.GET)
    public void getExportFile(HttpServletResponse response, HttpSession session) {

        String sessionId = session.getId();
        String content;
        synchronized (m_exportContent) {
            content = m_exportContent.get(sessionId);
        }

        try {
            response.setStatus(HttpStatus.OK_200);
            response.setContentLength(content.length());
            response.setContentType("text/csv; charset=utf-8");
            response.setHeader("Content-Disposition",
                               "attachment; filename=\"" + getApplicationContext().getMessage(
                                       "ExportPool.FileName", null,
                                       LocaleContextHolder.getLocale()));
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");

            ServletOutputStream out = response.getOutputStream();
            out.print(content);
            out.flush();
            out.close();
        } catch (Throwable t) {
            log.error("getExportFile() - Exception from service call", t);
        }
    }
}
