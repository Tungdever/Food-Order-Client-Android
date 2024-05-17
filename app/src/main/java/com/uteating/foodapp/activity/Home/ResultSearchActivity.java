package com.uteating.foodapp.activity.Home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.adapter.Home.ResultSearchAdapter;
import com.uteating.foodapp.databinding.ActivityResultSearchBinding;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultSearchActivity extends AppCompatActivity {
    private boolean isScrolling = true;
    private boolean isLoading = false;
    private ActivityResultSearchBinding binding;
    private final DatabaseReference productsReference= FirebaseDatabase.getInstance().getReference("Products");
    private List<Product> dsCurrent;
    private List<Product> dsAll = new ArrayList<>();
    private ResultSearchAdapter adapter;
    private String userId;
    private String text;
    Intent intent;
    APIService apiService;
    private int position = 0;
    private int itemCount = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initData();
        initUI();
    }
    private void initUI() {
        //Log.d("size", String.valueOf(dsAll.size()));
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        adapter = new ResultSearchAdapter(dsCurrent, userId,this);
        binding.recycleFoodFinded.setAdapter(adapter);

        binding.searhView.setIconifiedByDefault(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recycleFoodFinded.setLayoutManager(linearLayoutManager);
        binding.recycleFoodFinded.setHasFixedSize(true);
        binding.recycleFoodFinded.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && isScrolling){
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dsCurrent.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //set Sự kiện của ô nhập
    }

    private void initData() {
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        text = intent.getStringExtra("text");
        Log.d("text", text);
        dsCurrent = new ArrayList<>();
        dsAll = new ArrayList<>();
        apiService =  RetrofitClient.getRetrofit().create(APIService.class);
        apiService.searchProduct(text).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()) {
                    dsAll = response.body();
                    int i = 0;
                    while (position < dsAll.size() && i < itemCount) {
                        dsCurrent.add(dsAll.get(position));
                        position++;
                        i++;
                    }
                    adapter.notifyDataSetChanged();
                }
                else {

                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });
    }
    private void loadMore() {
        if (position < dsAll.size()) {
            dsCurrent.add(null);
            adapter.notifyItemInserted(dsCurrent.size() - 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dsCurrent.remove(dsCurrent.size() - 1);
                    int i = 0;
                    while (position < dsAll.size() && i < itemCount) {
                        dsCurrent.add(dsAll.get(position));
                        position++;
                        i++;
                    }
                    adapter.notifyDataSetChanged();
                    isLoading = false;
                }
            }, 1000);
        } else {
            isScrolling = false;
            adapter.notifyDataSetChanged();
        }
    }
}