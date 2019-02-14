package com.dell.asm.ui.model.repository;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.dell.asm.asmcore.asmmanager.client.osrepository.OSRepository;
import com.dell.asm.ui.model.UIBaseObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The Class UIRepository
 * Represents an OS repository
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UIRepository extends UIBaseObject {

    private static final String CURRENT_PASSWORD = "$CURRENT$";

    /** The id. */
    @JsonProperty
    public String id;

    @JsonProperty
    public String createdBy;

    @JsonProperty
    public Date createdDate;

    @JsonProperty
    public String name;

    @JsonProperty
    public String filepath;

    @JsonProperty
    public String state;

    @JsonProperty
    public String imagetype;

    @JsonProperty
    public Boolean isInUse;

    public String username;

    public String password;

    @JsonProperty
    public String rcmPath;

    @JsonProperty
    public boolean isRCM;

    public UIRepository() {
        super();
    }

    public UIRepository(OSRepository repo) {
        this.id = repo.getId();
        this.createdBy = repo.getCreatedBy();
        this.createdDate = repo.getCreatedDate();
        this.imagetype = repo.getImageType();
        this.name = repo.getName();
        this.filepath = repo.getSourcePath().trim();
        this.state = repo.getState();
        this.isInUse = repo.getInUse();
        this.username = repo.getUsername();
        this.rcmPath = repo.getRcmPath();
        this.isRCM = repo.getFirmwareRepository() != null;

        if (repo.getPassword() == null) {
            this.password = UIRepository.CURRENT_PASSWORD;
        } else {
            this.password = repo.getPassword();
        }
    }

    public OSRepository toOSRepository() {
        OSRepository repo = new OSRepository();
        repo.setId(this.id);
        repo.setCreatedBy(this.createdBy);
        repo.setCreatedDate(this.createdDate);
        repo.setImageType(this.imagetype);
        repo.setName(this.name);
        repo.setSourcePath(this.filepath);
        repo.setState(this.state);
        repo.setRcmPath(this.rcmPath);

        if (UIRepository.CURRENT_PASSWORD.equals(this.password)) {
            repo.setPassword(null);
        } else {
            repo.setPassword(this.password);
        }

        if (StringUtils.isEmpty(this.username)) {
            repo.setUsername(null);
        } else {
            repo.setUsername(this.username);
        }
        return repo;
    }

}
