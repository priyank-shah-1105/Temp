package com.dell.asm.ui.adapter.service;

import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.ui.model.UIJobs;
import com.dell.pg.jraf.client.jobmgr.JrafJobInfo;

/**
 * Interface for jobs
 */
public interface JobsServiceAdapter {

    /**
     * Get Jobs.
     *
     * @return Jobs.
     */
    ResourceList<JrafJobInfo> getJobs(String sort, List<String> filter, Integer offset,
                                      Integer limit);

    UIJobs getById(String id);

    Response deleteJobs(String id);

}
