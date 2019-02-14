package com.dell.asm.ui.download;

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;

public enum DownloadType {

    LOGS("logs", "logs", "userLogs", "csv"),
    SERVICES("services", "services", "asmServices", "csv"),
    TEMPLATES("templates", "templates", "serviceTemplates", "csv"),
    NETWORKS("networks", "networks", "networks", "csv"),
    NETWORK_DETAILS("networkdetails", "networkdetails", "networkDetails", "zip"),
    DEVICES("devices", "devices", "devices", ".csv");

    private final String urlPath;
    private final String filePath;
    private final String fileName;
    private final String fileFormatExt;

    private DownloadType(final String urlPath, final String filePath, final String fileName,
                         final String fileFormatExt) {
        this.urlPath = urlPath;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileFormatExt = fileFormatExt;
    }

    public static DownloadType findFromUrlIgnoreCase(final String url) {
        for (final DownloadType downloadType : DownloadType.values()) {
            if (StringUtils.equalsIgnoreCase(downloadType.urlPath, url)) {
                return downloadType;
            }
        }

        throw new IllegalArgumentException(
                String.format("Download type for the given url %s is not implemented!", url));
    }

    public String getUrlPath() {
        return urlPath;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileFormatExt() {
        return fileFormatExt;
    }

    public String getFileNameWithCurrentTimeStamp() {
        return String.format("%s-%d.%s", fileName, Calendar.getInstance().getTimeInMillis(),
                             fileFormatExt);
    }
}
