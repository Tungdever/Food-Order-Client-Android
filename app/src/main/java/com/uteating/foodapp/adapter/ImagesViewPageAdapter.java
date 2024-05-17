package com.uteating.foodapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.uteating.foodapp.R;
import com.uteating.foodapp.databinding.ItemImagesBinding;
import com.uteating.foodapp.model.Images;

import java.util.List;

public class ImagesViewPageAdapter extends RecyclerView.Adapter<ImagesViewPageAdapter.ImagesViewHolder> {
    private List<Images> imagesList;

    public ImagesViewPageAdapter(List<Images> imagesList) {
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemImagesBinding binding = ItemImagesBinding.inflate(inflater, parent, false);
        View view = binding.getRoot();
        return new ImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesViewHolder holder, int position) {
        Images images = imagesList.get(position);
        if (images != null) {
            holder.imageView.setImageResource(images.getImagesId());
        }
    }

    @Override
    public int getItemCount() {
        return imagesList != null ? imagesList.size() : 0;
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize imageView here
            imageView = itemView.findViewById(R.id.imgView);
        }
    }
}
