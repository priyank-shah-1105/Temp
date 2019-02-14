package com.dell.asm.ui.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.alcm.client.model.ASMVersion;
import com.dell.asm.asmcore.asmmanager.client.setting.Setting;
import com.dell.asm.ui.adapter.service.ApplianceServiceAdapter;
import com.dell.asm.ui.adapter.service.SettingServiceAdapter;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.JobSessionRequest;
import com.dell.asm.ui.model.UIAboutData;
import com.dell.asm.ui.model.UILoginResult;
import com.dell.asm.ui.model.UISitewideFunctions;

/**
 * The Class UtilityController.
 */
@RestController
public class UtilityController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(UtilityController.class);

    private ApplianceServiceAdapter applianceServiceAdapter;
    private SettingServiceAdapter settingServiceAdapter;

    @Autowired
    public UtilityController(ApplianceServiceAdapter applianceServiceAdapter,
                             SettingServiceAdapter settingServiceAdapter) {
        this.applianceServiceAdapter = applianceServiceAdapter;
        this.settingServiceAdapter = settingServiceAdapter;
    }

    /**
     * Do logout.
     *
     * @param session the session
     * @return the job response
     * @throws ServletException the servlet exception
     * @throws IOException      Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "/login/dologout")
    public @ResponseBody
    JobResponse doLogout(HttpSession session) {
        log.debug("doLogout() - Attempting to Logout");
        JobResponse jobResponse = new JobResponse();
        session.invalidate();
        SecurityContextHolder.clearContext();
        SecurityContextHolder.createEmptyContext();
        UILoginResult loginResult = new UILoginResult();
        loginResult.route = "";
        loginResult.success = true;
        loginResult.url = "login.html";
        jobResponse.responseObj = loginResult;
        return jobResponse;
    }

    /**
     * Gets the data.
     *
     * @return the data
     * @throws ServletException the servlet exception
     * @throws IOException      Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "/sitewidefunctions/getdata", method = RequestMethod.GET)
    public @ResponseBody
    JobResponse getData() {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = new UISitewideFunctions();
        return jobResponse;
    }

    /**
     * Gets the about data.
     *
     * @return the about data
     * @throws ServletException the servlet exception
     * @throws IOException      Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "/about/getaboutdata", method = RequestMethod.POST)
    public @ResponseBody
    JobResponse getAboutData() {
        JobResponse jobResponse = new JobResponse();
        UIAboutData responseObj = new UIAboutData();

        ASMVersion asmversion = applianceServiceAdapter.getASMVersion();

        if (asmversion != null) {
            responseObj = new UIAboutData();
            responseObj.applicationname = getApplicationContext().getMessage(
                    "About.ApplicationName", null, LocaleContextHolder.getLocale());
            responseObj.version = getApplicationContext().getMessage("About.Version", null,
                                                                     LocaleContextHolder.getLocale()) + " " + asmversion.getVersion();
            responseObj.build = getApplicationContext().getMessage("About.Build", null,
                                                                   LocaleContextHolder.getLocale()) + " " + asmversion.getBuildNumber();
            responseObj.copyright = getApplicationContext().getMessage("About.Copyright", null,
                                                                       LocaleContextHolder.getLocale());
            responseObj.patent = getApplicationContext().getMessage("About.Patent", null,
                                                                    LocaleContextHolder.getLocale());
            responseObj.trademark = getApplicationContext().getMessage("About.Trademark", null,
                                                                       LocaleContextHolder.getLocale());
            responseObj.opensource = getApplicationContext().getMessage("About.Opensource", null,
                                                                        LocaleContextHolder.getLocale());
            responseObj.legal = getApplicationContext().getMessage("About.Legal", null,
                                                                   LocaleContextHolder.getLocale());

            Setting setting = settingServiceAdapter.getSettingByName(
                    UIAboutData.ASM_SERVICE_TAG_SETTING);
            if (setting != null) {
                responseObj.serviceTag = setting.getValue();
            }

            responseObj.contributors.add("VxFLex Manager Team");
        }

        jobResponse.responseObj = responseObj;
        return jobResponse;
    }

    /**
     * Update session.
     *
     * @return the data
     * @throws ServletException the servlet exception
     * @throws IOException      Signals that an I/O exception has occurred.
     */
    @RequestMapping(value = "/session/updatesession", method = RequestMethod.GET)
    public @ResponseBody
    JobResponse updateSession(@RequestBody JobSessionRequest request) {
        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = request.requestObj;

        return jobResponse;
    }
}
