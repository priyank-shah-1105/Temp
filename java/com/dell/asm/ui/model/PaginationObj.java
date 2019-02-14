package com.dell.asm.ui.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class PaginationObj.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaginationObj {

    /** The current page. */
    @JsonProperty
    public int currentPage;

    /** The row count per page. */
    @JsonProperty
    public int rowCountPerPage;

    /** The total items count. */
    @JsonProperty
    public int totalItemsCount;

    /** Items current count. */
    @JsonProperty
    public int currentCount;

    /**
     * Instantiates a new pagination obj.
     */
    public PaginationObj() {
        super();
    }

    /**
     * Instantiates a new pagination obj.
     *
     * @param currentPage the current page
     * @param rowCountPerPage the row count per page
     * @param totalItemsCount the total items count
     */
    public PaginationObj(int currentPage, int rowCountPerPage,
                         int totalItemsCount) {
        super();
        this.currentPage = currentPage;
        this.rowCountPerPage = rowCountPerPage;
        this.totalItemsCount = totalItemsCount;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PaginationObj [currentPage=" + currentPage
                + ", rowCountPerPage=" + rowCountPerPage + ", totalItemsCount="
                + totalItemsCount + "]";
    }
}
