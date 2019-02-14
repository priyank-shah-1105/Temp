package com.dell.asm.ui.adapter.impl;


import javax.ws.rs.core.MediaType;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dell.asm.alcm.client.model.WizardStatus;
import com.dell.asm.ui.adapter.properties.ServiceProperties;
import com.dell.asm.ui.adapter.service.ASMSetupStatusServiceAdapter;

/**
 * The Class RESTASMSetupStatusServiceAdapter.
 */
@Component("asmSetupStatusServiceAdapter")
public class RESTASMSetupStatusServiceAdapter extends BaseServiceAdapter implements ASMSetupStatusServiceAdapter {

    /**
     * The log.
     */
    private final Logger log = Logger.getLogger(RESTASMSetupStatusServiceAdapter.class);

    /**
     * Instantiates a new rESTASM setup status service adapter.
     */
    @Autowired
    public RESTASMSetupStatusServiceAdapter(ServiceProperties serviceProperties) {
        super(serviceProperties);
        setServicePath("/alcm/wizardStatus");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ASMSetupStatusServiceAdapter#setASMSetupStatus(com.dell.asm.alcm.client.model.ASMSetupStatus)
     */
    @Override
    public WizardStatus setASMSetupStatus(WizardStatus status) {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.put(status, WizardStatus.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.dell.asm.ui.adapter.service.ASMSetupStatusServiceAdapter#getASMSetupStatus()
     */
    @Override
    public WizardStatus getASMSetupStatus() {
        WebClient webClient = createWebClient();
        webClient.accept(MediaType.APPLICATION_XML);
        return webClient.get(WizardStatus.class);
    }


}
