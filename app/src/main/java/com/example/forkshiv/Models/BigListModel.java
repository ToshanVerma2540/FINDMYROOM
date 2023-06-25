package com.example.forkshiv.Models;

import com.smarteist.autoimageslider.SliderView;

public class BigListModel {

    SliderView sliderView;
    int arr[];
    public BigListModel(){

    }
    public BigListModel(SliderView sliderView, int[] arr) {
        this.sliderView = sliderView;
        this.arr = arr;
    }

}
