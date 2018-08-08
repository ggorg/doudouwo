package com.ddw.beans;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Jacky on 2018/8/8.
 */
@ApiModel(value="会员删除相册用例",description="UserDeletePhotoDTO")
public class UserDeletePhotoDTO {
    @ApiModelProperty(name="photograph",value="相册id",dataType ="Integer[]" ,example="")
    private Integer[] photograph;

    public Integer[] getPhotograph() {
        return photograph;
    }

    public void setPhotograph(Integer[] photograph) {
        this.photograph = photograph;
    }
}
