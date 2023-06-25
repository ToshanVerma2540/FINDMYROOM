package com.example.forkshiv.Models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.forkshiv.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    Context context;
    ArrayList<CityModel> list;
    OnItemClickListener listener;
    public interface OnItemClickListener {
        void onItemClick(CityModel model);
    }
    public CityAdapter(Context context, ArrayList<CityModel> list,OnItemClickListener listener) {
        this.context = context;
        this.list=list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.city_sample,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CityModel model = list.get(position);
           holder.bind(list.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView cityImage;
        TextView cityName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cityImage = itemView.findViewById(R.id.cityImageView);
            cityName = itemView.findViewById(R.id.cityTextView);
        }
        public void bind(CityModel model,OnItemClickListener listener){
            cityImage.setImageResource(model.getImage());
            cityName.setText(model.getText());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(model);
                }
            });
        }
    }
}

