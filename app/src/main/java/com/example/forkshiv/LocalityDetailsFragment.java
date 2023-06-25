package com.example.forkshiv;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.firestore.FirebaseFirestore;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;


public class LocalityDetailsFragment extends ConstantlyUsedFragment{
    Constants constants;
    FirestoreClass firestoreClassObj;
    EditText cityEt, localityEt;
    Button saveButton;
    String mFilename;
    HashMap<String,Object> map;
    ConstraintLayout contraintLayout;
     FirebaseFirestore db;
     SerializeLocation serializeLocationObj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_locality_details, parent, false);
      //initializing fields
        cityEt = (EditText)v.findViewById(R.id.city_editText);
        localityEt = (EditText)v.findViewById(R.id.locality_editText);
        saveButton = (Button)v.findViewById(R.id.save_and_continue);
        map = new HashMap<>();
        constants = new Constants();
        firestoreClassObj = new FirestoreClass();
        db =  FirebaseFirestore.getInstance();
        mFilename = "LOCATION_DETAILS";
        serializeLocationObj = new SerializeLocation();
        contraintLayout = v.findViewById(R.id.cn);
       saveButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                   if (validte()) {
                       AddRoomActivity.photosBt.setEnabled(true);
                       serializeLocationObj.locality = localityEt.getText().toString().toLowerCase();
                       serializeLocationObj.city = cityEt.getText().toString().toLowerCase();
                       map.put(constants.LOCALITY, serializeLocationObj.locality);
                       map.put(constants.CITY, serializeLocationObj.city);

                       //updating the product fields in database and moving to next fragment
                       firestoreClassObj.update(getActivity(), map, BasicDetailsFragment.roomDetails.productId);
                       AddRoomActivity.basicDetailsBt.setTextColor(getResources().getColor(R.color.black));
                       AddRoomActivity.localityDetailsBt.setTextColor(getResources().getColor(R.color.black));
                       AddRoomActivity.photosBt.setTextColor(getResources().getColor(R.color.primary_color));
                       BasicDetailsFragment.constantlyUsedFragment.moveToNextFragment(R.id.fragment_container, new UploadImagesFragment(),
                               getActivity());
                   }
           }
       });


        return v;
    }

    //validating before uploading
    public boolean validte(){
        BasicDetailsFragment.roomDetails.city = cityEt.getText().toString().trim();
        BasicDetailsFragment.roomDetails.locality = localityEt.getText().toString().trim();
        if(BasicDetailsFragment.roomDetails.city.length() <=0){
            showSnackBar(contraintLayout,"Please Enter Your City",true,this);
            return false;
        }
        if(BasicDetailsFragment.roomDetails.locality.length()<=0){
            showSnackBar(contraintLayout,"Please Enter Your Locality",true,this);
            return false;
        }
        return true;
    }

    //serializing to store data
    public void saveLocationDetails(SerializeLocation serializeLocationObj)
            throws JSONException, IOException {
// Build an array in JSON
        JSONObject roomDetailsObj = new JSONObject();
       roomDetailsObj.put(constants.LOCALITY,serializeLocationObj.locality);
        roomDetailsObj.put(constants.CITY,serializeLocationObj.city);

        //    roomDetailsObj.put(constants.OTHER_AMENTIES,jsonArray);
        Writer writer = null;
        try {
            OutputStream out = getContext()
                    .openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(roomDetailsObj.toString());
        }finally {
            if (writer != null)
                writer.close();
        }
    }

    //deserializing the stored data
    public SerializeLocation loadLocationDetails() throws IOException, JSONException {
        SerializeLocation locationDetails = new SerializeLocation();
        BufferedReader reader = null;
        try {
// Open and read the file into a StringBuilder
            InputStream in = getContext().openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
// Line breaks are omitted and irrelevant
                jsonString.append(line);
            }
// Parse the JSON using JSONTokener
            JSONObject array = (JSONObject) new JSONTokener(jsonString.toString())
                    .nextValue();
// Build the array of crimes from JSONObjects
          locationDetails.city = array.getString(constants.CITY);
          locationDetails.locality = array.getString(constants.LOCALITY);

        } catch (FileNotFoundException e) {
// Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null)
                reader.close();
        }
        return locationDetails;
    }

    @Override
    public void onPause() {
        super.onPause();
        //mapView.onPause();
        try {
         //   AddRoomActivity.isCompleted2 = false;
            saveLocationDetails(serializeLocationObj);
        }catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void onStart() {
        super.onStart();
        if(AddRoomActivity.isCompleted == false&&BasicDetailsFragment.isEmpty == false) {
            try {
                serializeLocationObj = loadLocationDetails();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            cityEt.setText(serializeLocationObj.city);
            localityEt.setText(serializeLocationObj.locality);
        }else{
            BasicDetailsFragment.isEmpty = false;
        }
        }
    class SerializeLocation{
        String city;
        String locality;

//        public SerializeLocation(String city,String locality){
//            this.locality = locality;
//            this.city = city;
//        }
    }
}