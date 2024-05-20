package com.uteating.foodapp.activity.Home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.R;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.activity.MyShop.MyShopActivity;
import com.uteating.foodapp.activity.order.OrderActivity;
import com.uteating.foodapp.custom.SuccessfulToast;
import com.uteating.foodapp.databinding.ActivityProfileBinding;
import com.uteating.foodapp.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private String userId;
    private APIService apiService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        initToolbar();

        getUserInfo(ProfileActivity.this);

        binding.cardViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ProfileActivity.this, OrderActivity.class);
                intent1.putExtra("userId",userId);
                startActivity(intent1);
            }
        });

        binding.cardViewMyShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(ProfileActivity.this, MyShopActivity.class);
                intent2.putExtra("userId",userId);
                startActivity(intent2);
            }
        });

        binding.change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                intent.putExtra("userId",userId);
                intent.putExtra("username", user.getUserName());
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void getUserInfo(Context mContext) {
        apiService =  RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getUserByUserId(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    user = response.body();
                    binding.userName.setText(user.getUserName());
                    binding.userEmail.setText(user.getEmail());
                    binding.userPhoneNumber.setText(user.getPhoneNumber());
                    Glide.with(mContext.getApplicationContext())
                            .load(user.getAvatarURL())
                            .placeholder(R.drawable.default_avatar)
                            .into(binding.userAvatar);
                } else {
                    // Handle the case where the response is not successful
                    Log.d("getUserInfo", response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("getUserInfoonFailure", t.getMessage());
            }
        });
    }
}