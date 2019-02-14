package com.dell.asm.ui.model.device;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class UIVolume extends UIBaseObject {

    @JsonProperty
    public String id;

    @JsonProperty
    public String name;

    @JsonProperty
    public String size;

    @JsonProperty
    public String type;

    /**
     * Instantiates a new uI device summary.
     */
    public UIVolume() {
        super();

    }

    /**
     * Create volume with all attributes
     * @param id
     * @param name
     * @param lSize in Kb
     */
    public UIVolume(String id,
                    String name,
                    double lSize,
                    String type) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;

        double m = lSize / 1024.0;
        double g = lSize / 1048576.0;
        double t = lSize / 1073741824.0;

        // round to 2 decimal places
        if (t > 1) {
            size = ((double) Math.round(t * 100) / 100) + " TB";
        } else if (g > 1) {
            size = ((double) Math.round(g * 100) / 100) + " GB";
        } else if (m > 1) {
            size = ((double) Math.round(m * 100) / 100) + " MB";
        } else if (lSize > 1) {
            size = ((double) Math.round(lSize * 100) / 100) + " KB";
        } else {
            size = "0 KB";
        }
    }


}
