package com.uteating.foodapp.activity.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    private final DatabaseReference productsReference = FirebaseDatabase.getInstance().getReference("Products");
    private List<Product> dsCurrent;
    private List<Product> dsAll = new ArrayList<>();
    private ArrayList<String> history_search = new ArrayList<>();
    ;
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
        adapter = new ResultSearchAdapter(dsCurrent, userId, this);
        binding.recycleFoodFinded.setAdapter(adapter);

        binding.searhView.setIconifiedByDefault(false);
        binding.searhView.setQuery(text, false);
        binding.searhView.clearFocus();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recycleFoodFinded.setLayoutManager(linearLayoutManager);
        binding.recycleFoodFinded.setHasFixedSize(true);
        binding.recycleFoodFinded.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && isScrolling) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dsCurrent.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
        int searchCloseButtonId = binding.searhView.findViewById(androidx.appcompat.R.id.search_close_btn).getId();
        ImageView closeButton = binding.searhView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.searhView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                SharedPreferences sharedPreferences = getSharedPreferences("history_search", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!s.equals(history_search.get(0))) {
                    history_search.add(0, s); // Add to the start
                    if (history_search.size() > 3) {
                        history_search.remove(3); // Remove the oldest item if the list exceeds 3
                    }
                }
                editor.clear();
                if (history_search.size() > 2) {
                    editor.putString("3rd", history_search.get(2));
                }
                if (history_search.size() > 1) {
                    editor.putString("2nd", history_search.get(1));
                }
                if (history_search.size() > 0) {
                    editor.putString("1st", history_search.get(0));
                }
                editor.commit();
                apiService.searchProduct(s).enqueue(new Callback<List<Product>>() {
                    @Override
                    public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                        if (response.isSuccessful()) {
                            Log.d("state", "OK");
                            dsAll.clear();
                            dsCurrent.clear();
                            dsAll = response.body();
                            int i = 0;
                            position = 0;
                            while (position < dsAll.size() && i < itemCount) {
                                dsCurrent.add(dsAll.get(position));
                                position++;
                                i++;
                            }
                            adapter.notifyDataSetChanged();
                        } else {

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Product>> call, Throwable t) {

                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    private void initData() {
        intent = getIntent();
        userId = intent.getStringExtra("userId");
        text = intent.getStringExtra("text");
        history_search = (ArrayList<String>) intent.getStringArrayListExtra("search");

        Log.d("text", text);
        dsCurrent = new ArrayList<>();
        dsAll = new ArrayList<>();
        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.searchProduct(text).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    dsAll = response.body();
                    int i = 0;
                    while (position < dsAll.size() && i < itemCount) {
                        dsCurrent.add(dsAll.get(position));
                        position++;
                        i++;
                    }
                    adapter.notifyDataSetChanged();
                } else {

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