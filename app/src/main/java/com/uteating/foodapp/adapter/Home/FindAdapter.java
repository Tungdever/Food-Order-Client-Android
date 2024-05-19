package com.uteating.foodapp.adapter.Home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.uteating.foodapp.activity.Home.ResultSearchActivity;
import com.uteating.foodapp.databinding.ItemSearchBinding;

import java.util.ArrayList;

public class FindAdapter extends RecyclerView.Adapter{

    private ArrayList<String> ds;

    private String userId;
    private Context mContext;
    SharedPreferences sharedPreferences;
    private ArrayList<String> history_search = new ArrayList<>();

    public FindAdapter(ArrayList<String> ds, String id,Context context) {
        sharedPreferences = context.getSharedPreferences("history_search", context.MODE_PRIVATE);
        history_search.add(sharedPreferences.getString("1st", ""));
        history_search.add(sharedPreferences.getString("2nd", ""));
        history_search.add(sharedPreferences.getString("3rd", ""));
        this.mContext = context;
        this.ds=ds;
        this.userId = id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String item = ds.get(position);
        if (item != null) {
            ViewHolder viewHolder=(ViewHolder) holder;
            viewHolder.binding.txtSearched.setText(item);
            viewHolder.binding.txtSearched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("size", String.valueOf(history_search.size()));
                    Intent intent = new Intent(mContext, ResultSearchActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("text", item);
                    intent.putStringArrayListExtra("search",history_search);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemSearchBinding binding;

        public ViewHolder(@NonNull ItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
