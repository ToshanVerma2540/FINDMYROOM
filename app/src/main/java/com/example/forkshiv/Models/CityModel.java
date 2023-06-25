package com.example.forkshiv.Models;

public class CityModel {
    int image;
    String text;
    public CityModel(int image, String text){
        this.image = image;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
