/**************************************************************************
 *   Copyright (c) 2013 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/

package com.dell.asm.ui.adapter.service;

/**
 * Simple generic type for containing a list of resources and a total records
 * count.
 *
 * Since this class is a simple generic and is intended to be used
 * to simply pass the list and record count from the service to the
 * consumer we can suppress the sonar warning about direct storage
 * of an array. If the intended use changes or is made more
 * broad then this can be revisited.
 */
@SuppressWarnings("PMD.ArrayIsStoredDirectly")
public class ResourceList<T> {

    private long totalRecords;
    private T[] list;

    public ResourceList(T[] list, long totalRecords) {
        this.list = list;
        this.totalRecords = totalRecords;
    }

    public T[] getList() {
        return list;
    }

    public long getTotalRecords() {
        return totalRecords;
    }
}
