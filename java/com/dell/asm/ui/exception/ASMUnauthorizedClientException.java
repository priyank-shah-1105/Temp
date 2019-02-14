package com.dell.asm.ui.exception;

import java.io.IOException;

/**
 * ASMUnauthorizedClientException thrown by ASMResponseErrorHandler when raw response status code
 * is 401.
 */
public class ASMUnauthorizedClientException extends IOException {

    /**
     * Instantiates a new 401 client exception.
     *
     * @param faultMessage the fault message
     */
    public ASMUnauthorizedClientException(String faultMessage) {
        super(faultMessage);
    }
}
