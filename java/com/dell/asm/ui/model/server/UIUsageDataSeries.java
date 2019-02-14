package com.dell.asm.ui.model.server;

import java.util.List;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIUsageData.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIUsageDataSeries extends UIBaseObject {

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String timeframe;
    @JsonProperty
    public String timeframelabel;
    @JsonProperty
    public String chartlabel;
    @JsonProperty
    public List<UIUsageDataPoint> data;

    @JsonProperty
    public double averagevalue;
    @JsonProperty
    public double minimumvalue;
    @JsonProperty
    public double maximumvalue;

    /**
     * Instantiates a new struc.
     */
    public UIUsageDataSeries() {
        super();
    }

}
