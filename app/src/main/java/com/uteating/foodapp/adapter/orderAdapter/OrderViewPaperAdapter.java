package com.uteating.foodapp.adapter.orderAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.uteating.foodapp.fragment.order.CurrentOrderFragment;
import com.uteating.foodapp.fragment.order.HistoryOrderFragment;
import com.uteating.foodapp.model.Bill;

import java.util.ArrayList;

public class OrderViewPaperAdapter extends FragmentStateAdapter {
    private ArrayList<Bill> listCurrentOrder;
    private ArrayList <Bill> listHistoryOrder;
    private String userId;

    public OrderViewPaperAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Bill> listCurrentOrder,
                                 ArrayList<Bill> listHistoryOrder, String userId) {
        super(fragmentActivity);
        this.listCurrentOrder = listCurrentOrder;
        this.listHistoryOrder = listHistoryOrder;
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new HistoryOrderFragment(listHistoryOrder, userId);
        }
        return new CurrentOrderFragment(listCurrentOrder, userId);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
