package com.uteating.foodapp.fragment.Home;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.adapter.Home.FoodDrinkFrgAdapter;
import com.uteating.foodapp.databinding.FragmentDrinkHomeFrgBinding;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DrinkHomeFrg extends Fragment {
    private List<Product> dsCurrentDrink;
    private List<Product> totalDrink;
    private FragmentDrinkHomeFrgBinding binding;
    private FoodDrinkFrgAdapter adapter;
    private String userId;
    private boolean isLoading = false;
    private int itemCount = 2;
    private boolean isScrolling = true;
    private String lastKey = null;
    private int position = 0;
    APIService apiService;

    public DrinkHomeFrg(String id) {
        userId = id;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDrinkHomeFrgBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initData();
        initUI();
        return view;
    }
    private void initUI() {
        Log.d("UI", "done");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        binding.rycDrinkHome.setLayoutManager(linearLayoutManager);
        adapter = new FoodDrinkFrgAdapter(dsCurrentDrink, userId, getContext());
        binding.rycDrinkHome.setAdapter(adapter);
        binding.rycDrinkHome.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.rycDrinkHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && isScrolling){
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dsCurrentDrink.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void initData() {
        Log.d("Data", "done");
        dsCurrentDrink = new ArrayList<>();
        apiService =  RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductsByType("drink").enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if(response.isSuccessful()) {
                    totalDrink = response.body();
                    Log.d("size", String.valueOf(totalDrink.size()));
                    int i = 0;
                    while (position < totalDrink.size() && i < itemCount) {
                        dsCurrentDrink.add(totalDrink.get(position));
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
        if (position < totalDrink.size()) {
            dsCurrentDrink.add(null);
            adapter.notifyItemInserted(dsCurrentDrink.size() - 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dsCurrentDrink.remove(dsCurrentDrink.size() - 1);
                    int i = 0;
                    while (position < totalDrink.size() && i < itemCount) {
                        dsCurrentDrink.add(totalDrink.get(position));
                        position++;
                        i++;
                    }
                    adapter.notifyDataSetChanged();
                    isLoading = false;
                }
            }, 2000);
        } else {
            isScrolling = false;
            adapter.notifyDataSetChanged();
        }
    }
}