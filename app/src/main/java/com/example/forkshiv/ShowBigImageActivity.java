package com.example.forkshiv;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.forkshiv.Models.ImagesAdapter;

import java.util.ArrayList;

public class ShowBigImageActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<String> images;
    int pos;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_show_big_image);
        pos = 0;
        recyclerView = findViewById(R.id.recyclerView);
        //getting the position of the image clicked
        pos = getIntent().getIntExtra("Position",0);
        images = new ArrayList<>();
        //images array from the parent acticity
        images = RoomDetailedActivity.model.images;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.HORIZONTAL, false));
        recyclerView.scrollToPosition(pos);
        ImagesAdapter adapter = new ImagesAdapter(getApplicationContext(),images);
        recyclerView.setAdapter(adapter);
    }

}