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
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryGroup;
import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryImportEntity;
import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryUser;
import com.dell.asm.asmcore.user.model.AddADGroupUsersItem;
import com.dell.asm.asmcore.user.model.AddADGroupUsersRequest;
import com.dell.asm.asmcore.user.model.AddADGroupUsersResponse;
import com.dell.asm.asmcore.user.model.User;
import com.dell.asm.localizablelogger.LocalizableMessageService;
import com.dell.asm.localizablelogger.LogMessage;
import com.dell.asm.ui.ASMUIMessages;
import com.dell.asm.ui.adapter.service.ActiveDirectoryUserServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.UserManagementServiceAdapter;
import com.dell.asm.ui.model.FieldError;
import com.dell.asm.ui.model.FilterObj;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.activedirectory.UIActiveDirectoryGroup;
import com.dell.asm.ui.model.activedirectory.UIActiveDirectoryUser;
import com.dell.asm.ui.model.users.JobSaveImportUsersRequest;
import com.dell.asm.ui.util.ContextUtility;
import com.dell.asm.ui.util.MappingUtils;

/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood</a>
 *
 * Oct 31, 20132:43:47 PM
 */
@RestController
public class ActiveDirectoryUserController extends BaseController {

    private static final Logger logger = Logger.getLogger(ActiveDirectoryUserController.class);

    public static String ROLE_ADMIN = "Administrator";
    public static String ROLE_READ_ONLY = "ReadOnly";
    public static String ROLE_OPERATOR = "standard";
    public static String ROLE_ADMIN_DISPLAY = "Administrator";
    public static String ROLE_READ_ONLY_DISPLAY = "Read only";
    public static String ROLE_OPERATOR_DISPLAY = "Standard";
    private static List<UIActiveDirectoryUser> uIActiveDirectoryUserList;
    private static List<UIActiveDirectoryGroup> uIActiveDirectoryGroupList;
    private UserManagementServiceAdapter userManagementServiceAdapter;
    private ActiveDirectoryUserServiceAdapter activeDirectoryUserServiceAdapter;

    @Autowired
    public ActiveDirectoryUserController(UserManagementServiceAdapter userManagementServiceAdapter,
                                         ActiveDirectoryUserServiceAdapter activeDirectoryUserServiceAdapter) {
        this.userManagementServiceAdapter = userManagementServiceAdapter;
        this.activeDirectoryUserServiceAdapter = activeDirectoryUserServiceAdapter;
    }

