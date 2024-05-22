package com.uteating.foodapp.adapter.Home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.R;
import com.uteating.foodapp.activity.ProductInformation.ProductInfoActivity;


import com.uteating.foodapp.databinding.ItemHomeFindLayoutBinding;
import com.uteating.foodapp.databinding.ItemProgressbarBinding;
import com.uteating.foodapp.model.Product;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResultSearchAdapter extends RecyclerView.Adapter {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private List<Product> ds;
    private String userId;
    private String userName;
    private Context mContext;

    public ResultSearchAdapter(List<Product> ds, String id, Context context) {
        this.mContext = context;
        this.ds = ds;
        this.userId = id;
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
            ItemHomeFindLayoutBinding binding = ItemHomeFindLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ResultSearchAdapter.ItemViewHolder(binding);
        } else {
            ItemProgressbarBinding binding = ItemProgressbarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ResultSearchAdapter.LoadingViewHolder(binding);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        private final ItemHomeFindLayoutBinding binding;

        public ItemViewHolder(@NonNull ItemHomeFindLayoutBinding binding) {
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ResultSearchAdapter.ItemViewHolder) {
            populateItemRows((ResultSearchAdapter.ItemViewHolder) holder, position);
        } else if (holder instanceof ResultSearchAdapter.LoadingViewHolder) {
            showLoadingView((ResultSearchAdapter.LoadingViewHolder) holder, position);
        }

    }

    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHomeFindLayoutBinding binding;

        public ViewHolder(@NonNull ItemHomeFindLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    private void showLoadingView(ResultSearchAdapter.LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
        viewHolder.binding.progressBar.setVisibility(View.VISIBLE);

    }

    private void populateItemRows(ResultSearchAdapter.ItemViewHolder viewHolder, int position) {
        Product item = ds.get(position);
        if (item != null) {
            Glide.with(viewHolder.binding.getRoot())
                    .load(item.getProductImage1())
                    .placeholder(R.drawable.image_default)
                    .into(viewHolder.binding.imgFood);

            viewHolder.binding.txtFoodName.setText(item.getProductName());
            double ratingStar = (double) Math.round(item.getRatingStar() * 10) / 10;
            viewHolder.binding.txtRating.setText(ratingStar + "/5.0");
            if (item.getRatingStar() >= 5) {
                viewHolder.binding.imgRate.setImageResource(R.drawable.rating_star_filled);
            } else if (item.getRatingStar() >= 3 && item.getRatingStar() < 5) {
                viewHolder.binding.imgRate.setImageResource(R.drawable.rating_star_half);
            } else {
                viewHolder.binding.imgRate.setImageResource(R.drawable.rating_star_empty);
            }
            viewHolder.binding.txtFoodPrice.setText(nf.format(item.getProductPrice()));
            viewHolder.binding.txtSold.setText("Đã bán: " + String.valueOf(item.getSold()));
            viewHolder.binding.parentOfItemInFindActivity.setOnClickListener(new View.OnClickListener() {
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
}
