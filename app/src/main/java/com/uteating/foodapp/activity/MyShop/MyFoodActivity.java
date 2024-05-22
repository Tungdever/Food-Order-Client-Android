package com.uteating.foodapp.activity.MyShop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.adapter.MyShopAdapter.MyShopAdapter;
import com.uteating.foodapp.databinding.ActivityMyFoodBinding;
import com.uteating.foodapp.dialog.LoadingDialog;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFoodActivity extends AppCompatActivity {
    private ActivityMyFoodBinding binding;
    private List<Product> ds = new ArrayList<>();
    private MyShopAdapter adapter;
    private String userId;
    APIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));

        userId = getIntent().getStringExtra("userId");
        adapter = new MyShopAdapter(ds, MyFoodActivity.this, userId);
        binding.recycleView.setHasFixedSize(true);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        binding.recycleView.setAdapter(adapter);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.flpAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyFoodActivity.this, AddFoodActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LoadingDialog dialog = new LoadingDialog(this);
        dialog.show();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductsPublisherId(userId).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                List<Product> lstProduct = response.body();
                if (response.isSuccessful()) {
                    ds.clear();
                    ds.addAll(lstProduct);
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                    Log.d("userid", userId);
                    Log.d("List food", String.valueOf(ds.size()));
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }
}