package com.tw2.phimhoathinh.model;

public class MyVideo {
    private String name;
    private String image;
    private String idVideo;
    private String idPlayList;

    public MyVideo(String name, String image, String idVideo) {
        this.name = name;
        this.image = image;
        this.idVideo = idVideo;
    }

    public MyVideo(String name, String image, String idVideo, String idPlayList) {
        this.name = name;
        this.image = image;
        this.idVideo = idVideo;
        this.idPlayList = idPlayList;
    }

    public String getIdPlayList() {
        return idPlayList;
    }

    public void setIdPlayList(String idPlayList) {
        this.idPlayList = idPlayList;
    }

    public MyVideo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(String idVideo) {
        this.idVideo = idVideo;
    }
}
