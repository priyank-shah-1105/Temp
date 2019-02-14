package com.dell.asm.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dell.asm.localizablelogger.LocalizedLogMessage;
import com.dell.asm.localizablelogger.LogMessage;
import com.dell.asm.rest.common.AsmConstants;
import com.dell.asm.ui.adapter.service.LogServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UILog;
import com.dell.asm.ui.model.logs.JobPurgeLogsRequest;
import com.dell.asm.ui.spring.ASMUserDetails;
import com.dell.asm.ui.util.MappingUtils;

/**
 * The Class LogsController.
 */
@RestController
@RequestMapping(value = "/logs/")
public class LogsController extends BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(LogsController.class);

    /**
     * The log service adapter.
     */
    private LogServiceAdapter logServiceAdapter;

    @Autowired
    public LogsController(LogServiceAdapter logServiceAdapter) {
        this.logServiceAdapter = logServiceAdapter;
    }

    public static String getLoggedUserName() {
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null && sc.getAuthentication() != null) {
            ASMUserDetails userDetails =
                    (ASMUserDetails) sc.getAuthentication().getPrincipal();

            if (userDetails != null) {
                return userDetails.getUsername();
            }
        }

        return null;
    }

    public static String getLoggedUserRole() {
        SecurityContext sc = SecurityContextHolder.getContext();
        if (sc != null && sc.getAuthentication() != null) {
            ASMUserDetails userDetails =
                    (ASMUserDetails) sc.getAuthentication().getPrincipal();

            if (userDetails != null) {
                String role = "";
                for (GrantedAuthority auth : userDetails.getAuthorities()) {
                    if (auth.getAuthority() != null) {
                        return auth.getAuthority();
                    }
                }
            }
        }

        return AsmConstants.USERROLE_OPERATOR;
    }

    /**
     * Gets the logs.
     *
     * @param request the request
     * @return the logs
     */
    @RequestMapping(value = "getlogs", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse getLogs(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UILog> responseObj = new ArrayList<>();

        String thisUserName = getLoggedUserName();
        String userRole = getLoggedUserRole();

        RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                            MappingUtils.COLUMNS_LOGS, null);
        try {

            List<String> filter = options.filterList;

            if (userRole.equals(AsmConstants.USERROLE_OPERATOR)) {
                String sFilter = "eq,userName";
                sFilter = sFilter + "," + thisUserName;

                if (filter == null)
                    filter = new ArrayList<>();

                filter.add(sFilter);
            }

            if (null == options.sortedColumns) {
                options.sortedColumns = "-timeStamp";
            }

            ResourceList<LocalizedLogMessage> mList = logServiceAdapter.getUserLogMessages(
                    options.sortedColumns, filter,
                    options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);
            if (mList != null && mList.getList() != null) {
                if (mList.getList().length > 0) {
                    for (LocalizedLogMessage dto : mList.getList()) {
                        UILog log = parseLog(dto);
                        responseObj.add(log);
                    }
                } else {
                    if (mList.getTotalRecords() > 0 && request.criteriaObj != null && request.criteriaObj.currentPage > 0 && options.offset > 0) {
                        // ask previous page recursively until currentPage = 0
                        JobRequest newRequest = RESTRequestOptions.switchToPrevPage(request,
                                                                                    (int) mList.getTotalRecords());
                        return getLogs(newRequest);
                    }
                }
            }

            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null && mList != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) mList.getTotalRecords();
            }

        } catch (Throwable t) {
            log.error("getLogs() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        jobResponse.criteriaObj = request.criteriaObj;
        return jobResponse;
    }

    /**
     * Export all logs.
     *
     * @param response the response
     *
     * @deprecated - Use com.dell.asm.ui.controller.DownloadController - "downloads/getfile" instead
     */
    @Deprecated
    @RequestMapping(value = "exportfilteredlogs", method = RequestMethod.GET)
    public void exportAllLogs(HttpServletResponse response) {
        log.debug("exportAllLogs - Called by UI");
        StringBuilder builder = new StringBuilder();

        Locale locale = LocaleContextHolder.getLocale();
        builder.append("\"").append(
                getApplicationContext().getMessage("LogsController.Severity", null, locale)).append(
                "\",");
        builder.append("\"").append(
                getApplicationContext().getMessage("LogsController.Category", null, locale)).append(
                "\",");
        builder.append("\"").append(
                getApplicationContext().getMessage("LogsController.Description", null,
                                                   locale)).append("\",");
        builder.append("\"").append(
                getApplicationContext().getMessage("LogsController.DateTime", null, locale)).append(
                "\",");
        builder.append("\"").append(
                getApplicationContext().getMessage("LogsController.User", null, locale)).append(
                "\"\n");


        try {
            ResourceList<LocalizedLogMessage> mList = logServiceAdapter.getUserLogMessages(null,
                                                                                           null,
                                                                                           null,
                                                                                           null);

            if (mList != null && mList.getList() != null) {

                for (LocalizedLogMessage message : mList.getList()) {
                    UILog log = parseLog(message);

                    if (log.severity != null) {
                        builder.append("\"").append(log.severity).append("\",");
                    } else {
                        builder.append("\"\",");
                    }
                    if (log.category != null) {
                        builder.append("\"").append(log.category).append("\",");
                    } else {
                        builder.append("\"\",");
                    }
                    if (log.description != null) {
                        builder.append("\"").append(log.description).append("\",");
                    } else {
                        builder.append("\"\",");
                    }
                    if (log.date != null) {
                        builder.append("\"").append(log.date).append("\",");
                    } else {
                        builder.append("\"\",");
                    }
                    if (log.user != null) {
                        builder.append("\"").append(log.user).append("\"\n");
                    } else {
                        builder.append("\"\"\n");
                    }
                }
            }

            response.setStatus(200);
            response.setContentLength(builder.toString().length());
            response.setContentType("text/csv; charset=utf-8");
            response.setHeader("Content-Disposition",
                               "attachment; filename=\"" + getApplicationContext().getMessage(
                                       "LogsController.LogsFileName", null,
                                       LocaleContextHolder.getLocale())
                                       + ".csv\"");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");

            ServletOutputStream out = response.getOutputStream();
            out.println(builder.toString());
            out.flush();
            out.close();

        } catch (Throwable t) {
            log.error("getLogs() - Exception from service call", t);
        }

    }

    /**
     * Purge logs.
     *
     * @param request the request
     * @return the job response
     */
    @RequestMapping(value = "purgelogs", method = RequestMethod.POST)
    public
    @ResponseBody
    JobResponse purgeLogs(@RequestBody JobPurgeLogsRequest request) {

        log.debug("purgeLogs() - JobRequest recieved");
        JobResponse jobResponse = new JobResponse();
        int processed = 0;
        try {
            if (request.requestObj.severitycritical)
                processed += logServiceAdapter.purgeLogs(request.requestObj.olderThan,
                                                         LogMessage.LogSeverity.CRITICAL.name());
            if (request.requestObj.severityinformation)
                processed += logServiceAdapter.purgeLogs(request.requestObj.olderThan,
                                                         LogMessage.LogSeverity.INFO.name());
            if (request.requestObj.severitywarning)
                processed += logServiceAdapter.purgeLogs(request.requestObj.olderThan,
                                                         LogMessage.LogSeverity.WARNING.name());
            if (logServiceAdapter.purgeLogs(request.requestObj.olderThan,
                                            LogMessage.LogSeverity.ERROR.name()) != null)
                processed += logServiceAdapter.purgeLogs(request.requestObj.olderThan,
                                                         LogMessage.LogSeverity.ERROR.name());
        } catch (Throwable t) {
            log.error("purgeLogs() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }

        log.debug("purgeLogs() processed records: " + processed);
        return jobResponse;
    }

    /**
     * Local helper.
     *
     * @param message
     * @return
     */
    private UILog parseLog(LocalizedLogMessage message) {
        UILog log = new UILog();
        log.id = String.valueOf(message.getLogId());

        if (message.getCategory() != null) {
            log.category = getApplicationContext().getMessage(
                    "logCategory." + message.getCategory().name(), null,
                    LocaleContextHolder.getLocale());
        }
        if (message.getLocalizedMessage() != null) {
            log.description = message.getLocalizedMessage();
        }
        if (message.getSeverity() != null) {
            switch (message.getSeverity()) {
            case INFO:
                log.severity = "info";
                break;
            case CRITICAL:
            case ERROR:
                log.severity = "critical";
                break;
            case WARNING:
                log.severity = "warning";
                break;
            }
        }
        if (message.getUserName() != null) {
            log.user = message.getUserName();
        }
        if (message.getTimeStamp() != null) {
            log.date = MappingUtils.getTime(message.getTimeStamp());
        }
        return log;
    }
}
