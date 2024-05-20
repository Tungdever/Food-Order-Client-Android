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
import com.uteating.foodapp.activity.Home.ResultSearchActivity;
import com.uteating.foodapp.databinding.ItemManagerProductBinding;
import com.uteating.foodapp.databinding.ItemSearchBinding;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;
import java.util.List;


public class ManagerAdapter extends RecyclerView.Adapter{
    private List<Product> ds;
    private String userId;
    private Context mContext;

    public ManagerAdapter(List<Product> ds, String id,Context context) {
        this.mContext = context;
        this.ds=ds;
        this.userId = id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemManagerProductBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product item = ds.get(position);
        if (item != null) {
            ViewHolder viewHolder=(ViewHolder) holder;
            viewHolder.binding.txtFoodName.setText(item.getProductName());
            viewHolder.binding.txtUserId.setText("UserId: "+ userId);
            viewHolder.binding.parentOfItemInHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            viewHolder.binding.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        // Thực hiện hành động khi Switch được bật
                        Toast.makeText(viewHolder.binding.getRoot().getContext(), "Switch is ON", Toast.LENGTH_SHORT).show();
                    } else {
                        // Thực hiện hành động khi Switch bị tắt
                        Toast.makeText(viewHolder.binding.getRoot().getContext(), "Switch is OFF", Toast.LENGTH_SHORT).show();
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
