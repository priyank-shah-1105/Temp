package com.dell.asm.ui.model;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for FabricPurposeTypes.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="FabricPurposeTypes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN"&gt;
 *     &lt;enumeration value="ALL_LAN"/&gt;
 *     &lt;enumeration value="ALL_SAN"/&gt;
 *     &lt;enumeration value="PUBLIC_LAN"/&gt;
 *     &lt;enumeration value="PRIVATE_LAN"/&gt;
 *     &lt;enumeration value="SAN_iSCSI"/&gt;
 *     &lt;enumeration value="SAN_FCoE"/&gt;
 *     &lt;enumeration value="Converged"/&gt;
 *     &lt;enumeration value="NONE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "FabricPurposeTypes")
@XmlEnum
public enum FabricPurposeTypes {

    ALL_LAN("ALL_LAN"),
    ALL_SAN("ALL_SAN"),
    PUBLIC_LAN("PUBLIC_LAN"),
    PRIVATE_LAN("PRIVATE_LAN"),
    @XmlEnumValue("SAN_iSCSI")
    SAN_I_SCSI("SAN_iSCSI"),
    @XmlEnumValue("SAN_FCoE")
    SAN_F_CO_E("SAN_FCoE"),
    @XmlEnumValue("Converged")
    CONVERGED("Converged"),
    NONE("NONE");
    private final String value;

    FabricPurposeTypes(String v) {
        value = v;
    }

    public static FabricPurposeTypes fromValue(String v) {
        for (FabricPurposeTypes c : FabricPurposeTypes.values()) {
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
