package com.uteating.foodapp.adapter.manager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.activity.Home.ResultSearchActivity;
import com.uteating.foodapp.databinding.ItemManagerProductBinding;
import com.uteating.foodapp.databinding.ItemSearchBinding;
import com.uteating.foodapp.model.Product;
import com.uteating.foodapp.model.User;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ManagerAdapter extends RecyclerView.Adapter {
    private List<Product> ds;
    private String userId;
    private Context mContext;


    public ManagerAdapter(List<Product> ds, String id, Context context) {
        this.mContext = context;
        this.ds = ds;
        this.userId = id;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemManagerProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product item = ds.get(position);
        if (item != null) {
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.binding.txtFoodName.setText(item.getProductName());
            viewHolder.binding.txtUserId.setText("UserId: " + userId);
            Glide.with(viewHolder.binding.getRoot())
                    .load(item.getProductImage1())
                    .into(viewHolder.binding.imgFood);
            viewHolder.binding.sw.setChecked(item.isChecked());

            viewHolder.binding.parentOfItemInHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            viewHolder.binding.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    APIService apiService = RetrofitClient.getRetrofit().create(APIService.class);
                    apiService.checkProduct(userId,item.getProductId()).enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            if (response.isSuccessful()){
                                Log.d("State", "success");
                            }
                            else {
                                Log.d("State", "fail");
                            }
                        }
                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {
                        }
                    });
                    if (isChecked) {
                        Toast.makeText(viewHolder.binding.getRoot().getContext(), "Đã kích hoạt sản phẩm này", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(viewHolder.binding.getRoot().getContext(), "Đã tắt sản phẩm này", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemManagerProductBinding binding;

        public ViewHolder(@NonNull ItemManagerProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
