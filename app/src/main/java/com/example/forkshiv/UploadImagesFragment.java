package com.example.forkshiv;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.forkshiv.Models.AddRoomModels;
import com.example.forkshiv.Models.PhotoAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nullable;

public class UploadImagesFragment extends ConstantlyUsedFragment {
    //for reading external storage

    //i need to implement serialization here or later try implementing the singleton class.
    String a[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
    Button uploadImages,rentOut;
    RecyclerView imagesRv;
    ArrayList<Uri> imagesUri;
    int readExStoragePermissonCode = 102;
    int gettingImagesCode = 101;
    PhotoAdapter adapter;
    StorageReference storageRef;
    FirebaseStorage storage;
    Constants constants;
    HashMap<String,Object> map;
    String mFilename = "IMAGES";
    ConstraintLayout constraintLayout;
    FirestoreClass firestoreClassObj;
    ConstantlyUsedFragment constantlyUsedFragment;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_upload_images, parent, false);
        //initializing buttons and recyclerview
        uploadImages = (Button) v.findViewById(R.id.uploadImage_button);
        imagesRv =  v.findViewById(R.id.images_rv);
        imagesUri = new ArrayList<>();
        constants = new Constants();
        rentOut = v.findViewById(R.id.rent_out);
        map = new HashMap<>();
        firestoreClassObj = new FirestoreClass();
        //initializing firestore and storage instance
        storage =  FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        uploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if permission already granted then just reading the external storage and calling startactivityforresult
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "You Can Select Multiple Images"), gettingImagesCode);
                } else {
                    //asking for permission to read external storage
                    ActivityCompat.requestPermissions(getActivity(), a, readExStoragePermissonCode);
                }
            }
        });

        //giving the result back to the parent activity that user has finished his room and giving rent
            rentOut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    map.put(constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                    firestoreClassObj.update(getActivity(),map,BasicDetailsFragment.roomDetails.productId);
                    if (imagesUri.size() > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("RESULT_KEY", true); // replace with your own result key and value
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();
                    }else{
                        constraintLayout = v.findViewById(R.id.cn);
                        showSnackBar(constraintLayout,"Please Upload Images",true,UploadImagesFragment.this);
                    }
                }
            });
        //using photoadapter to get images in recyclerview.
         adapter = new PhotoAdapter(getContext(),imagesUri);
        imagesRv.setHasFixedSize(true);
        imagesRv.setLayoutManager(new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL, false));
        imagesRv.setAdapter(adapter);
        return v;
    }


    //checking that user has granted permission or not
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == readExStoragePermissonCode) {
            //checking if the user has granted the permission and if granted then reading
            //the external storage and calling startactivityfor result to get selected files
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Multiple Files"), gettingImagesCode);
            } else {
                //showing the toast as user denied the read external storage permission
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    //getting the activity result for images
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // getting result of startactivitybyresult
        if (requestCode == gettingImagesCode && resultCode == getActivity().RESULT_OK && data!=null) {
            // if user is selcting multiple images.
            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                     uploadToStorage(uri);
                }
            }else{
                Uri uri = data.getData();
                uploadToStorage(uri);
            }
        }
    }


    //uploading images to storage
    public void uploadToStorage(Uri uri){
        final StorageReference imageRef = storageRef.child("images/"+System.currentTimeMillis());
        UploadTask uploadTask = imageRef.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                     Uri downloadUri = task.getResult();
                     imagesUri.add(downloadUri);
                     map.put(constants.IMAGES,imagesUri);
                  //   map.put(constants.USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                     firestoreClassObj.update(getActivity(),map,BasicDetailsFragment.roomDetails.productId);
                     adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(getContext(),"failed",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}