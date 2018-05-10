package com.ddw.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 按钮
 */
@ApiModel
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppIndexButtonVO {

    @JsonProperty("title")
    @ApiModelProperty(name="title",value="按钮名称",example="")
    private String dbTitle;

    @JsonProperty("iconUrl")
    @ApiModelProperty(name="iconUrl",value="按钮图片",example="http://.........")
    private String dbIconUrl;

    @JsonProperty("iconLinkUrl")
    @ApiModelProperty(name="iconLinkUrl",value="标识",example="")
    private String dbIconLinkUrl;

    public String getDbTitle() {
        return dbTitle;
    }

    public void setDbTitle(String dbTitle) {
        this.dbTitle = dbTitle;
    }

    public String getDbIconUrl() {
        return dbIconUrl;
    }

    public void setDbIconUrl(String dbIconUrl) {
        this.dbIconUrl = dbIconUrl;
    }

    public String getDbIconLinkUrl() {
        return dbIconLinkUrl;
    }

    public void setDbIconLinkUrl(String dbIconLinkUrl) {
        this.dbIconLinkUrl = dbIconLinkUrl;
    }
}
