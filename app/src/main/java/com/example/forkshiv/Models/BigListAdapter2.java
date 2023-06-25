package com.example.forkshiv.Models;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forkshiv.Constants;
import com.example.forkshiv.MyRoomsFragment;
import com.example.forkshiv.R;
import com.smarteist.autoimageslider.SliderView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.HashSet;


public class BigListAdapter2 extends RecyclerView.Adapter<BigListAdapter2.ViewHolder>{
    ArrayList<AddRoomModels> list;
    Context context;
    Constants constants = new Constants();
     OnItemClickListener listener;
     OnItemLongClickListener onItemLongClickListener;
    public interface OnItemClickListener {
        void onItemClick(CheckBox checkBox,int position);
    }
    public interface OnItemLongClickListener {
        void onItemLongClick(CheckBox checkBox,int position);
    }
    public BigListAdapter2(Context context, ArrayList<AddRoomModels> list, OnItemClickListener listener,
    OnItemLongClickListener onItemLongClickListener) {
        this.context = context;
        this.list=list;
        this.listener = listener;
        this.onItemLongClickListener = onItemLongClickListener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.big_list_sample_layout,parent,false);
        return new BigListAdapter2.ViewHolder(view);
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
        holder.bind(position,holder.checkBox);
        holder.click(position,holder.checkBox);
        //holder.cardView.setBackgroundResource(R.drawable.item_backgroud);
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
        public void bind(int position,CheckBox checkBox){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(checkBox,position);
                }
            });
        }

        public void click(int position,CheckBox checkBox){
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemLongClickListener.onItemLongClick(checkBox,position);
                    return true;
                }
            });
        }
    }
}
