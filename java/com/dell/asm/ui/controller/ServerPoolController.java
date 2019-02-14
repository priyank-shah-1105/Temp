/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.asmmanager.client.devicegroup.DeviceGroup;
import com.dell.asm.asmcore.asmmanager.client.devicegroup.GroupUser;
import com.dell.asm.asmcore.asmmanager.client.devicegroup.GroupUserList;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDevice;
import com.dell.asm.asmcore.asmmanager.client.deviceinventory.ManagedDeviceList;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplateSettingIDs;
import com.dell.asm.asmcore.user.model.User;
import com.dell.asm.ui.adapter.service.ChassisServiceAdapter;
import com.dell.asm.ui.adapter.service.DeviceGroupServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.device.JobSaveServerPoolRequest;
import com.dell.asm.ui.model.device.UIDevice;
import com.dell.asm.ui.model.device.UIServerPool;
import com.dell.asm.ui.model.users.UIUserSummary;
import com.dell.asm.ui.util.ChassisCache;
import com.dell.asm.ui.util.MappingUtils;


/**
 * Resources -&gt; server pools.
 */
@RestController
@RequestMapping(value = "/serverpool/")
public class ServerPoolController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ServerPoolController.class);

    private DeviceGroupServiceAdapter deviceGroupServiceAdapter;
    private ChassisServiceAdapter chassisServiceAdapter;

    @Autowired
    public ServerPoolController(DeviceGroupServiceAdapter deviceGroupServiceAdapter,
                                ChassisServiceAdapter chassisServiceAdapter) {
        this.deviceGroupServiceAdapter = deviceGroupServiceAdapter;
        this.chassisServiceAdapter = chassisServiceAdapter;
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
    @RequestMapping(value = "getserverpools", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServerPools(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIServerPool> responseObj = new ArrayList<>();
        try {
            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_SERVER_POOL,
                                                                "name");

            Integer offset = options.offset < 0 ? null : options.offset;
            Integer limit = options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit;
            ResourceList<DeviceGroup> mList = deviceGroupServiceAdapter.getAllDeviceGroups(
                    options.sortedColumns, options.filterList,
                    offset, limit);

            if (mList != null && mList.getList() != null) {
                if (mList.getList().length > 0) {
                    for (DeviceGroup dto : mList.getList()) {
                        responseObj.add(getUIServerPool(dto, this.getApplicationContext()));
                    }
                } else {
                    if (mList.getTotalRecords() > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) mList.getTotalRecords());
                        return getServerPools(newRequest);
                    }
                }
            }

            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) ((mList != null) ? mList.getTotalRecords() : 0);
            }
        } catch (Throwable t) {
            log.error("getServerPools() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;

    }

    /**
     * Get server pool by ID.
     *
     * @param request
     *            the request
     * @return device
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getserverpoolbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getServerPoolById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {
            DeviceGroup dev = deviceGroupServiceAdapter.getDeviceGroup(request.requestObj.id);
            UIServerPool sp = getUIServerPool(dev, this.getApplicationContext());
            jobResponse.responseObj = sp;
        } catch (Throwable t) {
            log.error("getServerPoolById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Remove server pool from inventory by ids..
     *
     * @param request
     *            array of IDs
     * @return chassis
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "remove", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse removeServerPool(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            for (String id : request.requestObj) {
                if (id.equals(ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_POOL_GLOBAL_ID)) {
                    throw new ControllerException(
                            getApplicationContext().getMessage("error.GlobalServerPool", null,
                                                               LocaleContextHolder.getLocale()));
                } else {
                    deviceGroupServiceAdapter.deleteDeviceGroup(id);
                }
            }
        } catch (Throwable t) {
            log.error("removeServerPool() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Submit change/create server pool.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "saveserverpool", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse saveServerPool(@RequestBody JobSaveServerPoolRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            DeviceGroup configuration;
            if (request.requestObj != null) {
                configuration = createDeviceGroup(request.requestObj, this.getApplicationContext());
                if (StringUtils.isNotBlank(request.requestObj.id)) {
                    deviceGroupServiceAdapter.updateDeviceGroup(
                            configuration.getGroupSeqId().toString(), configuration);
                } else {
                    DeviceGroup n = deviceGroupServiceAdapter.createDeviceGroup(configuration);
                    request.requestObj.id = n.getGroupSeqId().toString();
                }
            }
            jobResponse.responseObj = request.requestObj;
        } catch (Throwable t) {
            log.error("saveServerPool() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;

    }

    private DeviceGroup createDeviceGroup(UIServerPool sp, ApplicationContext applicationContext) {
        DeviceGroup dg = new DeviceGroup();
        dg.setGroupDescription(sp.description);
        dg.setGroupName(sp.name);

        if (sp.id != null)
            dg.setGroupSeqId(Long.parseLong(sp.id));

        ManagedDeviceList mList = new ManagedDeviceList();
        List<ManagedDevice> listMD = new ArrayList<>();
        mList.setManagedDevices(listMD);
        dg.setManagedDeviceList(mList);

        GroupUserList uList = new GroupUserList();
        List<GroupUser> listGU = new ArrayList<>();
        uList.setGroupUsers(listGU);
        dg.setGroupUserList(uList);

        for (UIDevice dev : sp.servers) {
            ManagedDevice mdev = DeviceController.createManagedDevice(dev);
            listMD.add(mdev);
        }

        for (UIUserSummary sum : sp.users) {
            GroupUser gu = createUser(sum);
            listGU.add(gu);
        }

        return dg;
    }

    private UIServerPool getUIServerPool(DeviceGroup devGroup,
                                         ApplicationContext applicationContext) {
        UIServerPool sp = new UIServerPool();
        sp.createdby = devGroup.getCreatedBy();
        sp.createddate = MappingUtils.getTime(devGroup.getCreatedDate());
        sp.description = devGroup.getGroupDescription();
        sp.id = devGroup.getGroupSeqId().toString();
        sp.name = devGroup.getGroupName();

        if (devGroup.getManagedDeviceList() != null && devGroup.getManagedDeviceList().getManagedDevices() != null) {
            ChassisCache cache = new ChassisCache();
            for (ManagedDevice dev : devGroup.getManagedDeviceList().getManagedDevices()) {

                UIDevice server = DeviceController.getUIDeviceFromManagedDevice(dev,
                                                                                null);

                if (server != null) {
                    sp.servers.add(server);
                    UIDevice chassisLoc = DeviceController.getChassisLocation(dev,
                                                                              chassisServiceAdapter,
                                                                              cache);
                    if (chassisLoc != null) {
                        if (chassisLoc.location != null)
                            server.location = chassisLoc.location;
                        if (chassisLoc.chassisname != null)
                            server.chassisname = chassisLoc.chassisname;
                    }
                }
            }
        }

        if (devGroup.getGroupUserList() != null && devGroup.getGroupUserList().getGroupUsers() != null) {
            for (GroupUser user : devGroup.getGroupUserList().getGroupUsers()) {
                UIUserSummary us = getUIUserSummary(user, applicationContext);
                if (us != null)
                    sp.users.add(us);
            }
        }

        sp.servercount = sp.servers.size();
        sp.usercount = sp.users.size();

        if (devGroup.getGroupSeqId() == Long.parseLong(
                ServiceTemplateSettingIDs.SERVICE_TEMPLATE_SERVER_POOL_GLOBAL_ID)) {
            sp.canDelete = false;
            sp.canEdit = false;
        } else {
            sp.canDelete = true;
            sp.canEdit = true;
        }

        return sp;
    }

    private UIUserSummary getUIUserSummary(User u, ApplicationContext context) {
        UIUserSummary user1 = new UIUserSummary();
        user1.id = "" + u.getUserSeqId();
        user1.username = u.getUserName();
        user1.firstname = u.getFirstName();
        user1.lastname = u.getLastName();
        user1.canedit = true;

        if (u.getUserSeqId() == UsersController.ADMIN_USER_SEQ) { // cannot delete admin user.
            user1.candelete = false;
        }
        user1.state = u.isEnabled() ? context.getMessage("enabled", null,
                                                         LocaleContextHolder.getLocale()) : context.getMessage(
                "disabled", null, LocaleContextHolder.getLocale());
        user1.roleDisplay = u.getRole();

        return user1;
    }

    private UIUserSummary getUIUserSummary(GroupUser u, ApplicationContext context) {
        UIUserSummary user1 = new UIUserSummary();
        user1.id = "" + u.getUserSeqId();
        user1.username = u.getUserName();
        user1.firstname = u.getFirstName();
        user1.lastname = u.getLastName();
        user1.canedit = true;

        if (u.getUserSeqId() == UsersController.ADMIN_USER_SEQ) {// cannot delete admin user.
            user1.candelete = false;
        }
        user1.state = u.isEnabled() ? context.getMessage("enabled", null,
                                                         LocaleContextHolder.getLocale()) : context.getMessage(
                "disabled", null, LocaleContextHolder.getLocale());
        user1.roleDisplay = u.getRole();

        return user1;
    }

    private GroupUser createUser(UIUserSummary u) {
        GroupUser user1 = new GroupUser();
        long iID = -1;
        try {
            if (u.id != null && u.id.length() > 0) {
                iID = Integer.parseInt(u.id);
            }
        } catch (Exception e) {
            log.error("Failed to parse userID " + u.id, e);
        }

        if (iID != -1) {
            user1.setUserSeqId(iID);
        }

        user1.setUserName(u.username);
        user1.setFirstName(u.firstname);
        user1.setLastName(u.lastname);

        return user1;
    }
}
