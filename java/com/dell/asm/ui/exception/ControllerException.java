package com.dell.asm.ui.exception;

import java.io.IOException;

/**
 * ControllerException thrown by Controller to pass UI predefined error condition.
 */
public class ControllerException extends IOException {

    public String details;

    /**
     * Instantiates a new client exception.
     *
     * @param faultMessage the fault message
     */
    public ControllerException(String faultMessage) {
        super(faultMessage);
    }

    /**
     * Instantiates a new client exception.
     *
     * @param faultMessage the fault message
     */
    public ControllerException(String faultMessage, String details) {
        super(faultMessage);

        this.details = details;
    }

}
