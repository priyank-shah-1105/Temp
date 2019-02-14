package com.dell.asm.ui.adapter.service;

import java.net.URL;
import java.util.List;

import javax.ws.rs.core.Response;

import com.dell.asm.asmcore.asmmanager.client.addonmodule.AddOnModule;

public interface AddOnModuleServiceAdapter {

    public AddOnModule createAddOnModule(final URL addOnModuleUrl);

    public AddOnModule getAddOnModule(final String addOnModuleId);

    public ResourceList<AddOnModule> getAddOnModules(final String sort, final List<String> filter,
                                                     final Integer offset,
                                                     final Integer limit);

    public Response deleteAddOnModule(final String addOnModuleId);
}
