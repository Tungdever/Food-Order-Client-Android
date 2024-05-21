package com.uteating.foodapp.fragment.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.adapter.Home.FavouriteFoodAdapter;
import com.uteating.foodapp.databinding.FragmentFavoriteBinding;
import com.uteating.foodapp.helper.FirebaseFavouriteUserHelper;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {
    private FragmentFavoriteBinding binding;
    private String userId;
    private APIService productService;

    public FavoriteFragment(String id) {
        userId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Khởi tạo APIService từ RetrofitClient
        productService = RetrofitClient.getRetrofit().create(APIService.class);

        readFavouriteList();

        return view;
    }

    public void readFavouriteList() {
        // Sử dụng FirebaseFavouriteUserHelper để đọc danh sách sản phẩm yêu thích từ API
        new FirebaseFavouriteUserHelper(productService).readFavouriteList(userId, new FirebaseFavouriteUserHelper.DataStatus() {
            @Override
            public void DataIsLoaded(ArrayList<Product> favouriteProducts, ArrayList<String> keys) {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                binding.recFavouriteFood.setLayoutManager(layoutManager);
                binding.recFavouriteFood.setHasFixedSize(true);
                FavouriteFoodAdapter adapter = new FavouriteFoodAdapter(getContext(), favouriteProducts, userId);
                binding.recFavouriteFood.setAdapter(adapter);
                binding.progressBarFavouriteList.setVisibility(View.GONE);
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
    }
}
