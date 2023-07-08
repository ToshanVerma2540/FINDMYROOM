package com.example.forkshiv;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.forkshiv.Models.CityAdapter;
import com.example.forkshiv.Models.CityModel;
import com.example.forkshiv.Models.PhotoAdapter;
import com.example.forkshiv.Models.SliderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    CityAdapter adapter;
    ArrayList<CityModel> al;
    SliderView roomForSlider;
    SliderView hostelForSlider;
    RecyclerView cityrv;
    Constants constants;
    static int count = 0;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    int [] roomForSliderImagesArray1 = {R.drawable.boys_room,R.drawable.girls_room,R.drawable.family_room};
    int [] roomForSliderImagesArray2 = {R.drawable.boys_room,R.drawable.girls_room};
    SearchView searchView;
    LinearLayout homeFragmentLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_home, container, false);
        searchView = v.findViewById(R.id.search_view);
        homeFragmentLayout = v.findViewById(R.id.home_fragment);
        mAuth = FirebaseAuth.getInstance();
         currentUser = mAuth.getCurrentUser();
        constants = new Constants();
        //implementing the search from searchview
        searchView.setIconified(false);
        searchView.clearFocus();
        // Hide the focus and keyboard

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                   intent.putExtra(constants.TEXT, query.trim().toLowerCase());
                     startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Update the search results based on the new query
                return true;
            }
        });

        //cities and implementing search according to cities
        al = new ArrayList<>();
        al.add(new CityModel(R.drawable.raipur,"Raipur"));
        al.add(new CityModel(R.drawable.delhi,"Delhi"));
        al.add(new CityModel(R.drawable.jaipur,"Jaipur"));
        al.add(new CityModel(R.drawable.banglore,"Banglore"));
        al.add(new CityModel(R.drawable.mumbai,"Mumbai"));
        al.add(new CityModel(R.drawable.hydrabad,"Hydrabad"));
        al.add(new CityModel(R.drawable.chennai,"Chennai"));
        al.add(new CityModel(R.drawable.kolkata,"Kolkata"));
        al.add(new CityModel(R.drawable.pune,"Pune"));
        cityrv = v.findViewById(R.id.rv);
        cityrv.setHasFixedSize(true);
        cityrv.setLayoutManager(new LinearLayoutManager(v.getContext(),LinearLayoutManager.HORIZONTAL, false));
        cityrv.setAdapter(new CityAdapter(getContext(), al, new CityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CityModel model) {
                Intent intent = new Intent(getActivity(),SearchActivity.class);
                intent.putExtra(constants.TEXT,model.getText());
                startActivity(intent);
            }
        }));


        //setting up both the sliders
        //adding clicklistener to both the sliders and implementing search according to it
        roomForSlider = v.findViewById(R.id.room_for_slider);
        SliderAdapter sliderAdapter = new SliderAdapter(getContext(), roomForSliderImagesArray1, new SliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),SearchActivity.class);
                intent.putExtra(constants.ROOM_FOR,position);
                startActivity(intent);
            }
        });
        roomForSlider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        roomForSlider.setSliderAdapter(sliderAdapter);
        roomForSlider.setScrollTimeInSec(3);
        roomForSlider.setAutoCycle(true);
        roomForSlider.startAutoCycle();
        hostelForSlider = v.findViewById(R.id.hostel_for_slider);
        SliderAdapter sliderAdapter2 = new SliderAdapter(getContext(), roomForSliderImagesArray2, new SliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),SearchActivity.class);
                intent.putExtra(constants.HOSTEL,position);
                startActivity(intent);
            }
        });
        hostelForSlider.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        hostelForSlider.setScrollTimeInSec(4);
        hostelForSlider.setClickable(true);
        hostelForSlider.setAutoCycle(true);
        hostelForSlider.startAutoCycle();
        hostelForSlider.bringToFront();
        hostelForSlider.setSliderAdapter(sliderAdapter2);

        return v;
    }

    MenuItem menuItem;
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu,menu);
         menuItem = menu.findItem(R.id.log_out);
         //setting the menu text respect to user is logged in or not
      if(mAuth.getCurrentUser() != null){
          menuItem.setTitle("Log Out");
      }else{
          menuItem.setTitle("Log In");
      }
        super.onCreateOptionsMenu(menu, inflater);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.favourite) {
            //if user is logged in go to favorite activity else login activity
            if(mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(getActivity(),FavoriteActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            return true;
        }
        if(item.getItemId() == R.id.log_out){
            //logging out
            if(mAuth.getCurrentUser() != null) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(),"You are logged out",Toast.LENGTH_SHORT).show();
                menuItem.setTitle("Log In");
            }else {
                //logging in
                Intent intent = new Intent(getActivity(), LoginActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        searchView.clearFocus();
        super.onResume();
    }
}