package com.example.forkshiv.Models;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forkshiv.Constants;
import com.example.forkshiv.MyRoomsFragment;
import com.example.forkshiv.R;
import com.smarteist.autoimageslider.SliderView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;


public class BigListAdapter extends RecyclerView.Adapter<BigListAdapter.ViewHolder>{
    ArrayList<AddRoomModels> list;
    Context context;
    Constants constants = new Constants();
     OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public BigListAdapter(Context context, ArrayList<AddRoomModels> list,OnItemClickListener listener) {
        this.context = context;
        this.list=list;
        this.listener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.big_list_sample_layout,parent,false);
        return new BigListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AddRoomModels model = list.get(position);
        BigListSliderAdapter sliderAdapter2 = new BigListSliderAdapter(model.images, context,
                new BigListSliderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }
        });
        holder.sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        holder.sliderView.setSliderAdapter(sliderAdapter2);
        holder.sliderView.setScrollTimeInSec(3);
        holder.sliderView.setAutoCycle(true);
        holder.sliderView.startAutoCycle();

        holder.cityAreaTv.setText(model.city);
        holder.typeTennantTv.setText(constants.roomType[(int)model.roomType+1]+" - "+ constants.prefferedTennants[(int)model.prefferdTennats+1]);
        holder.expectedRentTv.setText(model.expectedRent);
        holder.localityTv.setText(model.locality);
        holder.bind(position);
        holder.checkBox.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        SliderView sliderView;
        TextView typeTennantTv;
        TextView localityTv;
        TextView cityAreaTv;
        TextView expectedRentTv;
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sliderView = itemView.findViewById(R.id.slider_view);
            typeTennantTv = itemView.findViewById((R.id.type_tennant_tv));
            localityTv = itemView.findViewById((R.id.locality_tv));
            cityAreaTv = itemView.findViewById((R.id.city_area_tv));
            expectedRentTv = itemView.findViewById((R.id.rent_tv));
            checkBox = itemView.findViewById(R.id.checkbox);
        }
        public void bind(int position){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(position);
                }
            });
        }
    }


}
