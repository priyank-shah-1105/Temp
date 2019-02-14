package com.dell.asm.ui.model.service;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class ServiceStatus.
 [
 {
 msg: "Starting deployment alex-depl1",
 datetime: "2014-01-14T21:39:56.020000 #2862"
 },
 {
 msg: "Processing components of type STORAGE",
 datetime: "2014-01-14T21:39:56.022000 #2862"
 },
 {
 msg: "Processing storage component: equallogic-172.17.4.10",
 datetime: "2014-01-14T21:39:56.029000 #2862"
 },
 {
 msg: "Starting processing resources for endpoint equallogic-172.17.4.10",
 datetime: "2014-01-14T21:39:56.030000 #2862"
 }
 ]
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceStatus extends UIBaseObject {

    @JsonProperty
    public String msg;

    @JsonProperty
    public String datetime;

    public ServiceStatus() {
        super();
    }
}