    /**
     * Get display version of role name.
     * @param roleID
     * @return
     */
    public static String getRoleNameDisplay(String roleID) {
        if (roleID.compareToIgnoreCase(ROLE_READ_ONLY) == 0) {
            return ROLE_READ_ONLY_DISPLAY;
        }

        if (roleID.compareToIgnoreCase(ROLE_ADMIN) == 0) {
            return ROLE_ADMIN_DISPLAY;
        }
        if (roleID.compareToIgnoreCase(ROLE_OPERATOR) == 0) {
            return ROLE_OPERATOR_DISPLAY;
        }
        return "";
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
    @RequestMapping(value = "/users/getdirectoryusers", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDirectoryUsers(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIActiveDirectoryUser> uIActiveDirectoryUserList = new ArrayList<>();
        jobResponse.responseObj = uIActiveDirectoryUserList;

        // Process request filter info
        String activeDirectoryName = null;
        List<String> filter = null;
        if (request.criteriaObj.filterObj.size() == 2) {
            for (FilterObj filterObj : request.criteriaObj.filterObj) {
                if (filterObj != null && filterObj.opTarget != null && filterObj.opTarget.size() > 0) {
                    String fieldName = filterObj.field;
                    if (StringUtils.isNotEmpty(fieldName)) {
                        if ("server".equalsIgnoreCase(fieldName)) {
                            activeDirectoryName = filterObj.opTarget.get(0);
                        } else if ("keyword".equalsIgnoreCase(fieldName)) {
                            String searchString = filterObj.opTarget.get(0);
                            filter = new ArrayList<>();
                            filter.add("co,name," + searchString);
                        }
                    }
                }
            }
        }

        if (activeDirectoryName != null) {
            try {
                RESTRequestOptions options = new RESTRequestOptions(
                        request.criteriaObj,
                        MappingUtils.COLUMNS_DIRECTORIE_USERS, null);

                ResourceList<ActiveDirectoryImportEntity> list = activeDirectoryUserServiceAdapter.getActiveDirectoryImportEntities(
                        activeDirectoryName,
                        "name",
                        filter,
                        options.offset < 0 ? null : options.offset,
                        options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);

                if (list != null) {
                    if (list.getList().length > 0) {
                        for (ActiveDirectoryImportEntity activeDirectoryImportEntity : list.getList()) {
                            UIActiveDirectoryUser uiActiveDirectoryUser = getUserJSONObject(
                                    activeDirectoryImportEntity,
                                    activeDirectoryName);

                            uIActiveDirectoryUserList.add(uiActiveDirectoryUser);
                        }
                    } else {
                        if (list.getTotalRecords() > 0
                                && request.criteriaObj != null
                                && request.criteriaObj.currentPage > 0
                                && options.offset > 0) {
                            // ask previous page recursively until currentPage =
                            // 0
                            JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                        (int) list.getTotalRecords());
                            return getDirectoryUsers(newRequest);
                        }
                    }
                }

                jobResponse.criteriaObj = request.criteriaObj;
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null && list != null) {
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) list.getTotalRecords();
                }
            } catch (Throwable t) {
                logger.error("getDirectoryUsers() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }

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
    @RequestMapping(value = "/groups/getdirectorygroupsummaries", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDirectoryGroupSummaries(@RequestBody JobIDRequest request) {

        String searchString = null;

        if (request.requestObj.id != null && !request.requestObj.id.isEmpty()) {
            searchString = request.requestObj.id;
        }
        JobResponse jobResponse = new JobResponse();
        uIActiveDirectoryGroupList = new ArrayList<>();
        jobResponse.responseObj = uIActiveDirectoryGroupList;

        String activeDirectoryName = null;
        if (request.criteriaObj.filterObj.size() > 0) {
            FilterObj filterObj = request.criteriaObj.filterObj.get(0);
            if (filterObj != null && filterObj.opTarget != null && filterObj.opTarget.size() > 0)
                activeDirectoryName = filterObj.opTarget.get(0);
        }

        if (activeDirectoryName != null) {

            try {
                RESTRequestOptions options = new RESTRequestOptions(
                        request.criteriaObj,
                        MappingUtils.COLUMNS_DIRECTORIE_GROUPS, null);
                // All kind of directory doesn't support sorting on different
                // parameters API support "name" as sorting so this is hardcoded here
                ResourceList<ActiveDirectoryGroup> list = activeDirectoryUserServiceAdapter.getActiveDirectoryGroupSummaries(
                        activeDirectoryName,
                        "name",
                        null,
                        options.offset < 0 ? null : options.offset,
                        options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit,
                        searchString);

                if (list != null) {
                    if (list.getList().length > 0) {
                        for (ActiveDirectoryGroup activeDirectoryGroup : list.getList()) {
                            UIActiveDirectoryGroup uiActiveDirectoryGroup = getGroupJSONObject(
                                    activeDirectoryGroup,
                                    activeDirectoryName);

                            uIActiveDirectoryGroupList.add(uiActiveDirectoryGroup);
                        }
                    } else {
                        if (list.getTotalRecords() > 0
                                && request.criteriaObj != null
                                && request.criteriaObj.currentPage > 0
                                && options.offset > 0) {
                            // ask previous page recursively until currentPage =
                            // 0
                            JobIDRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                          (int) list.getTotalRecords());
                            return getDirectoryGroupSummaries(newRequest);
                        }
                    }
                }

                jobResponse.criteriaObj = request.criteriaObj;
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null && list != null) {
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) list.getTotalRecords();
                }
            } catch (Throwable t) {
                logger.error("getDirectoryGroups() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }

        }
        return jobResponse;
    }

    /**
     * Retrieves all AD users for a given group specified as a query parameter on the request
     * @param request
     * @return
     * @throws ServletException
     * @throws IOException
     */
    @RequestMapping(value = "/users/getgroupdetails", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDirectoryGroupDetails(@RequestBody JobIDRequest request) {
        String groupDN = null;

        JobResponse jobResponse = new JobResponse();
        ActiveDirectoryGroup uIActiveDirectoryGroup = null;
        jobResponse.responseObj = uIActiveDirectoryGroupList;
        String groupId = null;
        String activeDirectoryName = null;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(request.requestObj.id)) {
            groupId = request.requestObj.id;
            String[] idFields = groupId.split("###");
            activeDirectoryName = idFields[0];
            groupDN = idFields[1];
        }

        if (groupId != null) {

            try {
                RESTRequestOptions options = new RESTRequestOptions(
                        request.criteriaObj,
                        MappingUtils.COLUMNS_DIRECTORIE_GROUPS, null);
                // All kind of directory doesn't support sorting on different
                // parameters API support "firstName" as sorting so this is hardcoded here
                ResourceList<ActiveDirectoryGroup> list = activeDirectoryUserServiceAdapter.getActiveDirectoryGroupDetails(
                        activeDirectoryName,
                        "name",
                        null,
                        options.offset < 0 ? null : options.offset,
                        options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit,
                        groupDN);

                if (list != null) {
                    ActiveDirectoryGroup activeDirectoryGroup = list.getList()[0];
                    UIActiveDirectoryGroup uiActiveDirectoryGroup = getGroupJSONObject(
                            activeDirectoryGroup, activeDirectoryName);
                    jobResponse.responseObj = uiActiveDirectoryGroup;
                }

                jobResponse.criteriaObj = request.criteriaObj;
                if (request.criteriaObj != null && request.criteriaObj.paginationObj != null && list != null) {
                    jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) list.getTotalRecords();
                }
            } catch (Throwable t) {
                logger.error("getDirectoryGroupDetails() - Exception from service call", t);
                jobResponse = addFailureResponseInfo(t);
            }

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
    @RequestMapping(value = "//users/getdirectoryuserbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDirectoryUserById(@RequestBody JobIDRequest request) {

        JobResponse jobResponse = new JobResponse();

        try {

            if (request.requestObj.id != null && !request.requestObj.id.isEmpty()) {
                for (UIActiveDirectoryUser user : uIActiveDirectoryUserList) {
                    if (request.requestObj.id.equalsIgnoreCase(user.id)) {
                        jobResponse.responseObj = user;
                    }
                }
                return jobResponse;

            }
        } catch (Throwable t) {
            logger.error("getDirectoryUserById() - Exception from service call", t);
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
    @RequestMapping(value = "/users/getimportroles", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getImportRoles(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIListItem> responseObj = new ArrayList<>();
        responseObj.add(new UIListItem(ROLE_READ_ONLY, ROLE_READ_ONLY_DISPLAY));
        responseObj.add(new UIListItem(ROLE_ADMIN, ROLE_ADMIN_DISPLAY));
        responseObj.add(new UIListItem(ROLE_OPERATOR, ROLE_OPERATOR_DISPLAY));
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
    @RequestMapping(value = "/users/saveimportdirectoryusers", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse saveADUsers(@RequestBody JobSaveImportUsersRequest request) {
        List<FieldError> errorFields = new ArrayList<>();
        JobResponse jobResponse = new JobResponse();
        AddADGroupUsersResponse addADGroupUsersResponse = new AddADGroupUsersResponse();

        int successes = 0;
        int conflicts = 0;
        int failures = 0;

        List<UIActiveDirectoryUser> uiSaveUserList = new ArrayList<>();
        List<UIActiveDirectoryUser> uISaveGroupList = new ArrayList<>();
        List<UIActiveDirectoryUser> uiImportSaveUsers = request.requestObj;

        for (UIActiveDirectoryUser importEntity : uiImportSaveUsers) {
            if (importEntity.isGroup) {
                uISaveGroupList.add(importEntity);
            } else {
                uiSaveUserList.add(importEntity);
            }
        }

        // First, save the individual users
        if (CollectionUtils.isNotEmpty(uiSaveUserList)) {
            for (UIActiveDirectoryUser uiActiveDirectoryUser : uiSaveUserList) {
                User user = getUserFromActiveDirectoryUser(uiActiveDirectoryUser);
                try {
                    userManagementServiceAdapter.createUser(user);
                    successes++;
                    logger.info(
                            "AD User '" + user.getUserName() + "' in domain '" + user.getDomainName() + "'");
                } catch (WebApplicationException wae) {
                    // Check which exception occured ... an
                    if (Response.Status.CONFLICT.getStatusCode() == wae.getResponse().getStatus()) {
                        conflicts++;
                        logger.info(
                                "AD User '" + user.getUserName() + "' in domain '" + user.getDomainName() + "' NOT imported because an existing VxFM user exists");
                    } else {
                        failures++;
                        logger.error(
                                "AD User '" + user.getUserName() + "' in domain '" + user.getDomainName() + "' NOT imported due to an exception: " + wae.getMessage());
                    }
                } catch (Throwable throwable) {
                    logger.error("saveADUsers() - Exception from service call", throwable);
                    jobResponse = addFailureResponseInfo(throwable);
                    List<FieldError> errorFieldsSingleTransaction = jobResponse.errorObj
                            .getErrorFields();
                    errorFields.addAll(errorFieldsSingleTransaction);
                }
            }
        }

        // Next save the users each chosen group
        if (CollectionUtils.isNotEmpty(uISaveGroupList)) {
            AddADGroupUsersRequest groupSaveRequest = new AddADGroupUsersRequest();
            groupSaveRequest.setGroupList(new ArrayList<>());
            // Create the request containing all group info
            for (UIActiveDirectoryUser uiActiveDirectoryGroup : uISaveGroupList) {
                // Create the new group item for the request
                AddADGroupUsersItem group = new AddADGroupUsersItem();
                group.setAdServerName(uiActiveDirectoryGroup.serverName);
                group.setDn(uiActiveDirectoryGroup.distinguishedName);
                group.setRole(uiActiveDirectoryGroup.roleId);
                groupSaveRequest.getGroupList().add(group);
            }
            try {
                addADGroupUsersResponse = activeDirectoryUserServiceAdapter.createUsersInADGroups(
                        groupSaveRequest);
            } catch (Throwable throwable) {
                logger.error("saveADUsers() - Exception from service call", throwable);
                jobResponse = addFailureResponseInfo(throwable);
                List<FieldError> errorFieldsSingleTransaction = jobResponse.errorObj
                        .getErrorFields();
                errorFields.addAll(errorFieldsSingleTransaction);
            }
        }

        if (jobResponse.errorObj != null) {
            jobResponse.errorObj.fldErrors.clear();
            jobResponse.errorObj.fldErrors.addAll(errorFields);
        }

        // Add in any stats for group add calls to stats from
        // calls to add individual users previously recorded
        successes += addADGroupUsersResponse.getSuccesses();
        conflicts += addADGroupUsersResponse.getConflicts();
        failures += addADGroupUsersResponse.getFaiures();

        Object[] msgArgs = { successes, conflicts, failures };
        if (failures != 0) {
            // Return an error object to the UI if we have failures; i.e., conflicts are not considered an error
            addErrorMessage(jobResponse, msgArgs, "UserImport.ImportSummary");
        }

        // Log a message with summary info for import operation
        LogMessage.LogSeverity severity = failures == 0 ? LogMessage.LogSeverity.INFO : LogMessage.LogSeverity.ERROR;
        LocalizableMessageService.getInstance()
                .logMsg(ASMUIMessages.usersImported(successes, conflicts,
                                                    failures).getDisplayMessage(), severity);

        // Combine user and group list for response object body
        List<UIBaseObject> jobResponseObject = new ArrayList<>();
        jobResponseObject.addAll(uiSaveUserList);
        jobResponseObject.addAll(uISaveGroupList);

        jobResponse.responseObj = jobResponseObject;

        return jobResponse;
    }

    /**
     *
     * @param activeDirectoryImportEntity
     * @param activeDirectoryName
     * @return
     */
    private UIActiveDirectoryUser getUserJSONObject(
            ActiveDirectoryImportEntity activeDirectoryImportEntity, String activeDirectoryName) {

        UIActiveDirectoryUser uiActiveDirectoryUser = new UIActiveDirectoryUser();
        uiActiveDirectoryUser.id = activeDirectoryImportEntity.getId();
        uiActiveDirectoryUser.name = activeDirectoryImportEntity.getName();
        uiActiveDirectoryUser.distinguishedName = activeDirectoryImportEntity.getDn();
        uiActiveDirectoryUser.isGroup = activeDirectoryImportEntity.isGroup();
        if (activeDirectoryImportEntity.isGroup()) {
            uiActiveDirectoryUser.groupName = activeDirectoryImportEntity.getName();
            uiActiveDirectoryUser.group = activeDirectoryImportEntity.getDn();
        } else {
            uiActiveDirectoryUser.username = activeDirectoryImportEntity.getUsername();
            uiActiveDirectoryUser.firstname = activeDirectoryImportEntity.getFirstname();
            uiActiveDirectoryUser.lastname = activeDirectoryImportEntity.getLastname();
            uiActiveDirectoryUser.email = activeDirectoryImportEntity.getEmail();
            uiActiveDirectoryUser.roleId = ROLE_READ_ONLY;
            uiActiveDirectoryUser.roleDisplay = getRoleNameDisplay(uiActiveDirectoryUser.roleId);
            uiActiveDirectoryUser.rolename = getRoleNameDisplay(uiActiveDirectoryUser.roleId);
        }


        uiActiveDirectoryUser.serverName = activeDirectoryName;


        return uiActiveDirectoryUser;
    }

    /**
     *
     * @param activeDirectoryUser
     * @param activeDirectoryName
     * @return
     */
    private UIActiveDirectoryUser getUserJSONObject(ActiveDirectoryUser activeDirectoryUser,
                                                    String activeDirectoryName) {

        UIActiveDirectoryUser uiActiveDirectoryUser = new UIActiveDirectoryUser();
        uiActiveDirectoryUser.id = activeDirectoryUser.getUserId();
        uiActiveDirectoryUser.roleId = ROLE_READ_ONLY;
        uiActiveDirectoryUser.firstname = activeDirectoryUser.getFirstName();
        uiActiveDirectoryUser.lastname = activeDirectoryUser.getLastName();
        uiActiveDirectoryUser.username = activeDirectoryUser.getUsername();
        uiActiveDirectoryUser.email = activeDirectoryUser.getEmail();
        uiActiveDirectoryUser.serverName = activeDirectoryName;
        uiActiveDirectoryUser.distinguishedName = activeDirectoryUser.getDn();
        uiActiveDirectoryUser.roleDisplay = getRoleNameDisplay(uiActiveDirectoryUser.roleId);
        uiActiveDirectoryUser.rolename = getRoleNameDisplay(uiActiveDirectoryUser.roleId);
        uiActiveDirectoryUser.username = activeDirectoryUser.getUsername();
        return uiActiveDirectoryUser;
    }

    /**
     *
     * @param activeDirectoryGroup
     * @param activeDirectoryName
     * @return
     */
    private UIActiveDirectoryGroup getGroupJSONObject(ActiveDirectoryGroup activeDirectoryGroup,
                                                      String activeDirectoryName) {

        UIActiveDirectoryGroup uiActiveDirectoryGroup = new UIActiveDirectoryGroup();
        uiActiveDirectoryGroup.id = activeDirectoryGroup.getId();
        uiActiveDirectoryGroup.name = activeDirectoryGroup.getName();
        uiActiveDirectoryGroup.description = activeDirectoryGroup.getDescription();
        uiActiveDirectoryGroup.serverName = activeDirectoryName;
        uiActiveDirectoryGroup.distinguishedName = activeDirectoryGroup.getDn();

        List<ActiveDirectoryUser> userList = activeDirectoryGroup.getUserList();
        if (null != userList && userList.size() > 0) {
            uiActiveDirectoryGroup.users = new ArrayList<>();
            for (ActiveDirectoryUser adUser : userList) {
                UIActiveDirectoryUser user = getUserJSONObject(adUser, activeDirectoryName);
                user.group = activeDirectoryGroup.getDn();
                user.groupName = activeDirectoryGroup.getName();
                uiActiveDirectoryGroup.users.add(user);
            }
        } else {
            uiActiveDirectoryGroup.users = null;
        }
        uiActiveDirectoryGroup.distinguishedName = activeDirectoryGroup.getDn();

        return uiActiveDirectoryGroup;
    }


    /**
     *
     * @param activeDirectoryUser
     * @return
     */
    private User getUserFromActiveDirectoryUser(UIActiveDirectoryUser activeDirectoryUser) {
        User user = new User();
        user.setUserName(activeDirectoryUser.name);
        user.setFirstName(activeDirectoryUser.firstname);
        user.setLastName(activeDirectoryUser.lastname);
        user.setUserName(activeDirectoryUser.username);

        if (activeDirectoryUser.email.isEmpty()) {
            // since email is mandatory field if AD doesn't return email set it
            // to default.
            user.setEmail("default@asm.com");
        } else {
            user.setEmail(activeDirectoryUser.email);

        }

        if (ROLE_ADMIN.equalsIgnoreCase(activeDirectoryUser.roleId))
            user.setRole(ROLE_ADMIN);
        else if (ROLE_OPERATOR.equalsIgnoreCase(activeDirectoryUser.roleId))
            user.setRole(ROLE_OPERATOR);
        else if (ROLE_READ_ONLY.equalsIgnoreCase(activeDirectoryUser.roleId))
            user.setRole(ROLE_READ_ONLY);

        user.setPassword("Passw0rd");
        user.setDomainName(activeDirectoryUser.serverName);
        user.setSystemUser(false);

        return user;
    }
}
