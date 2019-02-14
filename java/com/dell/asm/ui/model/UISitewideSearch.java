package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UISitewideSearch.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UISitewideSearch {

    /** The Should show sitewide search. */
    @JsonProperty
    public boolean ShouldShowSitewideSearch;

    /**
     * Instantiates a new uI sitewide search.
     */
    public UISitewideSearch() {
        super();
    }

    /**
     * Instantiates a new uI sitewide search.
     *
     * @param shouldShowSitewideSearch the should show sitewide search
     */
    public UISitewideSearch(boolean shouldShowSitewideSearch) {
        super();
        ShouldShowSitewideSearch = shouldShowSitewideSearch;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UISitewideSearch [ShouldShowSitewideSearch=" + ShouldShowSitewideSearch + "]";
    }

}
