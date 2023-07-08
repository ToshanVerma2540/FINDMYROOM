package com.example.forkshiv;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.forkshiv.Models.AddRoomModels;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.json.JSONArray;
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
import java.util.Map;

public class BasicDetailsFragment extends ConstantlyUsedFragment {
    Spinner apartmentTypeSpinner,roomTypeSpinner,prefferedTennatsSpinner,waterSupplyspinner,kitchenSpinner,bathroomSpinner;
    Button saveAndContinue;
    EditText mobileNumEt,expectedRentEt,floorNumEt,totalFloorEt;
    ConstraintLayout constraintLayout;
    public FirestoreClass firestoreClassObj;
     String mFilename;
    static AddRoomModels roomDetails;
    Constants constants;
    FirebaseAuth mAuth;
    HashMap<String,Object> map;
    static ConstantlyUsedFragment constantlyUsedFragment;
    FirebaseFirestore db;
    CheckBox bedCheckbox,tvCheckbox,acCheckbox,coolerCheckbox,freezCheckbox;
    static boolean isEmpty = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_basic_details, parent, false);
        //getting spinners
        apartmentTypeSpinner = (Spinner)v.findViewById(R.id.apartment_type_spinner);
        roomTypeSpinner = (Spinner)v.findViewById(R.id.room_type_spinner);
        prefferedTennatsSpinner = (Spinner)v.findViewById(R.id.preffered_tennant_spinner);
        waterSupplyspinner = (Spinner)v.findViewById(R.id.water_supply_spinner);
        kitchenSpinner = (Spinner)v.findViewById(R.id.kitchen_spinner);
        bathroomSpinner = (Spinner)v.findViewById(R.id.bathroom_spinner);
        mAuth = FirebaseAuth.getInstance();
        constantlyUsedFragment = new ConstantlyUsedFragment();
        roomDetails = new AddRoomModels();
        map = new HashMap<>();
        //attaching items in spinners
        initializeSpinner(apartmentTypeSpinner,R.array.apartment_type);
        initializeSpinner(roomTypeSpinner,R.array.room_type);
        initializeSpinner(prefferedTennatsSpinner,R.array.prefferd_tennants);
        initializeSpinner(waterSupplyspinner,R.array.water_supply);
        initializeSpinner(kitchenSpinner,R.array.kitchen);
        initializeSpinner(bathroomSpinner,R.array.bathroom);
        db = FirebaseFirestore.getInstance();

        //initializing buttons and edittexts and checkboxes
        saveAndContinue = (Button)v.findViewById(R.id.save_and_continue);
        mobileNumEt = v.findViewById(R.id.mobile_num_editText);
        expectedRentEt = v.findViewById(R.id.expected_rent);
        floorNumEt = v.findViewById(R.id.floor_number_et);
        totalFloorEt = v.findViewById(R.id.total_floor_et);
        constraintLayout = v.findViewById(R.id.cn);
        //roomDetails = new AddRoomModels();
        mFilename = "RoomDetails";
        constants = new Constants();
        firestoreClassObj = new FirestoreClass();
        bedCheckbox = (CheckBox)v.findViewById(R.id.bed_checkbox);
        tvCheckbox = (CheckBox)v.findViewById(R.id.tv_checkbox);
        acCheckbox = (CheckBox)v.findViewById(R.id.ac_checkbox);
        coolerCheckbox = (CheckBox)v.findViewById(R.id.cooler_checkbox);
        freezCheckbox = (CheckBox)v.findViewById(R.id.freez_checkbox);
        saveAndContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             if(validate()){
                AddRoomActivity.localityDetailsBt.setEnabled(true);
                 AddRoomActivity.basicDetailsBt.setTextColor(getResources().getColor(R.color.black));
                 AddRoomActivity.localityDetailsBt.setTextColor(getResources().getColor(R.color.primary_color));
                 AddRoomActivity.photosBt.setTextColor(getResources().getColor(R.color.black));
                 roomDetails.contact = mobileNumEt.getText().toString().trim();
               roomDetails.expectedRent = expectedRentEt.getText().toString().trim();
               roomDetails.floorNumber = floorNumEt.getText().toString().trim();
                 roomDetails.toatalFloor = totalFloorEt.getText().toString().trim();
                 roomDetails.apartMentType = apartmentTypeSpinner.getSelectedItemPosition();
                 roomDetails.roomType = roomTypeSpinner.getSelectedItemPosition();
                 roomDetails.kitchen = kitchenSpinner.getSelectedItemPosition();
                 roomDetails.bathroom = bathroomSpinner.getSelectedItemPosition();
                 roomDetails.prefferdTennats = prefferedTennatsSpinner.getSelectedItemPosition();
                 roomDetails.waterSupply = waterSupplyspinner.getSelectedItemPosition();
                 if(coolerCheckbox.isChecked())roomDetails.cooler =true;
                if(bedCheckbox.isChecked())roomDetails.bed = true;
                if(acCheckbox.isChecked())roomDetails.ac = true;
                if(freezCheckbox.isChecked())roomDetails.freez = true;
                if(tvCheckbox.isChecked())roomDetails.television = true;
                AddRoomActivity.count = 1;
                //Adding data to the firestore

                 if(roomDetails.productId ==""||roomDetails.productId.length()<=0) {

                     roomDetails.productId = firestoreClassObj.addRoomToFireStore(roomDetails, roomDetails.productId, getActivity());
                 }else{
                     //if the product is already added and incomplete just update
                        firestoreClassObj.update(getActivity(),map,roomDetails.productId);
                        constantlyUsedFragment.moveToNextFragment(R.id.fragment_container,new LocalityDetailsFragment(),getActivity());

                 }
                 }
           }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        AddRoomActivity.count = 1;
        super.onAttach(context);
    }

    //initializing the spinners
    public void initializeSpinner(Spinner spinner, int arrayId){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    //validating user entry
    public boolean validate(){
        String mobileNum = mobileNumEt.getText().toString().trim();
        String expectedRent = expectedRentEt.getText().toString().trim();
        String totalFloor = totalFloorEt.getText().toString().trim();
        String floorNum = floorNumEt.getText().toString().trim();
        String waterSupply = waterSupplyspinner.getSelectedItem().toString();
        String kitchen = kitchenSpinner.getSelectedItem().toString();
        String bathroom = bathroomSpinner.getSelectedItem().toString();
        if(mobileNum.length() <=0){
               showSnackBar(constraintLayout,"please enter mobile number",true,this);
               return false;
        }
        if(mobileNum.length()<10){
            showSnackBar(constraintLayout,"please enter correct mobile number",true,this);
            return false;
        }
        if(expectedRent.length()<=0){
            showSnackBar(constraintLayout,"please enter expected rent",true,this);
            return false;
        }

        if(floorNum.length()<=0){
            showSnackBar(constraintLayout,"please enter Floor Number",true,this);
            return false;
        }
        if(totalFloor.length()<=0){
            showSnackBar(constraintLayout,"please enter Total Floor Number",true,this);
            return false;
        }
        if(waterSupply=="Water Supply"||waterSupply.equals("Water Supply")){
            showSnackBar(constraintLayout,"please select water supply",true,this);
            return false;
        }
        if(kitchen == "Kitchen"||kitchen.equals("Kitchen")){
            showSnackBar(constraintLayout,"please select kitchen number",true,this);
            return false;
        }
        if(bathroom == "Bathroom"||kitchen.equals("Bathroom")||bathroom.length()==8){
            showSnackBar(constraintLayout,"please select bathroom number",true,this);
            return false;
        }
        return true;
    }


    //serializing to store data
    public void saveRooomDetails(AddRoomModels roomDetails)
            throws JSONException, IOException {
        JSONObject roomDetailsObj = new JSONObject();
        roomDetailsObj.put(constants.APARTMENT_TYPE,roomDetails.apartMentType);
        roomDetailsObj.put(constants.EXPECTED_RENT,roomDetails.expectedRent);
        roomDetailsObj.put(constants.ROOM_TYPE,roomDetails.roomType);
        roomDetailsObj.put(constants.PREFFERED_TENNANT,roomDetails.prefferdTennats);
        roomDetailsObj.put(constants.TOTAL_FLOOR,roomDetails.toatalFloor);
        roomDetailsObj.put(constants.FLOOR_NUMBER,roomDetails.floorNumber);
        roomDetailsObj.put(constants.KITCHEN,roomDetails.kitchen);
        roomDetailsObj.put(constants.BATHROOM,roomDetails.bathroom);
        roomDetailsObj.put(constants.WATERSUPPLY,roomDetails.waterSupply);
        roomDetailsObj.put(constants.CONTACT,roomDetails.contact);
        roomDetailsObj.put(constants.CITY,roomDetails.city);
        roomDetailsObj.put(constants.LOCALITY,roomDetails.locality);
        roomDetailsObj.put(constants.LATITUDE,roomDetails.latitude);
        roomDetailsObj.put(constants.LONGITUDE,roomDetails.longitude);
        roomDetailsObj.put(constants.BED,roomDetails.bed);
        roomDetailsObj.put(constants.AC,roomDetails.ac);
        roomDetailsObj.put(constants.FREEZ,roomDetails.freez);
        roomDetailsObj.put(constants.COOLER,roomDetails.cooler);
        roomDetailsObj.put(constants.TV,roomDetails.television);
        roomDetailsObj.put(constants.PRODUCT_ID,roomDetails.productId);
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

   //deserializing the saved data
    public AddRoomModels loadRoomDetails() throws IOException, JSONException {
        AddRoomModels roomDetails = new AddRoomModels();
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
            roomDetails.contact = array.getString(constants.CONTACT);
            roomDetails.expectedRent =  array.getString(constants.EXPECTED_RENT);
            roomDetails.floorNumber =  array.getString(constants.FLOOR_NUMBER);
            roomDetails.toatalFloor = array.getString(constants.TOTAL_FLOOR);
            roomDetails.apartMentType =  array.getInt(constants.APARTMENT_TYPE);
            roomDetails.roomType =  array.getInt(constants.ROOM_TYPE);
            roomDetails.kitchen =  array.getInt(constants.KITCHEN);
            roomDetails.bathroom =  array.getInt(constants.BATHROOM);
            roomDetails.prefferdTennats =  array.getInt(constants.PREFFERED_TENNANT);
            roomDetails.waterSupply =  array.getInt(constants.WATERSUPPLY);
            roomDetails.city = array.getString(constants.CITY);
            roomDetails.locality =  array.getString(constants.LOCALITY);
            roomDetails.latitude = array.getInt(constants.LATITUDE);
            roomDetails.longitude = array.getInt(constants.LONGITUDE);
            roomDetails.ac = array.getBoolean(constants.AC);
            roomDetails.bed = array.getBoolean(constants.BED);
            roomDetails.freez = array.getBoolean(constants.FREEZ);
            roomDetails.cooler = array.getBoolean(constants.COOLER);
            roomDetails.television = array.getBoolean(constants.TV);
            roomDetails.productId = array.getString(constants.PRODUCT_ID);
        } catch (FileNotFoundException e) {
// Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null)
                reader.close();
        }
        return roomDetails;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(AddRoomActivity.isCompleted == false) {
                try {
                    roomDetails = loadRoomDetails();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mobileNumEt.setText(roomDetails.contact);
                expectedRentEt.setText(roomDetails.expectedRent);
                totalFloorEt.setText(roomDetails.toatalFloor);
                floorNumEt.setText(roomDetails.floorNumber);
                coolerCheckbox.setChecked(roomDetails.cooler);
                bedCheckbox.setChecked(roomDetails.bed);
                freezCheckbox.setChecked(roomDetails.freez);
                acCheckbox.setChecked(roomDetails.ac);
                tvCheckbox.setChecked(roomDetails.television);
                kitchenSpinner.setSelection((int)roomDetails.kitchen);
                apartmentTypeSpinner.setSelection((int)roomDetails.apartMentType);
                roomTypeSpinner.setSelection((int)roomDetails.roomType);
                bathroomSpinner.setSelection((int)roomDetails.bathroom);
                waterSupplyspinner.setSelection((int)roomDetails.waterSupply);
                prefferedTennatsSpinner.setSelection((int)roomDetails.prefferdTennats);
//        LocalityDetailsFragment.localityEt.setText(roomDetails.locality);
//        LocalityDetailsFragment.cityEt.setText(roomDetails.city);
        }else {
            isEmpty = true;
            AddRoomActivity.isCompleted = false;
        }

        }


    @Override
    public void onPause() {
        super.onPause();
        try {
            saveRooomDetails(roomDetails);
        }catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}