/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */

package com.dell.asm.ui.adapter.service;

import com.dell.asm.asmcore.asmmanager.client.credential.AsmCredentialDTO;
import com.dell.asm.asmcore.asmmanager.client.credential.AsmCredentialListDTO;
import com.dell.asm.encryptionmgr.client.CredentialType;
import com.dell.asm.ui.model.credential.UICredential;

/**
 * Interface for Credentials.
 */
public interface CredentialServiceAdapter {

    AsmCredentialDTO createCredential(UICredential credentialData);

    void deleteCredential(String id);

    AsmCredentialListDTO getAllCredentials(CredentialType typeFilter, java.lang.String sort,
                                           java.util.List<java.lang.String> filter,
                                           java.lang.Integer offset, java.lang.Integer limit);

    AsmCredentialDTO getCredential(String id);

    AsmCredentialDTO updateCredential(String id, UICredential credentialData);

}
