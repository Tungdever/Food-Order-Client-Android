package com.uteating.foodapp.adapter.Home;

import android.app.Activity;
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

public class FindAdapter extends RecyclerView.Adapter<FindAdapter.ViewHolder> {

    private ArrayList<String> ds;
    private String userId;
    private Context mContext;
    SharedPreferences sharedPreferences;

    public FindAdapter(ArrayList<String> ds, String id, Context context) {
        this.mContext = context;
        this.ds = ds;
        this.userId = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemSearchBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = ds.get(position);
        if (item != null) {
            holder.binding.txtSearched.setText(item);
            holder.binding.txtSearched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ResultSearchActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("text", item);
                    // Cast mContext to Activity before calling startActivityForResult
                    if (mContext instanceof Activity) {
                        ((Activity) mContext).startActivityForResult(intent, 101);
                    } else {
                        Log.e("FindAdapter", "Context is not an instance of Activity");
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
        private final ItemSearchBinding binding;

        public ViewHolder(@NonNull ItemSearchBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
