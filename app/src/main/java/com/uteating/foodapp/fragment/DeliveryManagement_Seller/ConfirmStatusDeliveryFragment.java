package com.uteating.foodapp.fragment.DeliveryManagement_Seller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.uteating.foodapp.databinding.FragmentConfirmStatusDeliveryBinding;

import java.util.List;

public class ConfirmStatusDeliveryFragment extends Fragment {
    private FragmentConfirmStatusDeliveryBinding binding;
    private String userId;

    public ConfirmStatusDeliveryFragment(String Id) {
        userId = Id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentConfirmStatusDeliveryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //set data and adapter for list

        // return statement
        return view;
    }
}
