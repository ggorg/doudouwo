package com.ddw.beans;

import java.util.List;

public class GoodsPageModelItemDTO {
    private String name;
    private List<Integer> gids;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getGids() {
        return gids;
    }

    public void setGids(List<Integer> gids) {
        this.gids = gids;
    }

    @Override
    public String toString() {
        return "GoodsPageModelItemDTO{" +
                "name='" + name + '\'' +
                ", gids=" + gids +
                '}';
    }
}
