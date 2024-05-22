package com.uteating.foodapp.fragment.DeliveryManagement_Seller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.uteating.foodapp.adapter.DeliveryManagement_Seller.StatusOrderRecyclerViewAdapter;
import com.uteating.foodapp.databinding.FragmentConfirmStatusDeliveryBinding;
import com.uteating.foodapp.helper.FirebaseStatusOrderHelper;
import com.uteating.foodapp.model.Bill;

import java.util.Collections;
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
        new FirebaseStatusOrderHelper(userId).readConfirmBills(userId, new FirebaseStatusOrderHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Bill> bills, boolean isExistingBill) {
                Collections.reverse(bills);
                StatusOrderRecyclerViewAdapter adapter = new StatusOrderRecyclerViewAdapter(getContext(), bills);
                binding.recConfirmDelivery.setHasFixedSize(true);
                binding.recConfirmDelivery.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recConfirmDelivery.setAdapter(adapter);
                binding.progressBarConfirmDelivery.setVisibility(View.GONE);
                if (isExistingBill) {
                    binding.txtNoneItem.setVisibility(View.GONE);
                }
                else {
                    binding.txtNoneItem.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void DataIsInserted() {}

            @Override
            public void DataIsUpdated() {}

            @Override
            public void DataIsDeleted() {}
        });


        // return statement
        return view;
    }
}
