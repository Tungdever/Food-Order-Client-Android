package com.uteating.foodapp.fragment.Home;

import android.os.Bundle;
import android.os.Handler;
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
import com.uteating.foodapp.adapter.Home.FoodDrinkFrgAdapter;
import com.uteating.foodapp.databinding.FragmentFoodHomeFrgBinding;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;


public class FoodHomeFrg extends Fragment {
    private FragmentFoodHomeFrgBinding binding;
    private ArrayList<Product> dsCurrentFood;
    private ArrayList<Product> totalFood;
    private FoodDrinkFrgAdapter adapter;
    private String userId;
    private boolean isLoading = false;
    private int itemCount = 2;
    private boolean isScrolling = true;
    private String lastKey = null;

    private int position = 0;

    public FoodHomeFrg(String id) {
        userId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFoodHomeFrgBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        initData();
        initUI();
        return view;
    }

    private void initUI() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        binding.rycFoodHome.setLayoutManager(linearLayoutManager);
        adapter = new FoodDrinkFrgAdapter(dsCurrentFood, userId, getContext());
        binding.rycFoodHome.setAdapter(adapter);
        binding.rycFoodHome.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.rycFoodHome.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && isScrolling) {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == dsCurrentFood.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void initData() {
        dsCurrentFood = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalFood = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Product product = ds.getValue(Product.class);
                    if (product != null && !product.getState().equals("deleted")
                            && product.getProductType().equalsIgnoreCase("Food")
                            && !product.getPublisherId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                        totalFood.add(product);
                    }
                }
                int i = 0;
                while (position < totalFood.size() && i < itemCount) {
                    dsCurrentFood.add(totalFood.get(position));
                    position++;
                    i++;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadMore() {
        if (position < totalFood.size()) {
            dsCurrentFood.add(null);
            adapter.notifyItemInserted(dsCurrentFood.size() - 1);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dsCurrentFood.remove(dsCurrentFood.size() - 1);
                    int i = 0;
                    while (position < totalFood.size() && i < itemCount) {
                        dsCurrentFood.add(totalFood.get(position));
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