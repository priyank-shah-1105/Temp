/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.service;

import java.util.List;

import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryGroup;
import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryImportEntity;
import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryUser;
import com.dell.asm.asmcore.user.model.AddADGroupUsersRequest;
import com.dell.asm.asmcore.user.model.AddADGroupUsersResponse;


/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Sood </a>
 *
 * Dec 16, 2013 3:13:39 PM
 */
public interface ActiveDirectoryUserServiceAdapter {


    /**
     *
     * @param activeDirectoryName
     * @param sort
     * @param filter
     * @param offset
     * @param limit
     * @return
     */
    public ResourceList<ActiveDirectoryUser> getActiveDirectoryUsers(String activeDirectoryName,
                                                                     String sort,
                                                                     List<String> filter,
                                                                     Integer offset, Integer limit);


    /**
     *
     * @param activeDirectoryName
     * @param sort
     * @param filter
     * @param offset
     * @param limit
     * @return
     */
    public ResourceList<ActiveDirectoryImportEntity> getActiveDirectoryImportEntities(
            String activeDirectoryName, String sort, List<String> filter, Integer offset,
            Integer limit);

    /**
     *
     * @param activeDirectoryName
     * @param sort
     * @param filter
     * @param offset
     * @param limit
     * @return
     */
    public ResourceList<ActiveDirectoryGroup> getActiveDirectoryGroupSummaries(
            String activeDirectoryName, String sort, List<String> filter, Integer offset,
            Integer limit, String searchString);

    /**
     *
     * @param activeDirectoryName
     * @param sort
     * @param filter
     * @param offset
     * @param limit
     * @return
     */
    public ResourceList<ActiveDirectoryGroup> getActiveDirectoryGroupDetails(
            String activeDirectoryName, String sort, List<String> filter, Integer offset,
            Integer limit, String groupDN);

    /**
     *
     * @param requestBody A list of GroupDN, Role pairs for which users are to be created
     * @return
     */
    public AddADGroupUsersResponse createUsersInADGroups(AddADGroupUsersRequest requestBody);


}
