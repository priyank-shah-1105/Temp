package com.dell.asm.ui.model.download;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JobDownloadRequest {

    @JsonProperty
    public UIDownload requestObj;

    public JobDownloadRequest(final UIDownload requestObj) {
        super();
        this.requestObj = requestObj;
    }

    public JobDownloadRequest() {
        super();
    }
}
