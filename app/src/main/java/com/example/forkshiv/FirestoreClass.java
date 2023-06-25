package com.example.forkshiv;

import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;

import com.example.forkshiv.Models.AddRoomModels;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreClass extends ConstantlyUsedFragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Constants constants = new Constants();

    public String addRoomToFireStore(AddRoomModels roomDetails, String id, FragmentActivity activity){
        showProgressDialog(activity);
         id = db.collection(constants.PRODUCT_KEY).document().getId();
         roomDetails.productId = id;
        db.collection(constants.PRODUCT_KEY).document(id).set(roomDetails, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //hiding the dialog and launching locality fragment
                            onSuccesss(activity,new LocalityDetailsFragment());
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                         hideProgressDialog();
                        Toast.makeText(activity, "Error while uploading the product, please try again", Toast.LENGTH_SHORT).show();
                    }});
        return id;
    }

    public void update(FragmentActivity activity,HashMap<String,Object> map,String productId){
        showProgressDialog(activity);
        DocumentReference docRef =  db.collection(constants.PRODUCT_KEY).document(productId);

        // Update the data in the document
        docRef.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               hideProgressDialog();
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                     hideProgressDialog();
                     Toast.makeText(activity, "Error while uploading the product,please try again", Toast.LENGTH_SHORT).show();
                    }});
    }

    public ArrayList<AddRoomModels> get(FragmentActivity activity, ArrayList<AddRoomModels> roomModels){
        showProgressDialog(activity);
        db.collection(constants.PRODUCT_KEY).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                hideProgressDialog();
              List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
              for(DocumentSnapshot d:list){
                 Map<String,Object> map = d.getData();
                 AddRoomModels model = new AddRoomModels();
                  model.locality = (String)map.get(constants.LOCALITY);
                  roomModels.add(model);
              }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                hideProgressDialog();
            }
        });
        if(roomModels.size()>0){
            Log.d("FirestoreCLass","safsfs");
        }
        return roomModels;
    }
}
