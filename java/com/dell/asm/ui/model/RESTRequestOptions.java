package com.dell.asm.ui.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Container for pagination and sort parameters the way REST API expects those.
 * User: Alex_Dron
 * Date: 10/3/13
 * Time: 10:40 AM
 */
public class RESTRequestOptions {
    public int offset = 0;
    public int limit = 5000;
    public String sortedColumns = null;
    public List<String> filterList = null;

    /**
     * Convert request object into REST API consumable list of parameters. Uses one map for sort and filter.
     *
     * @param criteriaObj
     * @param colMap
     * @param secondSortColumn
     */
    public RESTRequestOptions(CriteriaObj criteriaObj, Map<String, String> colMap,
                              String secondSortColumn) {
        init(criteriaObj, colMap, colMap, secondSortColumn);
    }

    /**
     * Convert request object into REST API consumable list of parameters. Uses sort and filter maps.
     *
     * @param criteriaObj
     */
    public RESTRequestOptions(CriteriaObj criteriaObj, Map<String, String> sortMap,
                              Map<String, String> filterMap, String secondSortColumn) {
        init(criteriaObj, sortMap, filterMap, secondSortColumn);
    }

    /**
     * Create JobRequest with the same options but for the previous page.
     * @param oldRequest
     * @return
     */
    public static JobRequest switchToPrevPage(JobRequest oldRequest, int totalRecords) {
        JobRequest newRequest = oldRequest;
        newRequest.criteriaObj.paginationObj.currentPage--;
        newRequest.criteriaObj.paginationObj.totalItemsCount = totalRecords;
        // sanity check
        if (newRequest.criteriaObj.paginationObj.currentPage < 0)
            newRequest.criteriaObj.paginationObj.currentPage = 0;

        if (newRequest.criteriaObj.paginationObj.currentPage * newRequest.criteriaObj.paginationObj.rowCountPerPage > totalRecords) {
            newRequest.criteriaObj.paginationObj.currentPage = 0;
        }

        return newRequest;
    }

    /**
     * Create JobRequest with the same options but for the previous page.
     * @param oldRequest
     * @return
     */
    public static JobIDRequest switchToPrevPage(JobIDRequest oldRequest, int totalRecords) {
        JobIDRequest newRequest = oldRequest;
        newRequest.criteriaObj.paginationObj.currentPage--;
        newRequest.criteriaObj.paginationObj.totalItemsCount = totalRecords;
        // sanity check
        if (newRequest.criteriaObj.paginationObj.currentPage < 0)
            newRequest.criteriaObj.paginationObj.currentPage = 0;

        if (newRequest.criteriaObj.paginationObj.currentPage * newRequest.criteriaObj.paginationObj.rowCountPerPage > totalRecords) {
            newRequest.criteriaObj.paginationObj.currentPage = 0;
        }

        return newRequest;
    }

    private void init(CriteriaObj criteriaObj, Map<String, String> sortMap,
                      Map<String, String> filterMap, String secondSortColumn) {
        sortedColumns = getSortedColumns(criteriaObj, sortMap, secondSortColumn);

        if (criteriaObj != null && criteriaObj.paginationObj != null) {
            offset = criteriaObj.paginationObj.currentPage * criteriaObj.paginationObj.rowCountPerPage;
            limit = criteriaObj.paginationObj.rowCountPerPage;
        }

        // eq,columnName,columnValue,columnValue2[,columnValue3...]
        // eq,columnName,
        // eq,columnName,,columnValue[,columnValue2...]

        if (null != criteriaObj && null != criteriaObj.filterObj) {
            filterList = new ArrayList<String>();
            String filter = "";

            for (FilterObj fo : criteriaObj.filterObj) {

                // Here we are remapping input operator "=" -> "eq"
                if ("=".equals(fo.op))
                    fo.op = "eq";

                filter = fo.op;
                filter += ",";

                String mField = fo.field;
                // Here it appears we can do the same remapping on the input fields
                if (filterMap != null && filterMap.containsKey(fo.field)) {
                    mField = filterMap.get(fo.field);
                }

                filter += mField;
                if (fo.opTarget != null && fo.opTarget.size() > 0) {
                    for (String val : fo.opTarget) {
                        filter += ",";
                        if (val == null)
                            val = "";
                        filter += val;
                    }
                } else {
                    filter += ",";
                }
                filterList.add(filter);
            }
        }
    }

    private String getSortedColumns(CriteriaObj criteriaObj, Map<String, String> colMap,
                                    String secondSortColumn) {
        if (criteriaObj == null || criteriaObj.sortObj == null) return null;

        List<SortObj> list = new ArrayList<SortObj>();
        SortObj so = null;
        String sortColumn = null;
        for (SortObj sobj : criteriaObj.sortObj) {
            so = new SortObj();
            so.order = sobj.order;
            if (colMap != null && colMap.containsKey(sobj.field))
                // Remap the name if it's in the map
                so.field = colMap.get(sobj.field);
            else
                // Otherwise accept the name verbatim as given
                so.field = sobj.field;
            list.add(so);

            sortColumn = sobj.field;
        }

        if (secondSortColumn != null && !secondSortColumn.equals(sortColumn)) {
            so = new SortObj();
            if (colMap != null && colMap.containsKey(secondSortColumn))
                so.field = colMap.get(secondSortColumn);
            else
                so.field = secondSortColumn;
            so.order = 1;
            list.add(so);
        }

        return getSortString(list);
    }

    /**
     * Returns coma separated list of sort columns.
     * Currently (10/3/2013) we support only one primary column and one secondary
     *
     * @return
     */
    private String getSortString(List<SortObj> sortObj) {
        if (sortObj == null || sortObj.size() == 0) return null;

        String res = "";

        for (SortObj so : sortObj) {
            if (res.length() > 0)
                res += ",";

            if (so.order == 1)
                res += so.field;
            else if (so.order == 2)
                res += "-" + so.field;
            else
                continue;
        }

        return res;
    }
}
