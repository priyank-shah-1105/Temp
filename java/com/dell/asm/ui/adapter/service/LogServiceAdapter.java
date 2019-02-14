/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied
 * or disclosed except in accordance with the terms of that agreement.
 * Copyright (c) 2010-2013 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.adapter.service;

import java.io.File;
import java.util.List;

import com.dell.asm.localizablelogger.LocalizedLogMessage;

/**
 * The Interface LogServiceAdapter.
 */
public interface LogServiceAdapter {

    public ResourceList<LocalizedLogMessage> getUserLogMessages(
            String sort,
            List<String> filter,
            Integer offset,
            Integer limit);

    public void exportAllUserLogMessages(final File downloadFile);

    public Integer purgeLogs(String olderThan, String severity);
}
