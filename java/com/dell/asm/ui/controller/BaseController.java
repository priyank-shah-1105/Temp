package com.dell.asm.ui.controller;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.dell.asm.i18n2.AsmDetailedMessage;
import com.dell.asm.i18n2.AsmDetailedMessageList;
import com.dell.asm.i18n2.exception.AsmRuntimeException;
import com.dell.asm.rest.common.AsmConstants;
import com.dell.asm.rest.common.util.RestUtil;
import com.dell.asm.ui.converter.DownloadTypeEnumConverter;
import com.dell.asm.ui.converter.StringToUUIDConverter;
import com.dell.asm.ui.download.DownloadType;
import com.dell.asm.ui.exception.ControllerException;
import com.dell.asm.ui.exception.InvalidUploadFileFormat;
import com.dell.asm.ui.model.ErrorObj;
import com.dell.asm.ui.model.FieldError;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.pg.orion.common.utilities.MarshalUtil;

/**
 * Base Service Controller.
 */
public abstract class BaseController {

    /**
     * The Constant log.
     */
    private static final Logger log = Logger.getLogger(BaseController.class);
    /**
     * The context.
     */
    @Autowired
    private ApplicationContext context;

    /**
     * Get x-dell-collection-total-count header from response.
     * @param headers
     * @return
     */
    public static long getTotalCount(MultivaluedMap<String, Object> headers) {
        Object headerValue = headers.getFirst(AsmConstants.DELL_TOTAL_COUNT_HEADER);
        if (headerValue != null) {
            try {
                return Long.parseLong(headerValue.toString());
            } catch (NumberFormatException e) {
                log.error("getTotalCount is not numeric: " + headerValue.toString(), e);
                return 0;
            }
        } else {
            return 0;
        }
    }

    @ExceptionHandler(Exception.class)
    public @ResponseBody
    JobResponse handleException(final Exception ex) {
        return addFailureResponseInfo(ex);
    }

    //private Jaxb2Marshaller marshaller;

    @InitBinder
    public void initBinder(final WebDataBinder dataBinder) {
        dataBinder.registerCustomEditor(DownloadType.class, new DownloadTypeEnumConverter());
        dataBinder.registerCustomEditor(UUID.class, new StringToUUIDConverter());
    }

    /**
     * Returns the application context.
     *
     * @return the application context
     */
    public ApplicationContext getApplicationContext() {
        return this.context;
    }

    /**
     * Sets the application context.
     *
     * @param context
     *            the new application context
     */
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    /**
     * returns the error message.
     *
     * @param messageKey
     *            the message key
     * @param fieldMsgKey
     *            the field msg key
     * @param field
     *            the field
     * @return the portal response
     */
    protected JobResponse addInvalidRequestInfo(String messageKey, String fieldMsgKey,
                                                String field) {
        String methodName = Thread.currentThread().getStackTrace()[2].getMethodName();
        log.debug("Enter: " + this.getClass().getSimpleName() + "#" + methodName);
        JobResponse jobResponse = new JobResponse();
        addErrorMessage(jobResponse, messageKey);
        addErrorFields(jobResponse, fieldMsgKey, field);
        jobResponse.responseCode = -1;

        log.debug("Exit: " + this.getClass().getSimpleName() + "#" + methodName);
        return jobResponse;
    }

    /**
     * Adds the error fields.
     *
     * @param response
     *            the response
     * @param fieldMsgKey
     *            the field msg key
     * @param field
     *            the field
     */
    protected void addErrorFields(JobResponse response, String fieldMsgKey, String field) {
        if ((fieldMsgKey != null) && (field != null)) {
            if (response != null) {
                if (response.errorObj == null) {
                    response.errorObj = new ErrorObj();
                }
            }

            if (response != null) {
                response.errorObj.getErrorFields().add(
                        new FieldError(field, context.getMessage(fieldMsgKey, null,
                                                                 LocaleContextHolder.getLocale())));
            }
        }
    }

    /**
     * Adds the error message.
     *
     * @param response
     *            the response
     * @param messageKey
     *            the message key
     */
    protected JobResponse addErrorMessage(JobResponse response, String messageKey) {
        if (response != null) {
            if (response.errorObj == null) {
                response.errorObj = new ErrorObj();
            }
        }
        if (response != null) {
            response.responseCode = -1;
            response.errorObj.errorMessage = context.getMessage(messageKey, null,
                                                                LocaleContextHolder.getLocale());
        }
        return response;
    }

    /**
     * Adds the error message.
     *
     * @param response
     *            the response
     * @param messageKey
     *            the message key
     */
    protected JobResponse addErrorMessage(JobResponse response, Object[] args, String messageKey) {
        if (response != null) {
            if (response.errorObj == null) {
                response.errorObj = new ErrorObj();
            }
        }
        if (response != null) {
            response.responseCode = -1;
            response.errorObj.errorMessage = context.getMessage(messageKey, args,
                                                                LocaleContextHolder.getLocale());
        }
        return response;
    }

    /**
     * Validate id request.
     *
     * @param request
     *            the PJobIDRequest request
     * @return the portal response
     */

    protected JobResponse validateIDRequest(JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();
        if ((null == request) || (null == request.requestObj)) {
            return addInvalidRequestInfo("error.invalidRequest", "error.NullRequest", "requestObj");
        }

        if (StringUtils.isBlank(request.requestObj.id)) {
            addErrorMessage(jobResponse, "error.invalidRequest");
            addErrorFields(jobResponse, "error.idRequired", "ID");
        }
        return jobResponse;
    }

