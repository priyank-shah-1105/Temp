package com.dell.asm.ui.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.dell.asm.asmcore.asmmanager.client.addonmodule.AddOnModule;
import com.dell.asm.asmcore.asmmanager.client.deployment.Deployment;
import com.dell.asm.asmcore.asmmanager.client.servicetemplate.ServiceTemplate;
import com.dell.asm.i18n2.exception.AsmRuntimeException;
import com.dell.asm.ui.ASMUIMessages;
import com.dell.asm.ui.adapter.service.AddOnModuleServiceAdapter;
import com.dell.asm.ui.adapter.service.ResourceList;
import com.dell.asm.ui.model.JobIDRequest;
import com.dell.asm.ui.model.JobRequest;
import com.dell.asm.ui.model.JobResponse;
import com.dell.asm.ui.model.RESTRequestOptions;
import com.dell.asm.ui.model.UIListItem;
import com.dell.asm.ui.model.addonmodule.JobAddOnModuleRequest;
import com.dell.asm.ui.model.addonmodule.UIAddOnModule;
import com.dell.asm.ui.upload.UploadFileManager;
import com.dell.asm.ui.upload.UploadFileManager.UploadedFile;
import com.dell.asm.ui.upload.UploadFileManager.UploadedFile.UploadedFileStatus;
import com.dell.asm.ui.util.MappingUtils;

@RestController
@RequestMapping(value = "/addonmodule/")
public class AddOnModuleController extends BaseController {

    private static final Logger LOG = Logger.getLogger(AddOnModuleController.class);

    private AddOnModuleServiceAdapter addOnModuleServiceAdapter;

    @Autowired
    public AddOnModuleController(AddOnModuleServiceAdapter addOnModuleServiceAdapter) {
        this.addOnModuleServiceAdapter = addOnModuleServiceAdapter;
    }

