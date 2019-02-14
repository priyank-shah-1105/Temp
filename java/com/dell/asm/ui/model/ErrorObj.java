package com.dell.asm.ui.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class ErrorObj.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorObj {

    /** The error message. */
    @JsonProperty
    public String errorMessage;

    /** The error details. */
    @JsonProperty
    public String errorDetails;

    /** The collection of field errors. */
    @JsonProperty
    public List<FieldError> fldErrors;

    /**
     * Instantiates a new error object.
     */
    public ErrorObj() {
        super();
    }

    public ErrorObj(String errorMessage, String errorDetails, List<FieldError> fldErrors) {
        super();
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.fldErrors = fldErrors;
    }

    /**
     * To json.
     *
     * @return the string
     */
    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        try {
            json = mapper.writeValueAsString(this);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Gets the error fields.
     *
     * @return the error fields
     */
    public List<FieldError> getErrorFields() {
        if (fldErrors == null) {
            fldErrors = new ArrayList<FieldError>();
        }
        return fldErrors;
    }

}
