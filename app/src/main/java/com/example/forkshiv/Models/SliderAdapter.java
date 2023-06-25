package com.example.forkshiv.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.forkshiv.R;
import com.smarteist.autoimageslider.SliderViewAdapter;


public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {


   public int[] mSliderItems;
   public Context context;
   OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    // Constructor
    public SliderAdapter(Context context, int[] sliderDataArrayList) {
        this.context = context;
        this.mSliderItems = sliderDataArrayList;
    }

    public SliderAdapter(Context context, int[] sliderDataArrayList,OnItemClickListener listener) {
        this.context = context;
        this.mSliderItems = sliderDataArrayList;
        this.listener = listener;
    }
//
//    // We are inflating the slider_layout
//    // inside on Create View Holder method.
//    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item_home, null);
        return new SliderAdapterViewHolder(inflate);
    }


    //
//    // Inside on bind view holder we will
//    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position) {
       viewHolder.imageView.setImageResource(mSliderItems[position]);
       viewHolder.bind(position);
    }
//
//    // this method will return
//    // the count of our list.
    @Override
    public int getCount() {
        return mSliderItems.length;
    }
//
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

    public void bind(int position) {
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position);
            }
        });
    }
}
}