package com.uteating.foodapp.fragment.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.uteating.foodapp.activity.order.OrderActivity;
import com.uteating.foodapp.adapter.orderAdapter.OrderAdapter;
import com.uteating.foodapp.databinding.FragmentHistoryOrderBinding;
import com.uteating.foodapp.model.Bill;

import java.util.ArrayList;

public class HistoryOrderFragment extends Fragment {
    private FragmentHistoryOrderBinding binding;
    private ArrayList<Bill> listBill;
    private String userId;

    public HistoryOrderFragment(ArrayList<Bill> ds, String id) {
        listBill = ds;
        userId = id;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHistoryOrderBinding.inflate(inflater,container,false);

        OrderAdapter adapter=new OrderAdapter(getContext(),listBill, OrderActivity.HISTORY_ORDER,userId);
        binding.ryc.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL,false));
        binding.ryc.setAdapter(adapter);

        return binding.getRoot();
    }
}
