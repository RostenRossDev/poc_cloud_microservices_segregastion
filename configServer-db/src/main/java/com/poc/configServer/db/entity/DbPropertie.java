package com.poc.configServer.db.entity;

import javax.persistence.*;
import java.util.Date;

public class DbPropertie {
   
    private Long id;

    private String key;

    private String value;

    private String application;

    private String profile;

    private String label;

    private Date created_on;

    public DbPropertie(){}

    public DbPropertie(Long id, String key, String value, String application, String profile, String label, Date created_on) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.application = application;
        this.profile = profile;
        this.label = label;
        this.created_on = created_on;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Date getCreated_on() {
        return created_on;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
    
}
