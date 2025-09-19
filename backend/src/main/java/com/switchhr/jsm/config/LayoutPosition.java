package com.switchhr.jsm.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class LayoutPosition {

    @NotNull
    @Min(0)
    @JsonProperty("row")
    private Integer row;

    @NotNull
    @Min(0)
    @JsonProperty("column")
    private Integer column;

    @NotNull
    @Min(1)
    @JsonProperty("span")
    private Integer span;

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getSpan() {
        return span;
    }

    public void setSpan(Integer span) {
        this.span = span;
    }
}
