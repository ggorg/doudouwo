package com.ddw.beans;

import java.util.List;

public class GoodsPageModelDTO {
    private Integer id;
    private Integer platePos;
    private List<GoodsPageModelItemDTO> gitem;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPlatePos() {
        return platePos;
    }

    public void setPlatePos(Integer platePos) {
        this.platePos = platePos;
    }

    public List<GoodsPageModelItemDTO> getGitem() {
        return gitem;
    }

    public void setGitem(List<GoodsPageModelItemDTO> gitem) {
        this.gitem = gitem;
    }

    @Override
    public String toString() {
        return "GoodsPageModelDTO{" +
                "id=" + id +
                ", platePos=" + platePos +
                ", gitem=" + gitem +
                '}';
    }
}
