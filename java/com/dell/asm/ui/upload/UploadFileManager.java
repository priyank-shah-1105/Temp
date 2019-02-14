package com.dell.asm.ui.upload;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.dell.asm.ui.upload.UploadFileManager.UploadedFile.UploadedFileStatus;

// REFACTOR[fcarta] should take this class and com.dell.asm.ui.download.DownloadFileManager.DownloadFileManager()
// and create an abstract parent class (AbstractTmpFileManager) that abstracts/encapsulates commonalities
public class UploadFileManager {

    private static final Logger LOG = Logger.getLogger(UploadFileManager.class);
    private static final ConcurrentMap<UUID, UploadedFile> uploadedFilesMap = new ConcurrentHashMap<UUID, UploadedFile>();
    private static final String PARENT_UPLOAD_DIR = "/tmp/Dell/ASM/uploads";
    private static final long CLEANUP_INTERVAL = 5 * 60 * 1000; // try to clean up every 5 min
    private static final long DEFAULT_UPLOAD_TTL = 10 * 60 * 1000; // by default let uploads live for 10 min
    private static final UploadFileManager instance = new UploadFileManager();

    // Do not instantiate
    private UploadFileManager() {
        super();
        try {
            Files.createDirectories(Paths.get(PARENT_UPLOAD_DIR));
            final Thread cleanUpThread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(CLEANUP_INTERVAL);
                        } catch (InterruptedException ignore) {
                        }
                        cleanUploadFiles();
                    }
                }
            });
            cleanUpThread.setDaemon(true);
            cleanUpThread.start();
        } catch (IOException e) {
            LOG.error("Unable to create uploads directory!");
        }
    }

    /**
     * Add the given file item to mapped and tracked across requests
     * @param fileItem
     * @return
     * @throws Exception
     */
    public static final UploadedFile addUploadedFile(final FileItem fileItem) throws Exception {
        final long uploadTimeInMillis = System.currentTimeMillis();
        final Path uploadedDirPath = getUploadedDirPath(uploadTimeInMillis);
        if (Files.notExists(uploadedDirPath)) {
            Files.createDirectories(uploadedDirPath);
        }
        Path uploadedFilePath = getUploadedFilePath(fileItem, uploadTimeInMillis);
        if (Files.notExists(uploadedFilePath)) {
            uploadedFilePath = Files.createFile(uploadedFilePath);
        } else {
            // Overwrite file with latest
            Files.deleteIfExists(uploadedFilePath);
            uploadedFilePath = Files.createFile(uploadedFilePath);
        }
        fileItem.write(uploadedFilePath.toFile());
        return addUploadedFileToMap(uploadedFilePath, uploadTimeInMillis);
    }

    public static final UploadedFile updateUploadedFileStatus(final UUID uploadedFileKey,
                                                              final UploadedFileStatus status) {
        final UploadedFile uploadedFile = UploadFileManager.getUploadedFile(uploadedFileKey);
        if (uploadedFile == null) {
            throw new IllegalArgumentException("Upload key passed is not found!");
        }
        // synchronization may not be needed here but just incase
        synchronized (uploadedFilesMap) {
            uploadedFile.setStatus(status);
        }
        return uploadedFile;
    }

    /**
     * Return uploaded file for the given uploaded file key. Returns null on key not found and IllegalArgumentException
     * on null key parameter.
     * @param uploadedFileKey
     * @return Uploaded file if found otherwise null
     */
    public static final UploadedFile getUploadedFile(final UUID uploadedFileKey) {
        if (uploadedFileKey == null) {
            throw new IllegalArgumentException("Upload key passed in is null.");
        }
        return uploadedFilesMap.containsKey(uploadedFileKey) ? uploadedFilesMap.get(
                uploadedFileKey) : null;
    }

    /**
     * Removes the given uploaded file
     * on null key parameter.
     * @param uploadedFile
     */
    public static final void removeUploadedFile(final UploadedFile uploadedFile) {
        if (uploadedFile == null) {
            return;
        }
        try {
            Files.deleteIfExists(uploadedFile.getUploadPath());
            synchronized (uploadedFilesMap) {
                uploadedFilesMap.remove(uploadedFile.getUploadFileKey());
            }
        } catch (Exception e) {
            LOG.error("Unable to delete the file item for the given upload file:\n" + uploadedFile);
        }
    }

    /**
     * Removes the uploaded file for the given upload key.
     * on null key parameter.
     * @param uploadedFileKey
     */
    public static final void removeUploadedFile(final UUID uploadedFileKey) {
        removeUploadedFile(getUploadedFile(uploadedFileKey));
    }

    private static final UploadedFile addUploadedFileToMap(final Path uploadFilePath,
                                                           final long uploadedTimeInMillis) {
        // NOTE[fcarta] - I considered to check to see if the passed in file already exists in the map but dont think
        // we need that check or overhead at the moment.
        synchronized (uploadedFilesMap) {
            final UploadedFile upload = new UploadedFile(uploadFilePath, uploadedTimeInMillis);
            uploadedFilesMap.put(upload.getUploadFileKey(), upload);
            return upload;
        }
    }

    private static UUID generateLookupKey() {
        synchronized (uploadedFilesMap) {
            return UUID.randomUUID();
        }
    }

    private static Path getUploadedDirPath(final long uploadTimeInMillis) {
        return Paths.get(PARENT_UPLOAD_DIR + File.separator + uploadTimeInMillis);
    }

    private static Path getUploadedFilePath(final FileItem fileItem,
                                            final long uploadTimeInMillis) {
        return Paths.get(
                getUploadedDirPath(uploadTimeInMillis) + File.separator + fileItem.getName());
    }

    public UploadFileManager getInstance() {
        return instance;
    }

    public void cleanUploadFiles() {
        cleanUploadedFilesInMap(DEFAULT_UPLOAD_TTL);
        cleanUploadedFilesOnDisk(DEFAULT_UPLOAD_TTL);
    }

    private void cleanUploadedFilesInMap(final long ttlInMillis) {
        final long now = System.currentTimeMillis();
        synchronized (uploadedFilesMap) {
            for (final Map.Entry<UUID, UploadedFile> uploadedFileEntry : uploadedFilesMap.entrySet()) {
                if ((now - uploadedFileEntry.getValue().uploadTimeInMillis) >= ttlInMillis) {
                    // TTL has expired so remove from map
                    removeUploadedFile(uploadedFileEntry.getKey());
                    // Be nice and allow other threads to run if needed
                    Thread.yield();
                }
            }
        }
    }

    private void cleanUploadedFilesOnDisk(final long ttlInMillis) {
        final long now = System.currentTimeMillis();
        for (final File uploadFile : Paths.get(PARENT_UPLOAD_DIR).toFile().listFiles()) {
            if (uploadFile.isDirectory()) {
                try {
                    // because the directory names are the timestamp in millis
                    final long directoryCreationTime = Long.parseLong(uploadFile.getName());
                    if ((now - directoryCreationTime >= ttlInMillis)) {
                        // TTL has expired so remove the file
                        FileUtils.deleteQuietly(uploadFile);
                    }
                } catch (NumberFormatException e) {
                    LOG.warn(
                            "Found a nonstandard directory name in file upload directory: " + PARENT_UPLOAD_DIR);
                }
            } else {
                LOG.warn("Found a nonstandard file in file upload directory: " + PARENT_UPLOAD_DIR);
            }
            // Be nice and allow other threads to run if needed
            Thread.yield();
        }
    }

    public static class UploadedFile implements Serializable {
        private static final long serialVersionUID = 1L;
        private final UUID uploadFileKey;
        private final Path uploadPath;
        private final long uploadTimeInMillis;
        private UploadedFileStatus status;

        ;
        public UploadedFile(final Path uploadPath, final long uploadTimeInMillis) {
            super();
            this.uploadFileKey = generateLookupKey();
            this.uploadPath = uploadPath;
            this.uploadTimeInMillis = uploadTimeInMillis;
            this.status = UploadedFileStatus.WAITING_FOR_PROCESSING;
        }

        public UUID getUploadFileKey() {
            return uploadFileKey;
        }

        public Path getUploadPath() {
            return uploadPath;
        }

        public long getUploadTimeInMillis() {
            return uploadTimeInMillis;
        }

        public UploadedFileStatus getStatus() {
            return status;
        }

        public void setStatus(UploadedFileStatus status) {
            this.status = status;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((uploadFileKey == null) ? 0 : uploadFileKey.hashCode());
            result = prime * result + (int) (uploadTimeInMillis ^ (uploadTimeInMillis >>> 32));
            result = prime * result + ((uploadPath == null) ? 0 : uploadPath.hashCode());
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
            if (!(obj instanceof UploadedFile)) {
                return false;
            }
            UploadedFile other = (UploadedFile) obj;
            if (uploadFileKey == null) {
                if (other.uploadFileKey != null) {
                    return false;
                }
            } else if (!uploadFileKey.equals(other.uploadFileKey)) {
                return false;
            }
            if (uploadTimeInMillis != other.uploadTimeInMillis) {
                return false;
            }
            if (uploadPath == null) {
                if (other.uploadPath != null) {
                    return false;
                }
            } else if (!uploadPath.equals(other.uploadPath)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

        public enum UploadedFileStatus {
            WAITING_FOR_PROCESSING,
            PENDING,
            COMPLETE,
            ERROR
        }
    }
}
