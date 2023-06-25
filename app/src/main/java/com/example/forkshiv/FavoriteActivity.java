package com.example.forkshiv;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.forkshiv.Models.AddRoomModels;
import com.example.forkshiv.Models.BigListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteActivity extends AppCompatActivity {
    FirebaseFirestore db;
    RecyclerView recyclerView;
    Constants constants;
   static ArrayList<AddRoomModels> al;
    static BigListAdapter adapter;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.favorite_action_bar);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#F80202"));
        actionBar.setBackgroundDrawable(colorDrawable);
        setContentView(R.layout.activity_favorite);
        db = FirebaseFirestore.getInstance();
        constants = new Constants();
        al = new ArrayList<>();
        recyclerView = findViewById(R.id.favorite_rv);
        adapter =  new BigListAdapter(getApplicationContext(), al, new BigListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
             Intent intent = new Intent(FavoriteActivity.this,RoomDetailedActivity.class);
             intent.putExtra("Position",position);
             intent.putExtra("parent" ,constants.FAVORITE);
             startActivity(intent);
            }
        });
        textView = findViewById(R.id.empty_tv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        get();

    }


    public void get(){
        db.collection(constants.FAVORITE).
                whereEqualTo(constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
              //  hideProgressDialog();
                List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot d:list){
                    Map<String,Object> map = d.getData();
                    AddRoomModels model = new AddRoomModels();
                    model.locality = (String)map.get(constants.LOCALITY);
                    model.images = (ArrayList<String>)map.get(constants.IMAGES);
                    model.contact = (String)map.get(constants.CONTACT);
                    model.prefferdTennats = (long) map.get(constants.PREFFERED_TENNANT);
                    model.expectedRent = (String)map.get(constants.EXPECTED_RENT);
                    model.roomType = (long)map.get(constants.ROOM_TYPE);
                    model.bathroom = (long)map.get(constants.BATHROOM);
                    model.kitchen = (long)map.get(constants.KITCHEN);
                    model.toatalFloor = (String)map.get(constants.TOTAL_FLOOR);
                    model.floorNumber = (String)map.get(constants.FLOOR_NUMBER);
                    model.bed = (boolean)map.get(constants.BED);
                    model.ac = (boolean)map.get(constants.AC);
                    model.freez = (boolean)map.get(constants.FREEZ);
                    model.cooler = (boolean)map.get(constants.COOLER);
                    model.television = (boolean)map.get(constants.TV);
                    model.city = (String)map.get(constants.CITY);
                    model.productId = (String)map.get(constants.PRODUCT_ID);
                    al.add(model);
                }
                if(al.isEmpty()){
                    textView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
             //   hideProgressDialog();
            }
        });
    }
}