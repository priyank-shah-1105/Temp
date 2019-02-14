/**
 * DELL INC. PROPRIETARY INFORMATION: This software is supplied under the terms of a 
 * license agreement or nondisclosure agreement with Dell Inc. and may not be copied 
 * or disclosed except in accordance with the terms of that agreement. 
 * Copyright (c) 2010-2011 Dell Inc. All Rights Reserved.
 */
package com.dell.asm.ui.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Class HtmlController.
 */
@Controller
public class ViewController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(ViewController.class);

    @RequestMapping(value = "/")
    public String getIndexPage() {
        return "login.html";
    }

}
