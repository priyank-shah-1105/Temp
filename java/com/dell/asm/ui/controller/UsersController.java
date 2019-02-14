/**************************************************************************
 *   Copyright (c) 2013 - 2015 Dell Inc. All rights reserved.             *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.asmcore.user.model.User;
import com.dell.asm.rest.common.AsmConstants;
import com.dell.asm.ui.adapter.impl.RESTUserManagementServiceAdapter;
import com.dell.asm.ui.adapter.service.DeploymentServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.adapter.service.TemplateServiceAdapter;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobStringRequest;
import com.dell.asm.ui.model.JobStringsRequest;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.activedirectory.UIActiveDirectoryGroupSummary;
import com.dell.asm.ui.model.credential.UICredential;
import com.dell.asm.ui.model.users.JobUserRequest;
import com.dell.asm.ui.model.users.UIUser;
import com.dell.asm.ui.model.users.UIUserSummary;
import com.dell.asm.ui.spring.ASMUserDetails;
import com.dell.asm.ui.util.ContextUtility;
import com.dell.asm.ui.util.MappingUtils;

/**
 * The Class UsersController.
 */
@RestController
@RequestMapping(value = "/users/")
public class UsersController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(UsersController.class);

    public static String ROLE_ADMIN = "Administrator";
    public static String ROLE_READ_ONLY = "ReadOnly";
    public static String ROLE_STANDARD = "standard";

    public static String DEFAULT_DOMAIN = "VXFMLOCAL";
    public static int ADMIN_USER_SEQ = 1;

    private RESTUserManagementServiceAdapter userManagementAdapter;
    private TemplateServiceAdapter templateServiceAdapter;
    private DeploymentServiceAdapter serviceAdapter;

    @Autowired
    public UsersController(RESTUserManagementServiceAdapter userManagementAdapter,
                           TemplateServiceAdapter templateServiceAdapter,
                           DeploymentServiceAdapter serviceAdapter) {
        this.userManagementAdapter = userManagementAdapter;
        this.templateServiceAdapter = templateServiceAdapter;
        this.serviceAdapter = serviceAdapter;
    }

    /**
     * Get display version of role name.
     * @param roleID
     * @return
     */
    public static String getRoleNameDisplay(String roleID, ApplicationContext applicationContext) {
        return applicationContext.getMessage("rolesDisplay." + roleID, null,
                                             LocaleContextHolder.getLocale());
    }

    private static UIActiveDirectoryGroupSummary getJSONGroupSummary(User u) {
        if (com.dell.asm.rest.common.util.StringUtils.isEmpty(u.getGroupDN())) {
            return null;
        }
        UIActiveDirectoryGroupSummary summary = new UIActiveDirectoryGroupSummary();
        summary.id = u.getGroupName();
        summary.name = u.getGroupName();
        return summary;
    }

    private static UIUserSummary getJSONObjectSummary(User u,
                                                      ApplicationContext applicationContext) {
        UIUserSummary user1 = new UIUserSummary();
        user1.id = "" + u.getUserSeqId();
        user1.username = u.getUserName();
        user1.roleId = u.getRole();
        user1.roleDisplay = getRoleNameDisplay(u.getRole(), applicationContext);
        user1.firstname = u.getFirstName();
        user1.lastname = u.getLastName();
        user1.groupName = u.getGroupName();
        user1.groupDN = u.getGroupDN();
        user1.enabled = u.isEnabled();
        user1.canedit = true;
        user1.serverName = u.getDomainName();
        user1.lastlogin = MappingUtils.getTime(u.getUpdatedDate());

        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String cUserName;

        if (userDetails.getUsername().contains("/")) {
            String[] split = userDetails.getUsername().split("/");
            cUserName = split[1];
        } else {
            cUserName = userDetails.getUsername();
        }

        user1.candelete = !u.isSystemUser() && !user1.username.equals(
                cUserName) && u.getUserSeqId() != ADMIN_USER_SEQ;

        if (u.isEnabled()) {
            user1.state = "Enabled";
        } else {
            user1.state = "Disabled";
        }

        return user1;
    }

    public static UIUser getJSONObject(User u, ApplicationContext applicationContext) {
        UIUser user1 = new UIUser();
        user1.id = "" + u.getUserSeqId();
        user1.username = u.getUserName();
        if (u.getRole() != null) {
            user1.roleId = u.getRole();
        }

        user1.rolename = getRoleNameDisplay(u.getRole(), applicationContext);
        user1.firstname = u.getFirstName();
        user1.serverName = u.getDomainName();
        user1.lastname = u.getLastName();
        user1.enabled = u.isEnabled();
        if (u.getEmail() == null || u.getEmail().length() == 0) {
            user1.email = null;
        } else {
            user1.email = u.getEmail();
        }

        if (u.getPhoneNumber() == null || u.getPhoneNumber().length() == 0) {
            user1.phone = null;
        } else {
            user1.phone = u.getPhoneNumber();
        }

        if (u.isEnabled()) {
            user1.state = "Enabled";
        } else {
            user1.state = "Disabled";
        }

        user1.showdefaultpasswordprompt = u.isUpdatePassword();

        user1.userPreference = u.getUserPreference();

        return user1;
    }

    public static User getApplicationObject(UIUser u) {
        User user1 = new User();
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
        if (!UICredential.CURRENT_PASSWORD.equals(u.password))
            user1.setPassword(u.password);

        user1.setDomainName(DEFAULT_DOMAIN);
        user1.setEnabled(u.enabled);
        user1.setFirstName(u.firstname);
        user1.setLastName(u.lastname);
        if (StringUtils.isBlank(u.email)) {
            user1.setEmail(null);
        } else {
            user1.setEmail(u.email.trim());
        }

        if (StringUtils.isBlank(u.phone)) {
            user1.setPhoneNumber(null);
        } else {
            user1.setPhoneNumber(u.phone.trim());
        }

        if (u.roleId != null) {
            user1.setRole(u.roleId);
        }

        user1.setUpdatePassword(u.showdefaultpasswordprompt);

        user1.setUserPreference(u.userPreference);

        return user1;
    }

    @ModelAttribute
    public void setNoCache(HttpServletResponse servletResponse) {
        ContextUtility.setCacheControlHeaders(servletResponse);
    }

    /**
     * Gets the current user.
     *
     * @param request
     *            the request
     * @return the current user
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getcurrentuser", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getCurrentUser(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            User user = getCurrentUserDetail();
            if (user != null) {
                UIUser responseObj = getJSONObject(user, getApplicationContext());
                jobResponse.responseObj = responseObj;
            }
        } catch (Throwable t) {
            log.error("getCurrentUser() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Gets the current user.
     *
     * @param request
     *            the request
     * @return the current user
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "updateuserpreferences", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse updateUserPreference(@RequestBody JobStringRequest request) {

        JobResponse jobResponse = new JobResponse();
        try {
            User user = getCurrentUserDetail();
            if (user != null) {
                user.setUserPreference(request.requestObj);
                userManagementAdapter.updateUser(Long.toString(user.getUserSeqId()), user);
            }
        } catch (Throwable t) {
            log.error("updateUserPreference() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private User getCurrentUserDetail() {
        User user = null;
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String userName;

        if (userDetails.getUsername().contains("/")) {
            String[] split = userDetails.getUsername().split("/");
            userName = split[1];
        } else if (userDetails.getUsername().contains("\\")) {
            String[] split = userDetails.getUsername().split("\\\\");
            userName = split[1];
        } else {
            userName = userDetails.getUsername();
        }

        List<String> filter = new ArrayList<>();
        filter.add("eq,userName," + userName);
        ResourceList<User> cList = userManagementAdapter.getAllUsers(null, filter, null, null);
        if (cList != null && cList.getList() != null && cList.getList().length > 0) {
            user = cList.getList()[0];
        }
        return user;
    }

    /**
     * Delete user.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "deleteuser", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse deleteUser(@RequestBody JobStringsRequest request) {

        JobResponse jobResponse = new JobResponse();
        Set<String> userIds = new HashSet<>(request.requestObj);
        templateServiceAdapter.deleteUsersFromTemplates(request.requestObj);
        serviceAdapter.deleteUsersFromDeployments(request.requestObj);

        try {
            for (String userId : userIds) {
                userManagementAdapter.deleteUser(userId);
            }
        } catch (Throwable t) {
            log.error("deleteUser() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Disable user.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "disableuser", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse disableUser(@RequestBody JobStringsRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            for (int i = 0; i < request.requestObj.size(); i++) {
                String sID = request.requestObj.get(i);
                //if( sID.compareToIgnoreCase( "" + AdminUserSeqID) != 0 )
                //{
                User usr = userManagementAdapter.getUserByID(request.requestObj.get(i));
                if (usr.isEnabled()) {
                    usr.setEnabled(false);
                    userManagementAdapter.updateUser(sID, usr);
                }
                //}

            }
        } catch (Throwable t) {
            log.error("disableUser() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Enable user.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "enableuser", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse enableUser(@RequestBody JobStringsRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            for (int i = 0; i < request.requestObj.size(); i++) {
                User usr = userManagementAdapter.getUserByID(request.requestObj.get(i));
                if (!usr.isEnabled()) {
                    usr.setEnabled(true);
                    userManagementAdapter.updateUser(request.requestObj.get(i), usr);
                }
            }
        } catch (Throwable t) {
            log.error("enableUser() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    /**
     * Gets the roles.
     *
     * @param request
     *            the request
     * @return the roles
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getroles", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getRoles(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIListItem> responseObj = new ArrayList<>();
        responseObj.add(new UIListItem(ROLE_ADMIN,
                                       getApplicationContext().getMessage(
                                               "rolesDisplay." + ROLE_ADMIN, null,
                                               LocaleContextHolder.getLocale())));
        responseObj.add(new UIListItem(ROLE_STANDARD,
                                       getApplicationContext().getMessage(
                                               "rolesDisplay." + ROLE_STANDARD, null,
                                               LocaleContextHolder.getLocale())));
        responseObj.add(new UIListItem(ROLE_READ_ONLY,
                                       getApplicationContext().getMessage(
                                               "rolesDisplay." + ROLE_READ_ONLY, null,
                                               LocaleContextHolder.getLocale())));
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the user by id.
     *
     * @param request
     *            the request
     * @return the user by id
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getuserbyid", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getUserById(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();
        UIUser responseObj = null;
        try {
            responseObj = getJSONObject(userManagementAdapter.getUserByID(request.requestObj.id),
                                        getApplicationContext());

        } catch (Throwable t) {
            log.error("getUserById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Gets the users.
     *
     * @param request
     *            the request
     * @return the users
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getusers", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getUsers(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIUserSummary> responseObj = new ArrayList<>();

        try {

            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_USERS,
                                                                "username");
            boolean noRO = false;

            if (options.filterList != null) {

                for (String fo : options.filterList) {
                    if (fo.equals("<>,role,readonly")) {
                        noRO = true;
                        break;
                    }
                }

                if (noRO) {
                    options.filterList.clear();
                    options.filterList.add(
                            "eq,role," + AsmConstants.USERROLE_ADMINISTRATOR + "," + AsmConstants.USERROLE_OPERATOR);
                }
            }

            // ASM-1092: Do not paginate if no params specified - wizard can't handle pagination.
            Integer offset = options.offset < 0 ? 0 : options.offset;
            Integer limit = options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit;
            ResourceList<User> list = userManagementAdapter.getAllUsers(options.sortedColumns,
                                                                        options.filterList, offset,
                                                                        limit);

            if (list != null && list.getList() != null) {
                if (list.getList().length > 0) {
                    for (User u : list.getList()) {
                        UIUserSummary user1 = getJSONObjectSummary(u, getApplicationContext());
                        responseObj.add(user1);
                    }
                } else {
                    if (list.getTotalRecords() > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) list.getTotalRecords());
                        return getUsers(newRequest);
                    }
                }
            }
            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null && list != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) list.getTotalRecords();
            }
        } catch (Throwable t) {
            log.error("getUserById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Gets the set of AD group summaries for existing ASM users.
     *
     * @param request
     *            the request
     * @return the users
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "getdirectorygroups", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getDirectoryGroups(@RequestBody JobRequest request) {

        JobResponse jobResponse = new JobResponse();
        Set<UIActiveDirectoryGroupSummary> responseObj = new HashSet<>();

        try {

            RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                MappingUtils.COLUMNS_USERS,
                                                                "username");
            boolean noRO = false;

            if (options.filterList != null) {

                for (String fo : options.filterList) {
                    if (fo.equals("<>,role,readonly")) {
                        noRO = true;
                        break;
                    }
                }

                if (noRO) {
                    options.filterList.clear();
                    options.filterList.add(
                            "eq,role," + AsmConstants.USERROLE_ADMINISTRATOR + "," + AsmConstants.USERROLE_OPERATOR);
                }
            }

            // ASM-1092: Do not paginate if no params specified - wizard can't handle pagination.
            Integer offset = options.offset < 0 ? 0 : options.offset;
            Integer limit = options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit;
            ResourceList<User> list = userManagementAdapter.getAllUsers(options.sortedColumns,
                                                                        options.filterList, offset,
                                                                        limit);

            if (list != null && list.getList() != null) {
                if (list.getList().length > 0) {
                    for (User u : list.getList()) {
                        UIActiveDirectoryGroupSummary summary = getJSONGroupSummary(u);
                        if (null != summary) {
                            responseObj.add(summary);
                        }
                    }
                } else {
                    if (list.getTotalRecords() > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) list.getTotalRecords());
                        return getDirectoryGroups(newRequest);
                    }
                }
            }
            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null && list != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) list.getTotalRecords();
            }
        } catch (Throwable t) {
            log.error("getDirectoryGroups - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    /**
     * Save user.
     *
     * @param request
     *            the request
     * @return the job response
     * @throws ServletException
     *             the servlet exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "saveuser", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse saveUser(@RequestBody JobUserRequest request) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = request.requestObj;

        try {
            final User user = getApplicationObject(request.requestObj);

            final ASMUserDetails userDetails = (ASMUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            // if this is not an update to show the default password prompt then check current pwd for auth
            if (!isShowDefaultPasswordPromptUpdate(request, user)) {
                if (!StringUtils.equals(request.requestObj.currentpassword,
                                        userDetails.getPassword())) {
                    // if the current password passed is not the same as the logged in users password then fail here
                    throw new ControllerException(
                            getApplicationContext().getMessage(
                                    "validationError.currentPasswordNotMatching",
                                    new Object[] {}, LocaleContextHolder.getLocale()));
                }
            }

            if (user.getUserSeqId() != -1 && user.getUserSeqId() != 0) {
                userManagementAdapter.updateUser(request.requestObj.id, user);
            } else {
                userManagementAdapter.createUser(user);
            }
            //if updating current user password must update security context
            if (userDetails.getUsername().equals(user.getUserName()) &&
                    StringUtils.isNotEmpty(user.getPassword())) {
                Collection<SimpleGrantedAuthority> nowAuthorities = (Collection<SimpleGrantedAuthority>)
                        SecurityContextHolder.getContext().getAuthentication().getAuthorities();
                ASMUserDetails updatedUserDetails = new ASMUserDetails(userDetails.getUsername(),
                                                                       user.getPassword(),
                                                                       userDetails.getAuthorities(),
                                                                       userDetails.getAuthToken());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, user.getPassword(), nowAuthorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Throwable t) {
            log.error("saveUser() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    private boolean isShowDefaultPasswordPromptUpdate(final JobUserRequest request,
                                                      final User user) {
        if (request != null && user != null && StringUtils.isNotEmpty(request.requestObj.id)) {
            final User savedUser = userManagementAdapter.getUserByID(request.requestObj.id);
            if (savedUser != null) {
                if (savedUser.isUpdatePassword() != user.isUpdatePassword()) {
                    // if the default prompt password flag is not the same 
                    // then set them to be the same to find out if the users other fields are equal
                    // if they are then this is a default password prompt update
                    savedUser.setUpdatePassword(user.isUpdatePassword());
                    return true;
                }
            }
        }

        return false;
    }
}
