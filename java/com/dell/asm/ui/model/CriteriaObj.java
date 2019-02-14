package com.dell.asm.ui.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class CriteriaObj.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CriteriaObj {

    /** The pagination obj. */
    @JsonProperty
    public PaginationObj paginationObj;

    /** The sort obj. */
    @JsonProperty
    public List<SortObj> sortObj;

    /** The filter obj. */
    @JsonProperty
    public List<FilterObj> filterObj;

    /** currentPage */
    @JsonProperty
    public int currentPage;

    /** firstDisplayedRowNumber */
    @JsonProperty
    public int firstDisplayedRowNumber;

    /** itemCount */
    @JsonProperty
    public String itemCount;

    /** lastDisplayedRowNumber */
    @JsonProperty
    public String lastDisplayedRowNumber;

    /** lastPage */
    @JsonProperty
    public int lastPage;

    /** nextPageEnabled */
    @JsonProperty
    public boolean nextPageEnabled;

    /** previousPageEnabled */
    @JsonProperty
    public boolean previousPageEnabled;

    /**
     * Instantiates a new criteria obj.
     */
    public CriteriaObj() {
        super();
    }

    /**
     * Instantiates a new criteria obj.
     *
     * @param paginationObj
     *            the pagination obj
     * @param sortObj
     *            the sort obj
     * @param filterObj
     *            the filter obj
     */
    public CriteriaObj(PaginationObj paginationObj, List<SortObj> sortObj,
                       List<FilterObj> filterObj) {
        super();
        this.paginationObj = paginationObj;
        this.sortObj = sortObj;
        this.filterObj = filterObj;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CriteriaObj [paginationObj=" + paginationObj + ", sortObj=" + sortObj + ", filterObj=" + filterObj + "]";
    }
}
