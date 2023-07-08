package com.example.forkshiv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class ConstantlyUsedFragment extends Fragment {
    ProgressDialog progressDialog;


    public void showSnackBar(View view, String errorMessage, boolean istrue, Fragment fragment){
        Snackbar snackbar = Snackbar.make(view,errorMessage, BaseTransientBottomBar.LENGTH_SHORT);
        if(istrue == true){
            snackbar.setBackgroundTint(getResources().getColor(R.color.color_error));
        }else{
            snackbar.setBackgroundTint(getResources().getColor(R.color.color_successful));
        }
        snackbar.show();
    }

    public void showProgressDialog(FragmentActivity activity){
        progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage("please wait..."); // Setting Message
        //progressDialog.setTitle("ProgressDialog"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show();
    }
    public void hideProgressDialog(){
        if (!isDetached() && progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void moveToNextFragment(int container,Fragment fragment,FragmentActivity activity){
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public  void onSuccesss(FragmentActivity activity,Fragment fragment){
        hideProgressDialog();
        moveToNextFragment(R.id.fragment_container,fragment,activity);
    }
}