    @RequestMapping(value = "getaddonmodules", method = RequestMethod.POST)
    public JobResponse getAddOnModuless(@RequestBody JobRequest request) {
        JobResponse jobResponse = new JobResponse();
        List<UIAddOnModule> responseObj = new ArrayList<>();
        try {
            final RESTRequestOptions options = new RESTRequestOptions(request.criteriaObj,
                                                                      MappingUtils.COLUMNS_ADDONMODULES,
                                                                      "name");
            final ResourceList<AddOnModule> addOnModulesResource = addOnModuleServiceAdapter.getAddOnModules(
                    options.sortedColumns, options.filterList,
                    options.offset < 0 ? null : options.offset,
                    options.limit < 0 ? MappingUtils.MAX_RECORDS : options.limit);
            final List<AddOnModule> addOnModules = Arrays.asList(addOnModulesResource.getList());
            if (CollectionUtils.isNotEmpty(addOnModules)) {
                for (final AddOnModule addOnModule : addOnModules) {
                    responseObj.add(parseToUIView(addOnModule));
                }
            }
            jobResponse.responseObj = responseObj;
            jobResponse.criteriaObj = request.criteriaObj;
            if (request.criteriaObj != null && request.criteriaObj.paginationObj != null) {
                jobResponse.criteriaObj.paginationObj.totalItemsCount = (int) addOnModulesResource.getTotalRecords();
            }
        } catch (Throwable t) {
            LOG.error("getAddOnModules() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "getaddonmodulebyid", method = RequestMethod.POST)
    public JobResponse getAddOnModuleByID(@RequestBody JobIDRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            jobResponse.responseObj = parseToUIView(
                    addOnModuleServiceAdapter.getAddOnModule(request.requestObj.id));
        } catch (Throwable t) {
            LOG.error("getAddOnModuleById() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "uploadaddonmodule", method = RequestMethod.POST)
    public JobResponse uploadAddOnModule(@RequestParam("file") MultipartFile multipartFile) {

        JobResponse jobResponse = new JobResponse();
        jobResponse.responseObj = new UIAddOnModule();

        try {
            if (!multipartFile.isEmpty()) {
                try {
                    // The following will fail for MockMultipartFile - Most likely only affects tests :(
                    if (!(multipartFile instanceof CommonsMultipartFile)) {
                        throw new IllegalArgumentException(
                                "Only CommonsMultipartFile file uploads are supported at this time!");
                    }
                    // Store FileItem in the UploadManager so we can keep it in memory/disk until the next save request
                    final FileItem uploadFile = ((CommonsMultipartFile) multipartFile).getFileItem();
                    final UploadedFile uploadedFile = UploadFileManager.addUploadedFile(uploadFile);
                    ((UIAddOnModule) jobResponse.responseObj).id = uploadedFile.getUploadFileKey().toString();
                } catch (Exception e) {
                    ((UIAddOnModule) jobResponse.responseObj).filename = "ERROR";
                    LOG.error("Error uploading file to tmp location", e);
                    throw new AsmRuntimeException(
                            ASMUIMessages.addOnModuleErrorUploading(multipartFile.getName()), e);
                }
            } else {
                ((UIAddOnModule) jobResponse.responseObj).filename = "ERROR empty file";
                throw new AsmRuntimeException("Add on module upload file is empty!",
                                              ASMUIMessages.addOnModuleErrorUploadingEmptyFile(
                                                      multipartFile.getName()));
            }
        } catch (Throwable t) {
            jobResponse = addFailureResponseInfo(t);
        }

        return jobResponse;
    }

    @RequestMapping(value = "saveaddonmodule", method = RequestMethod.POST)
    public JobResponse saveAddOnModule(@RequestBody JobAddOnModuleRequest request) {
        JobResponse jobResponse = new JobResponse();
        try {
            if (request.requestObj != null) {
                final UIAddOnModule uiAddOnModule = request.requestObj;
                final UUID uploadedFileKey = UUID.fromString(uiAddOnModule.id);
                final UploadedFile uploadedFile = UploadFileManager.getUploadedFile(
                        uploadedFileKey);
                if (uploadedFile == null) {
                    throw new AsmRuntimeException(ASMUIMessages.addOnModuleCannotFindUpload(),
                                                  new IllegalArgumentException(
                                                          "File upload timed out or cannot be found!"));
                }
                switch (uploadedFile.getStatus()) {
                case WAITING_FOR_PROCESSING:
                    UploadFileManager.updateUploadedFileStatus(uploadedFileKey,
                                                               UploadedFileStatus.PENDING);
                    final AddOnModule addOnModule =
                            addOnModuleServiceAdapter.createAddOnModule(
                                    uploadedFile.getUploadPath().toUri().toURL());
                    LOG.info(String.format("Add on module %s parsed and added successfully!",
                                           addOnModule.getName()));
                    UploadFileManager.updateUploadedFileStatus(uploadedFileKey,
                                                               UploadedFileStatus.COMPLETE);
                    jobResponse.responseObj = UploadedFileStatus.COMPLETE.toString();
                    break;
                case PENDING:
                    jobResponse.responseObj = UploadedFileStatus.PENDING.toString();
                    break;
                case COMPLETE:
                    jobResponse.responseObj = UploadedFileStatus.COMPLETE.toString();
                    break;
                case ERROR:
                    LOG.error("Error parsing add on module!");
                    jobResponse.responseObj = UploadedFileStatus.ERROR.toString();
                    break;
                default:
                    throw new UnsupportedOperationException(
                            "Upload status not set or not implemented!");
                }
            }
        } catch (Throwable t) {
            LOG.error("saveAddOnModule() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    @RequestMapping(value = "removeaddonmodule", method = RequestMethod.POST)
    public JobResponse deleteAddOnModule(@RequestBody JobIDRequest request) {
        JobResponse jobResponse;
        try {
            jobResponse = validateIDRequest(request);
            if (jobResponse.errorObj != null) {
                return jobResponse;
            }
            addOnModuleServiceAdapter.deleteAddOnModule(request.requestObj.id);
        } catch (Throwable t) {
            LOG.error("deleteAddOnModule() - Exception from service call", t);
            jobResponse = addFailureResponseInfo(t);
        }
        return jobResponse;
    }

    private UIAddOnModule parseToUIView(final AddOnModule addOnModule) {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        final UIAddOnModule uiAddOnModule = new UIAddOnModule();
        uiAddOnModule.id = addOnModule.getId();
        uiAddOnModule.name = addOnModule.getName();
        uiAddOnModule.description = addOnModule.getDescription();
        uiAddOnModule.filepath = addOnModule.getModulePath();
        uiAddOnModule.version = addOnModule.getVersion();
        uiAddOnModule.isInUse = addOnModule.isActive();
        uiAddOnModule.uploadedBy = addOnModule.getUploadedBy();
        uiAddOnModule.uploadedDate = dateFormat.format(addOnModule.getUploadedDate());
        for (Deployment deployment : addOnModule.getDeployments()) {
            uiAddOnModule.services.add(
                    new UIListItem(deployment.getId(), deployment.getDeploymentName()));
        }
        for (ServiceTemplate template : addOnModule.getTemplates()) {
            uiAddOnModule.templates.add(
                    new UIListItem(template.getId(), template.getTemplateName()));
        }
        return uiAddOnModule;
    }

}
