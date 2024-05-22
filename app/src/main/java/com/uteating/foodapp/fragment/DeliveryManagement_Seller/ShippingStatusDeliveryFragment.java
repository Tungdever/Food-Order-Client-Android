package com.uteating.foodapp.fragment.DeliveryManagement_Seller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.uteating.foodapp.adapter.DeliveryManagement_Seller.StatusOrderRecyclerViewAdapter;
import com.uteating.foodapp.databinding.FragmentShippingStatusDeliveryBinding;
import com.uteating.foodapp.helper.FirebaseStatusOrderHelper;
import com.uteating.foodapp.model.Bill;

import java.util.Collections;
import java.util.List;

public class ShippingStatusDeliveryFragment extends Fragment {
    private FragmentShippingStatusDeliveryBinding binding;
    private String userId;

    public ShippingStatusDeliveryFragment(String Id) {
        userId = Id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShippingStatusDeliveryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //set adapter and pull data for recycler view
        new FirebaseStatusOrderHelper(userId).readShippingBills(userId, new FirebaseStatusOrderHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Bill> bills, boolean isExistingBill) {
                Collections.reverse(bills);
                StatusOrderRecyclerViewAdapter adapter = new StatusOrderRecyclerViewAdapter(getContext(), bills);
                binding.recShippingDelivery.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recShippingDelivery.setHasFixedSize(true);
                binding.recShippingDelivery.setAdapter(adapter);
                binding.progressBarShippingDelivery.setVisibility(View.GONE);
                if (isExistingBill) {
                    binding.txtNoneItem.setVisibility(View.GONE);
                }
                else {
                    binding.txtNoneItem.setVisibility(View.VISIBLE);
                }
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
        return view;
    }
}
