package com.uteating.foodapp.activity.Home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.adapter.Home.ResultSearchAdapter;
import com.uteating.foodapp.databinding.ActivityResultSearchBinding;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;

public class ResultSearchActivity extends AppCompatActivity {

    private ActivityResultSearchBinding binding;
    private final DatabaseReference productsReference= FirebaseDatabase.getInstance().getReference("Products");
    private ArrayList<Product> dsAll = new ArrayList<>();
    private ResultSearchAdapter adapter;
    private String userId;
    private String text;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.d("start", "yes");
        initData();
        initUI();
    }
    private void initUI() {
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        adapter = new ResultSearchAdapter(dsAll, userId,this);
        binding.recycleFoodFinded.setAdapter(adapter);

        binding.searhView.setIconifiedByDefault(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recycleFoodFinded.setLayoutManager(linearLayoutManager);
        binding.recycleFoodFinded.setHasFixedSize(true);
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //set Sự kiện của ô nhập
        binding.searhView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                adapter.getFilter().filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
    }

    private void initData() {
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        text = intent.getStringExtra("text");
        dsAll = new ArrayList<>();
        productsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item:snapshot.getChildren()) {
                    Product product = item.getValue(Product.class);
                    if (product != null && !product.getState().equals("deleted")) {
                        dsAll.add(product);
                    }
                }
                CharSequence charSequence = text;
                String key = charSequence.toString();
                ArrayList<Product> tmp = new ArrayList<>();
                key = key.toLowerCase();
                for (Product item : dsAll) {
                    if (item.getProductName().toLowerCase().contains(key)) {
                        tmp.add(item);
                    }
                }
                dsAll = tmp;
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}