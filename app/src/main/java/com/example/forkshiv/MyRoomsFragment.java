package com.example.forkshiv;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.forkshiv.Models.AddRoomModels;
import com.example.forkshiv.Models.BigListAdapter;
import com.example.forkshiv.Models.BigListAdapter2;
import com.example.forkshiv.Models.BigListSliderAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.SliderView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class MyRoomsFragment extends ConstantlyUsedFragment {
    BigListAdapter2 adapter;
    static ArrayList<AddRoomModels> myRooms;
    FirestoreClass firestoreClassObj;
    RecyclerView rv;
    Constants constants;
    FirebaseFirestore db;
   public boolean isContexualMode = false;
     HashSet<Integer> selected =  new HashSet<>();
    public Toolbar toolbar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_rooms, container, false);
        myRooms = new ArrayList<>();
        toolbar = v.findViewById(R.id.toolbar);
        getActivity().setActionBar(toolbar);
        getActivity().getActionBar().setTitle("");
        getActivity().getActionBar().setIcon(R.drawable.your_room);
       // toolbar.inflateMenu(R.menu.delete_menu);
        firestoreClassObj = new FirestoreClass();
        selected = new HashSet<>();
        constants = new Constants();
        isContexualMode = false;
        db = FirebaseFirestore.getInstance();
        get(getActivity());
        adapter = new BigListAdapter2(getActivity(), myRooms, new BigListAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(CheckBox checkBox,int position) {
                 if(isContexualMode == true){
                     startSelection(position,checkBox);
                 }else{
                     Intent intent = new Intent(getActivity(),RoomDetailedActivity.class);
                     intent.putExtra("RoomFragment",true);
                     startActivity(intent);
                 }
            }
        }, new BigListAdapter2.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(CheckBox checkBox, int position) {
                startSelection(position,checkBox);
            }
        });
        rv = v.findViewById(R.id.myroom_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        // adapter.notifyDataSetChanged();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_item_delete_room){
            showDeleteConfirmationDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    //getting the added products of the user
    public void get(FragmentActivity activity){
        showProgressDialog(activity);
        db.collection(constants.PRODUCT_KEY).
                whereEqualTo(constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                hideProgressDialog();
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
                    myRooms.add(model);
                    adapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                hideProgressDialog();
            }
        });
    }

    //selecting and unselecting items
    public void startSelection(int position,CheckBox checkBox){
        if(selected.isEmpty()){
            selected.add(position);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
            isContexualMode = true;
            toolbar.inflateMenu(R.menu.delete_menu);
        }
       else if(!selected.isEmpty()&&selected.contains(position)){
            selected.remove(position);
            checkBox.setChecked(false);
            checkBox.setVisibility(View.GONE);
            if(selected.size() == 0){
                toolbar.getMenu().clear();
                isContexualMode = false;
            }
        }else{
            selected.add(position);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
        }
     //  adapter.notifyDataSetChanged();
    }

    //deleting the selected items
    public void deleteSelected(){
        for(int i =0;i<myRooms.size();i++){
           if(selected.contains(i)){
               AddRoomModels models = myRooms.remove(i);
               deleteFromDatabase(models);
           }
        }
        adapter.notifyDataSetChanged();
    }

    //deleting the selected items from database
    public void deleteFromDatabase(AddRoomModels models){
        CollectionReference collectionRef = db.collection(constants.PRODUCT_KEY);
        DocumentReference documentRef = collectionRef.document(models.productId);

        documentRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // The document was successfully deleted, if it existed
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // The document could not be deleted, possibly due to permissions or network issues
                    }
                });
    }

    //showing the confirmation dialoge box
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Item")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Perform delete action here
                       deleteSelected();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}