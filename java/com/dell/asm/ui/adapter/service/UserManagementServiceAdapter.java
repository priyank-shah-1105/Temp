/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.service;

import com.dell.asm.asmcore.user.model.AddADGroupUsersRequest;
import com.dell.asm.asmcore.user.model.User;

/**
 * Interface for Credentials.
 */
public interface UserManagementServiceAdapter {

    public User createUser(com.dell.asm.asmcore.user.model.User user);

    public User[] createUsersInADGroups(AddADGroupUsersRequest requestBody);

    public ResourceList<User> getAllUsers(java.lang.String sort,
                                          java.util.List<java.lang.String> filter,
                                          java.lang.Integer offset, java.lang.Integer limit);

    public User getUserByID(java.lang.String id);

    public void deleteUser(java.lang.String id);

    public User updateUser(java.lang.String id, User user);

}
