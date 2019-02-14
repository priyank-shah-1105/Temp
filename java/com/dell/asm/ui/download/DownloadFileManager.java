package com.dell.asm.ui.download;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;


//REFACTOR[fcarta] should take this class and com.dell.asm.ui.upload.UploadFileManager
//and create an abstract parent class (AbstractTmpFileManager) that abstracts/encapsulates commonalities
public class DownloadFileManager {

    private static final Logger LOG = Logger.getLogger(DownloadFileManager.class);
    private static final ConcurrentMap<UUID, DownloadFile> downloadFilesMap = new ConcurrentHashMap<UUID, DownloadFile>();
    private static final String DOWNNLOAD_DIR = "/tmp/Dell/ASM/downloads";
    private static final long CLEANUP_INTERVAL = 15 * 60 * 1000; // try to clean up every 15 min
    private static final long DEFAULT_DOWNLOAD_TTL = 30 * 60 * 1000; // by default let generated downloads live for 30 min
    private static DownloadFileManager INSTANCE = new DownloadFileManager();

    // Do not instantiate
    private DownloadFileManager() {
        super();
        try {
            Files.createDirectories(Paths.get(DOWNNLOAD_DIR));
            final Thread cleanUpThread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(CLEANUP_INTERVAL);
                        } catch (InterruptedException ignore) {
                        }
                        cleanDowloadFiles();
                    }
                }
            });
            cleanUpThread.setDaemon(true);
            cleanUpThread.start();
        } catch (IOException e) {
            LOG.error("Unable to create uploads directory!");
        }
    }

    public static final DownloadFile addDownloadFile(final DownloadType type,
                                                     final String fileName) throws Exception {
        if (type == null || StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException(
                    "Cannot have a null download type or empty export file name!");
        }
        final long downloadTimeInMillis = System.currentTimeMillis();
        final Path downloadDirPath = getDownloadDirPath(type, downloadTimeInMillis);
        if (Files.notExists(downloadDirPath)) {
            Files.createDirectories(downloadDirPath);
        }
        Path downloadFilePath = getDownloadFilePath(type, downloadTimeInMillis, fileName);
        if (Files.notExists(downloadFilePath)) {
            downloadFilePath = Files.createFile(downloadFilePath);
        } else {
            // Overwrite file with latest
            Files.deleteIfExists(downloadFilePath);
            downloadFilePath = Files.createFile(downloadFilePath);
        }
        return addDownloadFileToMap(downloadFilePath, downloadTimeInMillis);
    }

    public static final DownloadFile getDownloadFile(final UUID downloadFileKey) {
        if (downloadFileKey == null) {
            throw new IllegalArgumentException("Upload key passed in is null.");
        }
        return downloadFilesMap.containsKey(downloadFileKey) ? downloadFilesMap.get(
                downloadFileKey) : null;
    }

    public static final void removeDownloadFile(final DownloadFile downloadFile) {
        if (downloadFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(downloadFile.getDownloadPath());
            synchronized (downloadFilesMap) {
                downloadFilesMap.remove(downloadFile.getDownloadFileKey());
            }
        } catch (Exception e) {
            LOG.error(
                    "Unable to delete the file item for the given download file:\n" + downloadFile);
        }
    }

    public static final void removeDownloadFile(final UUID downloadFileKey) {
        removeDownloadFile(getDownloadFile(downloadFileKey));
    }

    private static final DownloadFile addDownloadFileToMap(final Path downloadFilePath,
                                                           final long downloadTimeInMillis) {
        // NOTE[fcarta] - I considered to check to see if the passed in file already exists in the map but dont think
        // we need that check or overhead at the moment.
        synchronized (downloadFilesMap) {
            final DownloadFile download = new DownloadFile(downloadFilePath, downloadTimeInMillis);
            downloadFilesMap.put(download.getDownloadFileKey(), download);
            return download;
        }
    }

    private static UUID generateLookupKey() {
        synchronized (downloadFilesMap) {
            return UUID.randomUUID();
        }
    }

    private static Path getDownloadDirPath(final DownloadType type,
                                           final long downloadTimeInMillis) {
        return Paths.get(DOWNNLOAD_DIR + File.separator + type.getFilePath() + File.separator +
                                 downloadTimeInMillis);
    }

    private static Path getDownloadFilePath(final DownloadType type,
                                            final long downloadTimeInMillis,
                                            final String fileName) {
        return Paths.get(
                getDownloadDirPath(type, downloadTimeInMillis) + File.separator + fileName);
    }

    public DownloadFileManager getInstance() {
        return INSTANCE;
    }

    public void cleanDowloadFiles() {
        cleanDownloadedFilesInMap(DEFAULT_DOWNLOAD_TTL);
        cleanDownloadedFilesOnDisk(DEFAULT_DOWNLOAD_TTL);
    }

    private void cleanDownloadedFilesInMap(final long ttlInMillis) {
        final long now = System.currentTimeMillis();
        synchronized (downloadFilesMap) {
            for (final Map.Entry<UUID, DownloadFile> downloadFileEntry : downloadFilesMap.entrySet()) {
                if ((now - downloadFileEntry.getValue().generatedTimeInMillis) >= ttlInMillis) {
                    // TTL has expired so remove from map
                    removeDownloadFile(downloadFileEntry.getKey());
                    // Be nice and allow other threads to run if needed
                    Thread.yield();
                }
            }
        }
    }

    private void cleanDownloadedFilesOnDisk(final long ttlInMillis) {
        final long now = System.currentTimeMillis();
        for (final DownloadType type : DownloadType.values()) {
            Path downloadTypePath = null;
            try {
                downloadTypePath = Paths.get(DOWNNLOAD_DIR + File.separator + type.getFilePath());
            } catch (InvalidPathException ex) {
                // download type folder does not exist skip to next type in loop
                continue;
            }
            for (final File downloadFile : downloadTypePath.toFile().listFiles()) {
                if (downloadFile.isDirectory()) {
                    try {
                        // because the directory names are the timestamp in millis
                        final long directoryCreationTime = Long.parseLong(downloadFile.getName());
                        if ((now - directoryCreationTime >= ttlInMillis)) {
                            // TTL has expired so remove the file
                            FileUtils.deleteQuietly(downloadFile);
                        }
                    } catch (NumberFormatException e) {
                        LOG.warn(
                                "Found a nonstandard directory name in file download directory: " + DOWNNLOAD_DIR);
                    }
                } else {
                    LOG.warn(
                            "Found a nonstandard file in file download directory: " + DOWNNLOAD_DIR);
                }
                // Be nice and allow other threads to run if needed
                Thread.yield();
            }
        }
    }

    public static class DownloadFile implements Serializable {
        private static final long serialVersionUID = 1L;
        private final UUID downloadFileKey;
        private final Path downloadPath;
        private final long generatedTimeInMillis;
        private DownloadFileStatus status;

        ;
        public DownloadFile(final Path downloadPath, final long downloadTimeInMillis) {
            super();
            this.downloadFileKey = generateLookupKey();
            this.downloadPath = downloadPath;
            this.generatedTimeInMillis = downloadTimeInMillis;
            this.status = DownloadFileStatus.NOT_READY;
        }

        public UUID getDownloadFileKey() {
            return downloadFileKey;
        }

        public Path getDownloadPath() {
            return downloadPath;
        }

        public long getGeneratedTimeInMillis() {
            return generatedTimeInMillis;
        }

        public DownloadFileStatus getStatus() {
            return status;
        }

        public void setStatus(DownloadFileStatus status) {
            this.status = status;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((downloadFileKey == null) ? 0 : downloadFileKey.hashCode());
            result = prime * result + (int) (generatedTimeInMillis ^ (generatedTimeInMillis >>> 32));
            result = prime * result + ((downloadPath == null) ? 0 : downloadPath.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof DownloadFile)) {
                return false;
            }
            DownloadFile other = (DownloadFile) obj;
            if (downloadFileKey == null) {
                if (other.downloadFileKey != null) {
                    return false;
                }
            } else if (!downloadFileKey.equals(other.downloadFileKey)) {
                return false;
            }
            if (generatedTimeInMillis != other.generatedTimeInMillis) {
                return false;
            }
            if (downloadPath == null) {
                if (other.downloadPath != null) {
                    return false;
                }
            } else if (!downloadPath.equals(other.downloadPath)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        public enum DownloadFileStatus {
            NOT_READY,
            READY,
            ERROR
        }
    }
}
