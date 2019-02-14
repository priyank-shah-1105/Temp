package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class FieldError.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FieldError {

    /** The field. */
    @JsonProperty
    public String field;

    /** The error message. */
    @JsonProperty
    public String errorMessage;

    /** The error details. */
    @JsonProperty
    public String errorDetails;

    /** The error action. */
    @JsonProperty
    public String errorAction;

    /** The error code. */
    @JsonProperty
    public String errorCode;

    /**
     * Instantiates a new field error.
     */
    public FieldError() {
        super();
    }

    /**
     * Instantiates a new field error.
     *
     * @param field
     *            the field
     * @param errorMessage
     *            the error message
     * @param errorDetails
     *            the error details
     */
    public FieldError(String field, String errorMessage, String errorDetails, String code,
                      String action) {
        super();
        this.field = field;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.errorAction = action;
        this.errorCode = code;
    }

    /**
     * Instantiates a new field error.
     *
     * @param field
     *            the field
     * @param errorMessage
     *            the error message
     */
    public FieldError(String field, String errorMessage) {
        super();
        this.field = field;
        this.errorMessage = errorMessage;
    }

}
