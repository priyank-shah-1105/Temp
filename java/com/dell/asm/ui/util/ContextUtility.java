/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2011 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.util;

import javax.servlet.http.HttpServletResponse;

/**
 * Utility class for utilities methods.
 */
public final class ContextUtility {

    private ContextUtility() {
    }

    public static void setCacheControlHeaders(HttpServletResponse servletResponse) {
        servletResponse.addHeader("Cache-Control",
                                  "no-store, no-cache");
        servletResponse.addHeader("Pragma",
                                  "no-cache");
    }
}
