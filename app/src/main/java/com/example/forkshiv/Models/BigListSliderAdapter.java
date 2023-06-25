package com.example.forkshiv.Models;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.forkshiv.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class BigListSliderAdapter extends SliderViewAdapter<BigListSliderAdapter.SliderAdapterViewHolder>{
    ArrayList<String> list;
    Context context;
    OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public BigListSliderAdapter(ArrayList<String> list, Context context,OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_home, null);
        return  new SliderAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position) {
        Glide.with(context).load(list.get(position)).into(viewHolder.imageView);
        viewHolder.bind(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageView;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sliderImage_home);
            this.itemView = itemView;
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
