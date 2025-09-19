package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class NotificationConfig {

    @JsonProperty("notifyRequester")
    private Boolean notifyRequester;

    @JsonProperty("notifyManagers")
    private Boolean notifyManagers;

    @JsonProperty("additionalEmails")
    private List<String> additionalEmails;

    public Boolean getNotifyRequester() {
        return notifyRequester;
    }

    public void setNotifyRequester(Boolean notifyRequester) {
        this.notifyRequester = notifyRequester;
    }

    public Boolean getNotifyManagers() {
        return notifyManagers;
    }

    public void setNotifyManagers(Boolean notifyManagers) {
        this.notifyManagers = notifyManagers;
    }

    public List<String> getAdditionalEmails() {
        return additionalEmails;
    }

    public void setAdditionalEmails(List<String> additionalEmails) {
        this.additionalEmails = additionalEmails;
    }
}
