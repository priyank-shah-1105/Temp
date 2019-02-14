package com.dell.asm.ui.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FabricNameTypes.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FabricNameTypes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *     &lt;enumeration value="FabricA"/&gt;
 *     &lt;enumeration value="FabricB"/&gt;
 *     &lt;enumeration value="FabricC"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "FabricNameTypes")
@XmlEnum
public enum FabricNameTypes {

    @XmlEnumValue("FabricA")
    FABRIC_A("FabricA"),
    @XmlEnumValue("FabricB")
    FABRIC_B("FabricB"),
    @XmlEnumValue("FabricC")
    FABRIC_C("FabricC");
    private final String value;

    FabricNameTypes(String v) {
        value = v;
    }

    public static FabricNameTypes fromValue(String v) {
        for (FabricNameTypes c : FabricNameTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    public String value() {
        return value;
    }

}
