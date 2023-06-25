package com.example.forkshiv;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.forkshiv.Models.AddRoomModels;
import com.example.forkshiv.Models.BigListAdapter;
import com.example.forkshiv.Models.BigListSliderAdapter;
import com.example.forkshiv.Models.SliderAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.protobuf.Any;
import com.smarteist.autoimageslider.SliderView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomDetailedActivity extends AppCompatActivity {

    SliderView sliderView;
    CheckBox favoriteCheckbox;
    FirebaseFirestore db;
    Constants constants;
   static AddRoomModels model;
    String parent;
    String furnitureDes;
    int callPermissionCode = 1;
    int pos;
    Button callingButton;
    ImageButton shareButton;
    TextView phoneNumber,rentTv,localityTv,cityTv,roomTennantTv,basicDetailDescriptionTv,furnitureDescriptionTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_room_detailed);
        localityTv = findViewById(R.id.locality_tv);
        cityTv = findViewById(R.id.city_tv);
        roomTennantTv = findViewById(R.id.room_type_tv);
        basicDetailDescriptionTv = findViewById(R.id.basic_detail_description_tv);
        furnitureDescriptionTv = findViewById(R.id.furnitures_description_tv);
        callingButton = findViewById(R.id.call_button);
        shareButton = findViewById(R.id.shareButton);
        db = FirebaseFirestore.getInstance();
        constants = new Constants();
      sliderView = findViewById(R.id.slider_vieww);
      rentTv = findViewById(R.id.expected_rent_tvv);
      phoneNumber = findViewById(R.id.phone_number);
        favoriteCheckbox = findViewById(R.id.favorite_checkbox);

        Intent intent = getIntent();

        // Check if the activity is opened through a deep link
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            // The activity is opened through a deep link
            // Handle the deep link appropriately
            //handleDeepLink(intent.getData());
             get(intent.getData().getQueryParameter("productId"));
        }else {

            // if parent activity is searchactivity
            pos = getIntent().getIntExtra("Position", 0);
            //if parent activity is favorite activity
            parent = getIntent().getStringExtra("parent");
            model = new AddRoomModels();
            //checking the parent activity and setting the favorite accordind to it
            if (parent != null && parent.length() > 0) {
                favoriteCheckbox.setChecked(true);
                model = FavoriteActivity.al.get(pos);
            } else if (getIntent().getBooleanExtra("RoomFragment", false)) {
                model = MyRoomsFragment.myRooms.get(pos);
            } else {
                model = SearchActivity.al.get(pos);
            }
        }
        // calling intent
        callingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RoomDetailedActivity.this, new String[]{Manifest.permission.CALL_PHONE}, callPermissionCode);
                } else {
                    // Permission has already been granted
                    // Call intent here
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                    dialIntent.setData(Uri.parse("tel:" + phoneNumber.getText()));
                    startActivity(dialIntent);
                }

            }
        });

        //google map intent
        localityTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGoogleMaps(model.locality + "," + model.city);
            }
        });

        // setting the textviews
        cityTv.setText(model.city);
        roomTennantTv.setText(constants.roomType[(int) (model.roomType + 1)] + " - " +
                constants.prefferedTennants[(int) (model.prefferdTennats + 1)]);
        basicDetailDescriptionTv.setText(constants.apartmentType[(int) model.apartMentType + 1] + " Room with "
                + model.kitchen + " Kitchen " + "and " + model.bathroom + " Bathroom with Water Supply from " + constants.waterSupply[(int) model.waterSupply]
                + " at " + model.floorNumber + " out of " + model.toatalFloor + " Floor");
        rentTv.setText(model.expectedRent);
        SpannableString spannableString = new SpannableString(model.locality);
        spannableString.setSpan(new UnderlineSpan(), 0, spannableString.length(), 0);
        localityTv.setText(spannableString);
        furnitureDes = "";
        if (model.bed) furnitureDes = "Bed ";
        if (model.ac) furnitureDes = furnitureDes + "Air Conditioner ";
        if (model.television) furnitureDes = furnitureDes + "Television ";
        if (model.cooler) furnitureDes = furnitureDes + "Cooler ";
        if (model.freez) furnitureDes = furnitureDes + "Freez ";
        if (furnitureDescriptionTv.length() > 0) {
            furnitureDescriptionTv.setText(furnitureDes);
        }else{
            furnitureDescriptionTv.setText("Noting");
        }
        phoneNumber.setText(model.contact);
        // setting images in sliderview
        BigListSliderAdapter sliderAdapter = new BigListSliderAdapter(model.images, getApplicationContext(),
                new BigListSliderAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent(RoomDetailedActivity.this, ShowBigImageActivity.class);
                        intent.putExtra("Position", position);
                        startActivity(intent);
                    }
                });
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(sliderAdapter);
        sliderView.setScrollTimeInSec(3000);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();


        // adding to favorite
        favoriteCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    favoriteCheckbox.setChecked(false);
                    Intent intent = new Intent(RoomDetailedActivity.this, LoginActivity.class);
                    // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    if (isChecked) {

                        //if the user is adding to favorite again
                        if (parent != null && parent.length() > 0) {
                            FavoriteActivity.al.add(model);
                            FavoriteActivity.adapter.notifyDataSetChanged();
                        } else {
                            addToFavorite(model);
                        }
                    } else {
                        //if the room is already a favorite and user is removing it.
                        if (parent != null && parent.length() > 0) {
                            if (FavoriteActivity.al.size() > pos) ;
                            FavoriteActivity.al.remove(pos);
                            FavoriteActivity.adapter.notifyDataSetChanged();
                        }
                        delete(model);
                    }
                }
            }
        });


        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(model.productId != null) {
                    String deepLink = "https://www.findmyroom.com/roomdetail?productId=" + model.productId;

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, deepLink);

                    startActivity(Intent.createChooser(shareIntent, "Share using"));
                }
            }
        });
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        handleDeepLink(intent);
//    }
//    Uri data = null;
//    private void handleDeepLink(Intent intent) {
//         data = intent.getData();
//        if (data != null) {
//            String deepLink = data.toString();
//            Intent shareIntent = new Intent(Intent.ACTION_SEND);
//            shareIntent.setType("text/plain");
//            shareIntent.putExtra(Intent.EXTRA_TEXT, deepLink);
//
//            startActivity(Intent.createChooser(shareIntent, "Share using"));
//            // Perform actions based on the deep link data
//            // ...
//        }
    //}

    private void openGoogleMaps(String location) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Specify the Google Maps app package

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            // Handle the case when there is no app available to handle the intent
        }
    }



    // adding to the favorite
    public void addToFavorite(AddRoomModels models){
       models.userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection(constants.FAVORITE).document(
                FirebaseAuth.getInstance().getCurrentUser().getUid()+ models.productId)
                .set(models).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //deleting the favorite
    public void delete(AddRoomModels models){
        CollectionReference collection = db.collection(constants.FAVORITE);

// Get a reference to the document you want to delete

        DocumentReference document = collection.document(
                FirebaseAuth.getInstance().getCurrentUser().getUid()+models.productId);

// Call the delete() method on the document reference to delete the document
        document.delete()
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

    // checking if user has granted permission for contact or not
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == callPermissionCode){
            if(grantResults.length>0&&grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.setData(Uri.parse("tel:" + phoneNumber.getText()));
                startActivity(dialIntent);

            }else{
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }



    public void get(String product_id){
        DocumentReference docRef = db.collection(constants.PRODUCT_KEY).document(product_id);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Document exists, retrieve the data
                    Map<String, Object> map = document.getData();
                     model = new AddRoomModels();
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
                } else {
                    Toast.makeText(getApplicationContext(),"Room is not available currently",Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(),"Room is not available currently",Toast.LENGTH_SHORT).show();
            }
        });

    }

}