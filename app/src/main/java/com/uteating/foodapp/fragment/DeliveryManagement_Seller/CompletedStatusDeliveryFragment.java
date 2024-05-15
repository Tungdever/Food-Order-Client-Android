package com.uteating.foodapp.fragment.DeliveryManagement_Seller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.uteating.foodapp.databinding.FragmentCompletedStatusDeliveryBinding;
import com.uteating.foodapp.databinding.FragmentConfirmStatusDeliveryBinding;

import java.util.List;

public class CompletedStatusDeliveryFragment extends Fragment {
    private FragmentCompletedStatusDeliveryBinding binding;
    private String userId;

    public CompletedStatusDeliveryFragment(String Id) {
        userId = Id;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCompletedStatusDeliveryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // pull data and set adapter for recycler view


        return view;
    }
}
