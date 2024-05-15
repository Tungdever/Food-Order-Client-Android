package com.uteating.foodapp.adapter.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.activity.ProductInfoActivity;
import com.uteating.foodapp.databinding.ItemHomeBinding;
import com.uteating.foodapp.databinding.ItemProgressbarBinding;
import com.uteating.foodapp.model.Product;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FoodDrinkFrgAdapter extends RecyclerView.Adapter {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final ArrayList<Product> ds;
    private final String userId;
    private String userName;
    private final Context mContext;

    public FoodDrinkFrgAdapter(ArrayList<Product> ds, String id, Context context)
    {
        mContext = context;
        this.ds = ds;
        userId = id;

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            ItemHomeBinding binding = ItemHomeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ItemViewHolder(binding);
        } else {
            ItemProgressbarBinding binding = ItemProgressbarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new LoadingViewHolder(binding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) holder, position);
        } else if (holder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) holder, position);
        }
    }
    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }
    @Override
    public int getItemViewType(int position) {
        return ds.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemHomeBinding binding;

        public ItemViewHolder(@NonNull ItemHomeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        private final ItemProgressbarBinding binding;
        public LoadingViewHolder(@NonNull ItemProgressbarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
        viewHolder.binding.progressBar.setVisibility(View.VISIBLE);

    }

    private void populateItemRows(ItemViewHolder viewHolder, int position) {
        Product item = ds.get(position);
        Glide.with(viewHolder.binding.getRoot())
                .load(item.getProductImage1())
                .into(viewHolder.binding.imgFood);
        viewHolder.binding.txtFoodName.setText(item.getProductName());
        viewHolder.binding.txtSold.setText("Đã bán: " + String.valueOf(item.getSold()));
        viewHolder.binding.txtRating.setText(String.valueOf(item.getRatingStar()));
        viewHolder.binding.parentOfItemInHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ProductInfoActivity.class);
                intent.putExtra("productId", item.getProductId());
                intent.putExtra("productName", item.getProductName());
                intent.putExtra("productPrice", item.getProductPrice());
                intent.putExtra("productImage1", item.getProductImage1());
                intent.putExtra("productImage2", item.getProductImage2());
                intent.putExtra("productImage3", item.getProductImage3());
                intent.putExtra("productImage4", item.getProductImage4());
                intent.putExtra("ratingStar", item.getRatingStar());
                intent.putExtra("productDescription", item.getDescription());
                intent.putExtra("publisherId", item.getPublisherId());
                intent.putExtra("sold", item.getSold());
                intent.putExtra("productType", item.getProductType());
                intent.putExtra("remainAmount", item.getRemainAmount());
                intent.putExtra("ratingAmount", item.getRatingAmount());
                intent.putExtra("state", item.getState());
                intent.putExtra("userId", userId);
                intent.putExtra("userName", userName);
                mContext.startActivity(intent);
            }
        });
    }
}
