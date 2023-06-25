package com.example.forkshiv;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forkshiv.Models.AddRoomModels;
import com.example.forkshiv.Models.BigListAdapter;
import com.example.forkshiv.Models.BigListModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    RecyclerView searchRecyclerView;
   static ArrayList<AddRoomModels> al;
   ImageButton backButton;
    FirebaseFirestore db;
    SearchView searchView;
    BigListAdapter adapter;
    Constants constants;
    ProgressDialog progressDialog;
    int pos;
    int tennantSpinnerCount,waterSpinnerCount,roomSpinnerCount,apartmentSpinnerCount;
    int tennant,room,water,apartment = 0;
    Spinner tennantTypeSpinner,roomTypeSpinner,apartmentTypeSpinner,waterSupplySpinner;
    TextView emptyTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_search);
        searchRecyclerView = findViewById(R.id.search_rv);
        al = new ArrayList<AddRoomModels>();
        backButton = findViewById(R.id.back_button);
        constants = new Constants();
        tennantTypeSpinner = findViewById(R.id.tennants_type_spinner);
        roomTypeSpinner = findViewById(R.id.room_type_spinner);
        apartmentTypeSpinner = findViewById(R.id.apartment_type_spinner);
        waterSupplySpinner = findViewById(R.id.water_supply_spinner);
        searchView = findViewById(R.id.search_view);
        db = FirebaseFirestore.getInstance();
        emptyTv = findViewById(R.id.empty_tv);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        searchRecyclerView.setHasFixedSize(true);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        Intent intent = getIntent();
        searchView.setIconifiedByDefault(false);
        searchView.setQuery(intent.getStringExtra("text"), false);
        adapter = new BigListAdapter(getApplicationContext(), al, new BigListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                 Intent intent = new Intent(getApplicationContext(),RoomDetailedActivity.class);
                 intent.putExtra("Position",position);
                 startActivity(intent);
            }
        });
        searchRecyclerView.setAdapter(adapter);
        initializeSpinner(tennantTypeSpinner, constants.prefferedTennants);
        initializeSpinner(roomTypeSpinner, constants.roomType);
        initializeSpinner(apartmentTypeSpinner, constants.apartmentType);
        initializeSpinner(waterSupplySpinner, constants.waterSupply);


         pos = getIntent().getIntExtra(constants.ROOM_FOR,10);
        if(pos != 10){
            tennantTypeSpinner.setSelection(pos+1);
        }else{
            pos = getIntent().getIntExtra(constants.HOSTEL,10);
            if(pos!=10){
                apartmentTypeSpinner.setSelection(3);
                tennantTypeSpinner.setSelection(pos+1);
            }
        }

        tennantSpinnerCount = tennantTypeSpinner.getSelectedItemPosition();
        roomSpinnerCount = roomTypeSpinner.getSelectedItemPosition();
        waterSpinnerCount = waterSupplySpinner.getSelectedItemPosition();
        apartmentSpinnerCount = apartmentTypeSpinner.getSelectedItemPosition();

        if(tennantSpinnerCount==0&&roomSpinnerCount ==0&&waterSpinnerCount==0&&apartmentSpinnerCount==0) {
            get(searchView.getQuery().toString(), tennantSpinnerCount
                    , roomSpinnerCount,
                 apartmentSpinnerCount,waterSpinnerCount);
        }

                tennantTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(tennant <2&&position ==0){
                            tennant = tennant+1;
                        }else {
                            tennantSpinnerCount = position;
                            get(searchView.getQuery().toString(), tennantSpinnerCount
                                    , roomSpinnerCount,
                                    apartmentSpinnerCount,waterSpinnerCount);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });
                roomTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (room<2 && position == 0) {
                            room = room+1;
                        } else{
                            roomSpinnerCount = position;
                            get(searchView.getQuery().toString(), tennantSpinnerCount
                                    , roomSpinnerCount,
                                    apartmentSpinnerCount,waterSpinnerCount);
                    }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });

                apartmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(apartment<2&&position==0){
                            apartment =apartment+1;
                        }else {
                            apartmentSpinnerCount = position;
                            get(searchView.getQuery().toString(), tennantSpinnerCount
                                    , roomSpinnerCount,
                                    apartmentSpinnerCount,waterSpinnerCount);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Do nothing
                    }
                });
