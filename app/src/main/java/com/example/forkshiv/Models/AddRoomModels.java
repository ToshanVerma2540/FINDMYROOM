package com.example.forkshiv.Models;


import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class AddRoomModels {
    public String contact="";
    public String productId="";
    public long prefferdTennats=0;
    public long apartMentType=0;
    public long roomType;
    public String expectedRent="";
    public long waterSupply=0;
    public long kitchen=0;
    public String toatalFloor="";
    public String floorNumber="";
    public String city="";
    public String locality = "";
    public String fullAddress = "";
    public double latitude =0.0;
    public double longitude=0.0;
    public long bathroom =0;
    public boolean cooler =false;public boolean television =false;
    public boolean freez =false;
    public boolean ac =false;
    public boolean bed =false;
    public ArrayList<String> images = new ArrayList<String>();
    public String userId = null;
    public AddRoomModels(){

    }
}
