package com.dell.asm.ui.model.server;

import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.network.UINetworkIdentity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UINIC.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UINIC extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    /** The instance id. */
    @JsonProperty
    public String instanceId;

    /** The vendor. */
    @JsonProperty
    public String vendor;

    /** The product. */
    @JsonProperty
    public String product;

    /** The networktype. */
    @JsonProperty
    public String networktype;

    /** The network information. */
    @JsonProperty
    public UINetworkIdentity networkInformation;

    /**
     * Instantiates a new uINIC.
     */
    public UINIC() {
        super();
        networkInformation = new UINetworkIdentity();
    }

    /**
     * Instantiates a new uINIC.
     *
     * @param id the id
     * @param instanceId the instance id
     * @param vendor the vendor
     * @param product the product
     * @param networktype the networktype
     * @param networkInformation the network information
     */
    public UINIC(String id, String instanceId, String vendor, String product, String networktype,
                 UINetworkIdentity networkInformation) {
        super();
        this.id = id;
        this.instanceId = instanceId;
        this.vendor = vendor;
        this.product = product;
        this.networktype = networktype;
        this.networkInformation = networkInformation;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UINIC [id=" + id + ", instanceId=" + instanceId + ", vendor=" + vendor + ", product=" + product + ", networktype="
                + networktype + ", networkInformation=" + networkInformation + "]";
    }

}
