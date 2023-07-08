package com.example.forkshiv;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
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
import java.util.Objects;


public class MyRoomsFragment extends ConstantlyUsedFragment {
    BigListAdapter2 adapter;
    static ArrayList<AddRoomModels> myRooms;
    FirestoreClass firestoreClassObj;
    RecyclerView rv;
    Constants constants;
    FirebaseFirestore db;
   public boolean isContexualMode = false;
     HashSet<Item> selected;
     Button deleteButton;
     Button cancelButton;
     int count = 0;
     TextView noRoomTextView;
    private OnBackPressedCallback onBackPressedCallback;
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
//         ActionBar actionBar = getActivity().getActionBar();
//       actionBar.setTitle("");
//         getActivity().getActionBar().setIcon(R.drawable.your_room);
        noRoomTextView = v.findViewById(R.id.no_room_tv);
        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(count ==1){
                    unselect();
                }else {
                    ImageButton button = getActivity().findViewById(R.id.home_image_button);
                    ImageButton roombt = getActivity().findViewById(R.id.myroom_image_button);
                    roombt.setImageResource(R.drawable.my_room_outlined);
                    button.setImageResource(R.drawable.home_filled);
                    replaceFragment(new HomeFragment());
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCallback);
        firestoreClassObj = new FirestoreClass();
        deleteButton = v.findViewById(R.id.delete_button);
        cancelButton = v.findViewById(R.id.cancel_bt);
        selected = new HashSet<>();
        constants = new Constants();
        isContexualMode = false;
        db = FirebaseFirestore.getInstance();
        get(getActivity());
        adapter = new BigListAdapter2(getActivity(), myRooms, new BigListAdapter2.OnItemClickListener() {
            @Override
            public void onItemClick(int position,View view) {
                 if(isContexualMode == true){
                     startSelection(position,view);
                 }else{
                     Intent intent = new Intent(getActivity(),RoomDetailedActivity.class);
                     intent.putExtra("RoomFragment",true);
                     startActivity(intent);
                 }
            }
        }, new BigListAdapter2.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position,View view) {
                //if(getActivity().getActionBar() != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
             //   }
                startSelection(position,view);
            }
        });
        rv = v.findViewById(R.id.myroom_rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
        // adapter.notifyDataSetChanged();


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unselect();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
        return v;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onBackPressedCallback.remove();
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
                if(myRooms.isEmpty()){
                    noRoomTextView.setVisibility(View.VISIBLE);
                }else{
                    noRoomTextView.setVisibility(View.GONE);
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
    public void startSelection(int position,View view) {
        if (selected.isEmpty()) {
            selected.add(new Item(position,view));
            view.setBackgroundColor(getResources().getColor(R.color.primary_color_50));
//            if (actionBar != null) {
//                actionBar.hide();
//            }
            deleteButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            count = 1;
            isContexualMode = true;
        } else if (!selected.isEmpty() && selected.contains(new Item(position,view))) {
            Item item = new Item(position,view);
            selected.remove(item);
            view.setBackgroundColor(getResources().getColor(R.color.white));
            if (selected.size() == 0) {
                deleteButton.setVisibility(View.GONE);
                cancelButton.setVisibility(View.GONE);
              //  getAction.show();
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                count = 0;
                isContexualMode = false;
            }
        }else {
            selected.add(new Item(position,view));
            count =1;
            view.setBackgroundColor(getResources().getColor(R.color.primary_color_50));
            isContexualMode = true;
        }
    }

    public void unselect(){
        List<Item> dataList = new ArrayList<>(selected);
        for(int i =0;i<dataList.size();i++){
           dataList.get(i).view.setBackgroundColor(getResources().getColor(R.color.white));
        }
        cancelButton.setVisibility(View.GONE);
        deleteButton.setVisibility(View.GONE);
        isContexualMode = false;
        count = 0;
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        selected.clear();
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);

        fragmentTransaction.commit();
    }
    //deleting the selected items
    public void deleteSelected(){
           // showProgressDialog();
            List<Item> dataList = new ArrayList<>(selected);
            for(int i =0;i<dataList.size();i++){
                deleteFromDatabase(myRooms.get(dataList.get(i).position));
                myRooms.remove(dataList.get(i).position);
            }
           // hideProgressDialog();
          //  Toast.makeText(getApplicationContext(), "Notes deleted", Toast.LENGTH_SHORT).show();
            cancelButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
            isContexualMode = false;
           ((AppCompatActivity) getActivity()).getSupportActionBar().show();
            selected.clear();
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
                       count = 0;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        unselect();
                        count = 0;
                    }
                })
                .show();
    }
    public class Item {
        int position;
        View view;

        public Item(int position, View view) {
            this.position = position;
            this.view = view;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }
            Item otherItem = (Item) other;
            return position == otherItem.position && Objects.equals(view, otherItem.view);
        }

        @Override
        public int hashCode() {
            return Objects.hash(position, view);
        }
    }

}