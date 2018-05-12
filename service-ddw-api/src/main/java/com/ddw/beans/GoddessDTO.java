package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 女神
 */
@ApiModel(value="女神用例对像",description="用例对像GoddessDTO")
public class GoddessDTO {
    @ApiModelProperty(name="tableNo",value="店内桌号（桌名）",example="103")
    private String tableNo;

    public String getTableNo() {
        return tableNo;
    }

    public void setTableNo(String tableNo) {
        this.tableNo = tableNo;
    }

}
