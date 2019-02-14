/**************************************************************************
 *   Copyright (c) 2018 Dell Inc. All rights reserved.                    *
 *                                                                        *
 * DELL INC. CONFIDENTIAL AND PROPRIETARY INFORMATION. This software may  *
 * only be supplied under the terms of a license agreement or             *
 * nondisclosure agreement with Dell Inc. and may not be copied or        *
 * disclosed except in accordance with the terms of such agreement.       *
 **************************************************************************/
package com.dell.asm.ui.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.dell.asm.asmcore.asmmanager.client.deviceinventory.CompliantState;
import com.dell.asm.ui.model.UIBaseObject;
import com.dell.asm.ui.model.template.UITemplateBuilderComponent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UIServiceSummary extends UIBaseObject {

    @JsonProperty
    public String id;
    @JsonProperty
    public String name;
    @JsonProperty
    public String template;
    /**
     *  case 'red': text = 'Error';
     case 'green': text = 'Healthy';
     case 'blue':
     case 'unknown':
     case '': text = 'In Progress';
     case 'yellow': text = 'Warning';
     case 'orange':
     case 'cancelled': text = 'Cancelled';
     */
    @JsonProperty
    public UIServiceHealth health;
    @JsonProperty
    public String deployedBy;
    @JsonProperty
    public String deployedOn;
    @JsonProperty
    public String description;
    @JsonProperty
    public String errors;
    @JsonProperty
    public String firmwareCompliant = CompliantState.COMPLIANT.getLabel();
    @JsonProperty
    public String firmwarePackageName;
    @JsonProperty
    public boolean brownField;
    @JsonProperty
    public int count_application;
    @JsonProperty
    public int count_cluster;
    @JsonProperty
    public int count_server;
    @JsonProperty
    public int count_storage;
    @JsonProperty
    public int count_switch;
    @JsonProperty
    public int count_vm;
    @JsonProperty
    public int count_scaleio;
    @JsonProperty
    public List<UITemplateBuilderComponent> components;

    public UIServiceSummary() {
        super();
        components = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UIServiceSummary)) return false;
        UIServiceSummary that = (UIServiceSummary) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "UIServiceSummary{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", template='" + template + '\'' +
                ", health=" + health +
                ", deployedBy='" + deployedBy + '\'' +
                ", deployedOn='" + deployedOn + '\'' +
                ", description='" + description + '\'' +
                ", errors='" + errors + '\'' +
                ", firmwareCompliant='" + firmwareCompliant + '\'' +
                ", firmwarePackageName='" + firmwarePackageName + '\'' +
                ", brownField=" + brownField +
                ", count_application=" + count_application +
                ", count_cluster=" + count_cluster +
                ", count_server=" + count_server +
                ", count_storage=" + count_storage +
                ", count_switch=" + count_switch +
                ", count_vm=" + count_vm +
                ", count_scaleio=" + count_scaleio +
                ", components=" + components +
                '}';
    }
}
