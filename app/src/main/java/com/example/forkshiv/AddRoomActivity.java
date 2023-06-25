package com.example.forkshiv;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forkshiv.Models.AddRoomModels;


public class AddRoomActivity extends AppCompatActivity {

   static Button basicDetailsBt, localityDetailsBt,photosBt;
   static boolean isCompleted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_add_room);

        basicDetailsBt = findViewById(R.id.basic_details_tv);
        localityDetailsBt = findViewById(R.id.locality_details_tv);
        photosBt = findViewById(R.id.photos_tv);
        replaceFragment(new BasicDetailsFragment());
        isCompleted =  getIntent().getBooleanExtra("isCompleted",false);
        basicDetailsBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basicDetailsBt.setTextColor(getResources().getColor(R.color.primary_color));
                localityDetailsBt.setTextColor(getResources().getColor(R.color.black));
                photosBt.setTextColor(getResources().getColor(R.color.black));
                replaceFragment(new BasicDetailsFragment());
            }
        });
        localityDetailsBt.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   basicDetailsBt.setTextColor(getResources().getColor(R.color.black));
                   localityDetailsBt.setTextColor(getResources().getColor(R.color.primary_color));
                   photosBt.setTextColor(getResources().getColor(R.color.black));
                   replaceFragment(new LocalityDetailsFragment());
               }
           });
        photosBt.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  basicDetailsBt.setTextColor(getResources().getColor(R.color.black));
                  localityDetailsBt.setTextColor(getResources().getColor(R.color.black));
                  photosBt.setTextColor(getResources().getColor(R.color.primary_color));
                  replaceFragment(new UploadImagesFragment());
              }
          });

    }



    //launcing the another fragment
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }
}