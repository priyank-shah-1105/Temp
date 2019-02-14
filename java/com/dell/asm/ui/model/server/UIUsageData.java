package com.dell.asm.ui.model.server;

import java.util.ArrayList;
import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIUsageData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUsageData extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;
    @JsonProperty
    public String category;
    @JsonProperty
    public Double currentvalue;
    @JsonProperty
    public String currentvaluelabel;
    @JsonProperty
    public Double peakvalue;
    @JsonProperty
    public String peaktime;
    @JsonProperty
    public String starttime;
    @JsonProperty
    public double threshold;
    @JsonProperty
    public List<UIUsageDataSeries> historicaldata;

    /**
     * Instantiates a new struc.
     */
    public UIUsageData() {
        super();
        historicaldata = new ArrayList();
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

}
