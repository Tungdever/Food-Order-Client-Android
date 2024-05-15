package com.uteating.foodapp.activity.order;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.R;
import com.uteating.foodapp.adapter.orderAdapter.OrderViewPaperAdapter;
import com.uteating.foodapp.databinding.ActivityOrderBinding;
import com.uteating.foodapp.model.Bill;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private String userId;
    private ActivityOrderBinding binding;
    public static final int CURRENT_ORDER = 10001;
    public static final int HISTORY_ORDER = 10002;
    private ArrayList<Bill> dsCurrentOrder=new ArrayList<>();
    private ArrayList<Bill> dsHistoryOrder=new ArrayList<>();
    //private LoadingDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(Color.parseColor("#E8584D"));
        getWindow().setNavigationBarColor(Color.parseColor("#E8584D"));
// Ví dụ về cách tạo đối tượng Bill cứng và thêm vào danh sách dsCurrentOrder
        dsCurrentOrder.add(new Bill("address_id_1", "bill_id_1", "2024-05-13", "Completed", true, "recipient_id_1", "sender_id_1", 100000, "image_url_1"));
        dsCurrentOrder.add(new Bill("address_id_2", "bill_id_2", "2024-05-14", "Pending", false, "recipient_id_2", "sender_id_2", 150000, "image_url_2"));
        dsCurrentOrder.add(new Bill("address_id_3", "bill_id_3", "2024-05-15", "Completed", false, "recipient_id_3", "sender_id_3", 200000, "image_url_3"));

// Ví dụ về cách tạo đối tượng Bill cứng và thêm vào danh sách dsHistoryOrder
        dsHistoryOrder.add(new Bill("address_id_4", "bill_id_4", "2024-05-10", "Completed", true, "recipient_id_4", "sender_id_4", 120000, "image_url_4"));
        dsHistoryOrder.add(new Bill("address_id_5", "bill_id_5", "2024-05-11", "Completed", true, "recipient_id_5", "sender_id_5", 180000, "image_url_5"));
        dsHistoryOrder.add(new Bill("address_id_6", "bill_id_6", "2024-05-12", "Completed", true, "recipient_id_6", "sender_id_6", 220000, "image_url_6"));

        //userId = getIntent().getStringExtra("userId");
        //dialog = new LoadingDialog(this);
        //dialog.show();
        initUI();
        //initData();

        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUI() {
        OrderViewPaperAdapter viewPaperAdapter = new OrderViewPaperAdapter(OrderActivity.this, dsCurrentOrder, dsHistoryOrder, userId);
        binding.viewPaper2.setAdapter(viewPaperAdapter);
        binding.viewPaper2.setUserInputEnabled(false);
        new TabLayoutMediator(binding.tabLayout,binding.viewPaper2, ((tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Current Order");
                    break;
                case 1:
                    tab.setText("History Order");
                    break;
            }
        })).attach();
        //dialog.dismiss();
    }
    private void initData() {
        FirebaseDatabase.getInstance().getReference("Bills").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dsCurrentOrder.clear();
                dsHistoryOrder.clear();
                for (DataSnapshot item:snapshot.getChildren()) {
                    Bill tmp=item.getValue(Bill.class);
                    if (tmp.getRecipientId().equalsIgnoreCase(userId)) {

                        if (!tmp.getOrderStatus().equalsIgnoreCase("Completed")) {
                            dsCurrentOrder.add(tmp);
                        } else
                            dsHistoryOrder.add(tmp);
                    }
                }
                initUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}