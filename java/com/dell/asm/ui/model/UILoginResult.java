package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UILoginResponse.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UILoginResult {

    /** The success. */
    @JsonProperty
    public boolean success;

    /** The url. */
    @JsonProperty
    public String url;

    /** The route. */
    @JsonProperty
    public String route;

    /** The message. */
    @JsonProperty
    public String message;

    /**
     * Instantiates a new uI login result.
     */
    public UILoginResult() {
        super();
    }

    /**
     * Instantiates a new uI login result.
     *
     * @param success
     *            the success
     * @param url
     *            the url
     * @param route
     *            the route
     */
    public UILoginResult(boolean success, String url, String route) {
        super();
        this.success = success;
        this.url = url;
        this.route = route;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UILoginResult [success=" + success + ", url=" + url + ", route=" + route + "]";
    }

}
