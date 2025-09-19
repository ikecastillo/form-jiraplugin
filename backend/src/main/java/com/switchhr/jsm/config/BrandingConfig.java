package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class BrandingConfig {

    @JsonProperty("logoUrl")
    private String logoUrl;

    @NotBlank
    @JsonProperty("primaryColor")
    private String primaryColor;

    @JsonProperty("secondaryColor")
    private String secondaryColor;

    @JsonProperty("customCSS")
    private String customCss;

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getCustomCss() {
        return customCss;
    }

    public void setCustomCss(String customCss) {
        this.customCss = customCss;
    }
}