//
        waterSupplySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               if(water<2&&position == 0){
                   water = water+1;
               }else {
                   waterSpinnerCount = position;
                   get(searchView.getQuery().toString(), tennantSpinnerCount
                           , roomSpinnerCount,
                           apartmentSpinnerCount,waterSpinnerCount);
               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });


       // searchview implementation
          searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
              @Override
              public boolean onQueryTextSubmit(String query) {
                  get(searchView.getQuery().toString(), tennantSpinnerCount
                          , roomSpinnerCount,
                          apartmentSpinnerCount,waterSpinnerCount);
                  return true;
              }

              @Override
              public boolean onQueryTextChange(String newText) {
                  return true;
              }
          });

        }

    public void initializeSpinner(Spinner spinner, String[]  array){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }


    public void get(String searchText,int tennant,int roomType,int apartmentType,int waterSupply){
        CollectionReference collectionRef = db.collection(constants.PRODUCT_KEY);
        showProgressDialog();
// Start with a basic query
        Query query = collectionRef;
        //query = query.whereEqualTo(constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        if(tennant>0){
            query = query.whereEqualTo(constants.PREFFERED_TENNANT,(tennant-1));
        }
        if(apartmentType>0){
            query = query.whereEqualTo(constants.APARTMENT_TYPE,(apartmentType-1));
        }
        if(roomType>0){
            query = query.whereEqualTo(constants.ROOM_TYPE,(roomType-1));
        }
        if(waterSupply>0){
            query = query.whereEqualTo(constants.WATERSUPPLY,waterSupply);
        }
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                hideProgress();
                al.clear();
                adapter.notifyDataSetChanged();
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        Map<String, Object> map = d.getData();
                        String locality = (String)map.get(constants.LOCALITY);
                        if(contains(locality,searchText,(String)map.get(constants.CITY))||pos !=10) {
                            AddRoomModels model = new AddRoomModels();
                            model.locality = (String) map.get(constants.LOCALITY);
                            model.images = (ArrayList<String>) map.get(constants.IMAGES);
                            model.contact = (String) map.get(constants.CONTACT);
                            model.prefferdTennats = (long) map.get(constants.PREFFERED_TENNANT);
                            model.expectedRent = (String) map.get(constants.EXPECTED_RENT);
                            model.roomType = (long) map.get(constants.ROOM_TYPE);
                            model.bathroom = (long) map.get(constants.BATHROOM);
                            model.kitchen = (long) map.get(constants.KITCHEN);
                            model.toatalFloor = (String) map.get(constants.TOTAL_FLOOR);
                            model.floorNumber = (String) map.get(constants.FLOOR_NUMBER);
                            model.bed = (boolean) map.get(constants.BED);
                            model.ac = (boolean) map.get(constants.AC);
                            model.freez = (boolean) map.get(constants.FREEZ);
                            model.cooler = (boolean) map.get(constants.COOLER);
                            model.television = (boolean) map.get(constants.TV);
                            model.city = (String) map.get(constants.CITY);
                            model.productId = (String) map.get(constants.PRODUCT_ID);
                            model.waterSupply = (long) map.get(constants.WATERSUPPLY);
                            al.add(model);
                        }

                }
                if(al.size() == 0){
                    emptyTv.setVisibility(View.VISIBLE);
                }else{
                    emptyTv.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
               hideProgress();
            }
        });
    }

    public static boolean contains(String locality,String search,String city) {
        String lo[] = locality.split(" ");
        String se[] = search.split(" ");
        boolean isTrue = false;
        HashSet<String> set = new HashSet<>();
        for(int i =0;i<se.length;i++) {
            set.add(se[i].toLowerCase().trim());
            if(set.contains(city.trim().toLowerCase())) {
                if(se.length == 1){
                    return true;
                }
                isTrue = true;
            }
        }
        if(isTrue == false) {
            return false;
        }

        for(int i =0;i<lo.length;i++) {
            if(set.contains(lo[i].trim().toLowerCase())){
                return true;
            }
        }
        return false;
    }

    public void showProgressDialog(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait..."); // Setting Message
        //progressDialog.setTitle("ProgressDialog"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
        progressDialog.show();
    }
    public void hideProgress(){
        progressDialog.dismiss();
    }
}