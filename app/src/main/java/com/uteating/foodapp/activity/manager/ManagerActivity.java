package com.uteating.foodapp.activity.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.R;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.activity.Home.FindActivity;
import com.uteating.foodapp.activity.Home.ResultSearchActivity;
import com.uteating.foodapp.adapter.Home.FindAdapter;
import com.uteating.foodapp.adapter.manager.ManagerAdapter;
import com.uteating.foodapp.databinding.ActivityManagerBinding;
import com.uteating.foodapp.databinding.ActivityResultSearchBinding;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManagerActivity extends AppCompatActivity {
    ManagerAdapter adapter;
    private ActivityManagerBinding binding;
    private List<Product> ds;
    private String userId;
    APIService apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initUI();
    }
    private void initUI() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rc.setLayoutManager(linearLayoutManager);
        adapter = new ManagerAdapter(ds, userId, this);
        binding.rc.setAdapter(adapter);
        binding.rc.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initData() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        ds = new ArrayList<>();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> listPros = response.body();
                    ds.addAll(listPros);
                    adapter.notifyDataSetChanged();
                } else {

                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }
    
}