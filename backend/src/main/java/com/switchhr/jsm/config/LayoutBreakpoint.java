package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;

public class LayoutBreakpoint {

    @Min(1)
    @JsonProperty("columns")
    private Integer columns;

    @JsonProperty("gap")
    private String gap;

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
}
