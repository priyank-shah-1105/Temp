package com.dell.asm.ui.adapter.service;

import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.osrepository.OSRepository;

public interface OSRepositoryServiceAdapter {
    public OSRepository create(OSRepository osRepository);

    public OSRepository update(String id, OSRepository osRepository, Boolean sync);

    public OSRepository sync(String id, OSRepository osRepository);

    public Response delete(String id);

    public ResourceList<OSRepository> getAll(String sort, List<String> filter,
                                             Integer offset, Integer limit);

    public OSRepository getById(String id);

    public Response testConnection(OSRepository osRepository);
}
