package com.example.forkshiv;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends AppCompatActivity {
  ImageButton homeImagebt,myRoomsImagebt;
  FloatingActionButton addFab;
  int REQUEST_CODE = 1;
  Boolean result = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.action_bar);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#F80202"));
        actionBar.setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_home);
        addFab = findViewById(R.id.add_fab_button);

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //checking if user is not locked in
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                  //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(HomeActivity.this, AddRoomActivity.class);
                    intent.putExtra("isCompleted", result);
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
        homeImagebt = (ImageButton)findViewById(R.id.home_image_button);
        myRoomsImagebt = (ImageButton)findViewById(R.id.myroom_image_button);
        homeImagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionBar.show();
                setIcon(R.drawable.home_filled,homeImagebt);
                setIcon(R.drawable.my_room_outlined,myRoomsImagebt);

                replaceFragment(new HomeFragment());
            }
        });
        myRoomsImagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
//                    if(actionBar != null) {
//                        actionBar.hide();
//                    }
                    setIcon(R.drawable.my_room_filled,myRoomsImagebt);
                    setIcon(R.drawable.home_outlined,homeImagebt);
                    replaceFragment(new MyRoomsFragment());
                }
            }
        });
        replaceFragment(new HomeFragment());
    }


    //getting the result as user has successfully registered his product or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // retrieve the result data from the Intent
             result = data.getBooleanExtra("RESULT_KEY",false);
        }else{
            result =false;
        }
    }

    //lauching new fragment
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);

        fragmentTransaction.commit();
    }
    public void setIcon(int icon, ImageButton imageButton){
        imageButton.setImageResource(icon);
    }
   // @Override
//    public void onBackPressed() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_container);
//        if(fragment instanceof MyRoomsFragment){
//            if (fragmentManager.getBackStackEntryCount() > 1) {
//                fragmentManager.popBackStack();
//                //  getActionBar().show();
//            }else {
//                super.onBackPressed();
//            }
//        }
//        else {
//            super.onBackPressed();
//        }
//    }
}