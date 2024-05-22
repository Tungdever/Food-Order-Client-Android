package com.uteating.foodapp.activity.feedback;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.uteating.foodapp.R;
import com.uteating.foodapp.adapter.FeedBackAdapter;
import com.uteating.foodapp.databinding.ActivityFeedBackBinding;
import com.uteating.foodapp.model.Bill;
import com.uteating.foodapp.model.BillInfo;

import java.util.ArrayList;

public class FeedBackActivity extends AppCompatActivity {
    private ActivityFeedBackBinding binding;
    private ArrayList<BillInfo> dsBillInfo;
    private Bill currentBill;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityFeedBackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent=getIntent();
        dsBillInfo= (ArrayList<BillInfo>) intent.getSerializableExtra("List of billInfo");
        currentBill= (Bill) intent.getSerializableExtra("Current Bill");
        userId = intent.getStringExtra("userId");
        initUI();
    }

    private void initUI() {
        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
        FeedBackAdapter adapter=new FeedBackAdapter(this,dsBillInfo,currentBill,userId);
        binding.ryc.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.ryc.setHasFixedSize(true);
        binding.ryc.setAdapter(adapter);
        //Set sự kiện cho nút back
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}