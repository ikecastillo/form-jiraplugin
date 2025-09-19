package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class LayoutConfig {

    @NotBlank
    @JsonProperty("type")
    private String type;

    @NotNull
    @Min(1)
    @JsonProperty("columns")
    private Integer columns;

    @JsonProperty("gap")
    private String gap;

    @JsonProperty("maxWidth")
    private String maxWidth;

    @Valid
    @JsonProperty("responsive")
    private Map<String, LayoutBreakpoint> responsive;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public String getGap() {
        return gap;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }

    public String getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(String maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Map<String, LayoutBreakpoint> getResponsive() {
        return responsive;
    }

    public void setResponsive(Map<String, LayoutBreakpoint> responsive) {
        this.responsive = responsive;
    }
}
