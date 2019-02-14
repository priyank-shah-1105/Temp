package com.dell.asm.ui.model.credential;

import java.io.IOException;

import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Credentials.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UICredential extends UIBaseObject implements Cloneable {

    public static final String TYPE_SERVER = "server";
    public static final String TYPE_CHASSIS = "chassis";
    public static final String TYPE_IOM = "iom";
    public static final String TYPE_STORAGE = "storage";
    public static final String TYPE_VCENTER = "vcenter";
    public static final String TYPE_SCVMM = "scvmm";
    public static final String TYPE_EM = "em";
    public static final String TYPE_SCALEIO = "scaleio";
    public static final String TYPE_OS = "os";

    public static final String PROTOCOL_TELNET = "TELNET";
    public static final String PROTOCOL_SSH = "SSH";
    /**
     * on password fields: a value of '$CURRENT$' will be used to indicate that the password has not been changed in submission
     */
    public static final String CURRENT_PASSWORD = "$CURRENT$";
    /** The id. */
    @JsonProperty
    public String id;
    /** The typeId. */
    @JsonProperty
    public String typeId;
    /** The credentialsName. */
    @JsonProperty
    public String credentialsName;
    /** The username. */
    @JsonProperty
    public String username;
    /** The password. */
    @JsonProperty
    public String password;
    /** The OS username. */
    @JsonProperty
    public String gatewayosusername;
    /** The OS password. */
    @JsonProperty
    public String gatewayospassword;
    /** The enableCertificateCheck. */
    @JsonProperty
    public Boolean enableCertificateCheck;
    /** The isTelnet. */
    @JsonProperty
    public String credentialProtocol;
    /** The communityString. */
    @JsonProperty
    public String communityString;
    /** The snmpVersionId. */
    @JsonProperty
    public String snmpVersionId;
    /** The snmpUsername. */
    @JsonProperty
    public String snmpUsername;
    /** The authorizationProtocolId. */
    @JsonProperty
    public String authorizationProtocolId;
    /** The authorizationProtocolPassword. */
    @JsonProperty
    public String authorizationProtocolPassword;
    /** The privacyProtocolId. */
    @JsonProperty
    public String privacyProtocolId;
    /** The privacyProtocolPassword. */
    @JsonProperty
    public String privacyProtocolPassword;
    /** The numberOfDevices. */
    @JsonProperty
    public int numberOfDevices;
    /** The createdBy. */
    @JsonProperty
    public String createdBy;
    /** The creationTime. */
    @JsonProperty
    public String creationTime;
    /** The updateTime. */
    @JsonProperty
    public String updateTime;
    /** The updatedBy. */
    @JsonProperty
    public String updatedBy;
    @JsonProperty
    public String domain;

    @JsonProperty
    public boolean canedit = true;

    @JsonProperty
    public boolean candelete = true;

    /**
     * Constructor.
     */
    public UICredential() {
        super();

        password = CURRENT_PASSWORD;
        gatewayospassword = CURRENT_PASSWORD;
        communityString = CURRENT_PASSWORD;
        authorizationProtocolPassword = CURRENT_PASSWORD;
        privacyProtocolPassword = CURRENT_PASSWORD;

    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "UICredential [id=" + id + ", username=" + username + ", typeId=" + typeId + ", credentialsName=" + credentialsName
                + ", enableCertificateCheck=" + enableCertificateCheck + ", credentialProtocol=" + credentialProtocol
                + ", snmpVersionId=" + snmpVersionId + ", snmpUsername=" + snmpUsername + ", authorizationProtocolId=" + authorizationProtocolId
                + ", privacyProtocolId=" + privacyProtocolId
                + ", numberOfDevices=" + numberOfDevices + ", createdBy=" + createdBy
                + ", creationTime=" + creationTime + ", updateTime" + updateTime + ", updatedBy=" + updatedBy + "]";
    }

    /**
     * Clone Json object.
     *
     * @return the string
     */
    @Override
    public UICredential clone() throws CloneNotSupportedException {
        ObjectMapper mapper = new ObjectMapper();
        String json = "";
        UICredential uic = (UICredential) super.clone();
        try {
            json = mapper.writeValueAsString(this);
            uic = mapper.readValue(json, UICredential.class);
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return uic;
    }


}
