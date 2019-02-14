package com.dell.asm.ui.model.device;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UISCVMMDevice extends UIBaseObject {

    @JsonProperty
    public String deviceid = "";

    @JsonProperty
    public String name = "";

    @JsonProperty
    public String ipaddress;

    // scvmm, datacenter, cluster, host, resourcepool, vm
    @JsonProperty
    public String type = "";

    @JsonProperty
    public List<UISCVMMDevice> children;

    /**
     * Instantiates a new uI device summary.
     */
    public UISCVMMDevice() {
        super();
        children = new ArrayList<UISCVMMDevice>();
    }

    public UISCVMMDevice(String nodeName, String deviceType) {
        super();
        name = nodeName;
        type = deviceType;
        deviceid = java.util.UUID.randomUUID().toString();
        children = new ArrayList<UISCVMMDevice>();
    }

    public UISCVMMDevice findNode(String name) {
        for (UISCVMMDevice node : children) {
            if (node.name != null && node.name.equals(name)) {
                return node;
            } else {
                UISCVMMDevice ret = node.findNode(name);
                if (ret != null) return ret;
            }
        }
        return null;
    }

    public int hostgroupsNum() {
        int cnt = 0;
        for (UISCVMMDevice node : children) {
            if (node.type.equals("folder")) {
                cnt++;
            }
            cnt += node.hostgroupsNum();
        }

        if (type.equals("scvmm")) cnt--; // compensate VMs folder for root
        return cnt;
    }

    public int hostsNum() {
        int cnt = 0;
        for (UISCVMMDevice node : children) {
            if (node.type.equals("host")) {
                cnt++;
            }
            cnt += node.hostsNum();
        }
        return cnt;
    }

    public int clustersNum() {
        int cnt = 0;
        for (UISCVMMDevice node : children) {
            if (node.type.equals("cluster")) {
                cnt++;
            }
            cnt += node.clustersNum();
        }
        return cnt;
    }

    public int vmsNum() {
        int cnt = 0;
        for (UISCVMMDevice node : children) {
            if (node.type.equals("vm")) {
                cnt++;
            }
            cnt += node.vmsNum();
        }
        return cnt;
    }

}
