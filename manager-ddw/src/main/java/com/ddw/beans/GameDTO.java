package com.ddw.beans;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Jacky on 2018/5/14.
 */
public class GameDTO {
    private int id;
    private String gameName;
    private MultipartFile fileImgShow;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public MultipartFile getFileImgShow() {
        return fileImgShow;
    }

    public void setFileImgShow(MultipartFile fileImgShow) {
        this.fileImgShow = fileImgShow;
    }

}
