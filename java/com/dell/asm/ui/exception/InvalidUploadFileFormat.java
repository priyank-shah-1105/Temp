package com.dell.asm.ui.exception;

public class InvalidUploadFileFormat extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidUploadFileFormat(String faultMessage) {
        super(faultMessage);
    }
}
