package com.dell.asm.ui.exception;

import java.io.IOException;

/**
 * Used by MappingUtility to report conversion errors back to controller.
 */
public class MappingRequestException extends IOException {

    /**
     * Instantiates a exception with a message key.
     *
     * @param messageKey the fault message
     */
    public MappingRequestException(String messageKey) {
        super(messageKey);
    }
}