    /**
     * Adds the failure response info.
     *
     * @param t
     *            the throwable exception
     * @return the portal response
     */
    public JobResponse addFailureResponseInfo(Throwable t) {
        JobResponse jobResponse = new JobResponse();
        ErrorObj errObj = new ErrorObj();
        if (t instanceof WebApplicationException) {
            jobResponse.responseCode = ((WebApplicationException) t).getResponse().getStatus();
            Response r = ((WebApplicationException) t).getResponse();
            parseResponse(((WebApplicationException) t).getResponse(), errObj,
                          t.getLocalizedMessage());
        } else if (t instanceof AsmRuntimeException) {
            AsmRuntimeException asmE = (AsmRuntimeException) t;
            jobResponse.responseCode = -1;
            errObj.errorMessage = asmE.getEEMILocalizableMessage().getDisplayMessage().localize();
            errObj.errorDetails = asmE.getEEMILocalizableMessage().getDetailedDescription().localize();
            errObj.getErrorFields().add(
                    new FieldError(null, errObj.errorMessage, null, null, null));

        } else if (t instanceof ControllerException) {
            jobResponse.responseCode = -1;
            errObj.errorMessage = t.getMessage();
            errObj.errorDetails = ((ControllerException) t).details;
            errObj.getErrorFields().add(
                    new FieldError(null, errObj.errorMessage, errObj.errorDetails, null, null));
        } else if (t instanceof MaxUploadSizeExceededException) {
            log.error("Max upload file limit exceeded!", t);
            jobResponse.responseCode = -1;
            if (context != null) {
                errObj.errorMessage =
                        context.getMessage("fileUpload.maxFileSizeExceeded", null,
                                           LocaleContextHolder.getLocale());
            } else {
                errObj.errorMessage = t.getLocalizedMessage();
            }
            errObj.getErrorFields().add(
                    new FieldError(null, errObj.errorMessage, null, null, null));
        } else if (t instanceof InvalidUploadFileFormat) {
            log.error("Attempt to upload a file with invalid format!", t);
            jobResponse.responseCode = -1;
            if (context != null) {
                errObj.errorMessage =
                        context.getMessage("fileUpload.invalidFileFormat", null,
                                           LocaleContextHolder.getLocale());
            } else {
                errObj.errorMessage = t.getLocalizedMessage();
            }
            errObj.getErrorFields().add(
                    new FieldError(null, errObj.errorMessage, null, null, null));
        } else {
            log.error("Found generic exception in Controller", t);
            jobResponse.responseCode = -1;
            if (context != null) {
                errObj.errorMessage = context.getMessage("SystemError.general", null,
                                                         LocaleContextHolder.getLocale());
            } else {
                errObj.errorMessage = t.getLocalizedMessage();
            }
            errObj.getErrorFields().add(
                    new FieldError(null, errObj.errorMessage, null, null, null));

        }
        jobResponse.errorObj = errObj;
        return jobResponse;
    }

    public JobResponse addFailureResponseInfo(Response r) {
        JobResponse jobResponse = new JobResponse();
        ErrorObj errObj = new ErrorObj();
        parseResponse(r, errObj, null);
        jobResponse.errorObj = errObj;
        return jobResponse;
    }

    protected void parseResponse(Response response, ErrorObj errObj, String message) {
        final AsmDetailedMessageList aList = RestUtil.unwrapExceptionToAsmDetailedMessageList(
                response);
        if (aList != null) {
            final List<AsmDetailedMessage> messages = aList.getMessages();
            buildErrorObj(errObj, messages, context);
        } else {
            // not ASM checked exception
            // TODO: try to get something useful from Entity
            errObj.getErrorFields().add(
                    new FieldError(null, message, message, "500", message));
        }
    }

    protected static void buildErrorObj(ErrorObj errorObj,
                                        List<AsmDetailedMessage> messages,
                                        ApplicationContext context) {
        if (CollectionUtils.isNotEmpty(messages)) {
            // if there is only one error message then add that display message otherwise add generic error message
            errorObj.errorMessage = (messages.size() == 1) ? messages.get(0).getDisplayMessage() :
                    context.getMessage("error.listOfMessages",
                                       null,
                                       LocaleContextHolder.getLocale());
            for (final AsmDetailedMessage msg : messages) {
                errorObj.getErrorFields().add(
                        new FieldError(null,
                                       msg.getDisplayMessage(),
                                       msg.getDetailedMessage(),
                                       msg.getMessageCode(),
                                       msg.getResponseAction()));
            }
        }
    }

    /**
     * This is a good way to see if an excepiton that is returned matches a known error message.  This method uses the 
     * exception to find the ASsmDetailedMessageList and checkes to see if the initial message code in the list matches 
     * the msgCode passed in. 
     *
     * @param exception that is thrown from the adapters
     * @param msgCode the message code that exception should contain
     * @return a boolean indicating if the exception matches/contains the msgCode provided
     */
    public boolean matchesMessageCode(WebApplicationException exception, String msgCode) {
        boolean matches = false;

        if (msgCode != null) {
            String messageCode;
            Object entity = exception.getResponse().getEntity();

            if (entity instanceof AsmDetailedMessageList) {
                AsmDetailedMessageList asmDetailedMessageList = (AsmDetailedMessageList) entity;
                messageCode = asmDetailedMessageList.getMessages().get(0).getMessageCode();
            } else if (entity instanceof InputStream) {
                AsmDetailedMessageList messageList = MarshalUtil.unmarshal(
                        AsmDetailedMessageList.class, (InputStream) entity);
                AsmDetailedMessage asmDetailedMessage = messageList.getMessages().get(0);
                messageCode = asmDetailedMessage.getMessageCode();
            } else {
                messageCode = null;
            }

            if (messageCode != null && msgCode.equals(messageCode)) {
                matches = true;
            }
        }

        return matches;
    }
}
