/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.service;

import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.adconfig.model.ActiveDirectoryConfiguration;
import com.dell.asm.asmcore.adconfig.model.ResponseMessage;


/**
 *
 * @author <a href="mailto:Ankur_Sood1@dell.com">Ankur Soo</a>
 *
 * Oct 29, 20131:14:37 PM
 */
public interface ActiveDirectoryConfigMgmtServiceAdapter {

    /**
     *
     * @param activeDirectoryConfiguration
     * @return
     */
    ActiveDirectoryConfiguration create(ActiveDirectoryConfiguration activeDirectoryConfiguration);


    /**
     *
     * @param refId
     * @param activeDirectoryConfiguration
     * @return
     */
    ActiveDirectoryConfiguration update(String refId,
                                        ActiveDirectoryConfiguration activeDirectoryConfiguration);

    /**
     *
     * @param refId
     * @return
     */
    ActiveDirectoryConfiguration get(String refId);

    /**
     *
     * @param refId
     * @return
     */
    Response delete(String refId);

    /**
     *
     * @param filter
     * @param offset
     * @param limit
     * @param sort
     * @return
     */
    // ActiveDirectoryConfigurationList getActiveDirectoryConfigurations(List<String> filter, Integer offset, Integer limit, String sort);

    /**
     *
     * @param refId
     * @return
     */
    ResponseMessage validateActiveDirectoryWithId(String refId);

    /**
     *
     * @param requestVo
     * @return
     */
    ResponseMessage validateActiveDirectoryWithPayLoad(ActiveDirectoryConfiguration requestVo);


    /**
     *
     * @param filter
     * @param offset
     * @param limit
     * @param sort
     * @return
     */
    ActiveDirectoryConfiguration[] getActiveDirectoryConfigurations(List<String> filter,
                                                                    Integer offset, Integer limit,
                                                                    String sort);

}
