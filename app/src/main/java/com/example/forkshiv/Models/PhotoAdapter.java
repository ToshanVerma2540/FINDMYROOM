package com.example.forkshiv.Models;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.forkshiv.R;
import com.example.forkshiv.UploadImagesFragment;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.FragmentViewHolder>{
    List<Uri> imageUri;
    Context context;
    public PhotoAdapter(Context context,List<Uri> uri){
        this.context = context;
        this.imageUri = uri;
    }
    @Override
    public FragmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate( R.layout.photos_layout, parent,false);
        return new FragmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FragmentViewHolder holder, int position) {
        //holder.getImageView().setImageURI(imageUri.get(position));
        Glide.with(context).load(imageUri.get(position)).into(holder.getImageView());
    }


    @Override
    public int getItemCount() {
        return imageUri.size();
    }
    public class FragmentViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder{
        private ImageView imageView;
        public FragmentViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sample_imageView);
        }
        public ImageView getImageView(){
            return imageView;
        }
    }
}

