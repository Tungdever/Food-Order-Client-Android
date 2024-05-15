package com.uteating.foodapp.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;



public class ViewPager2Adapter extends FragmentStateAdapter {
    private String userId;
    public ViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, String userId) {
        super(fragmentManager, lifecycle);
        this.userId = userId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new DrinkHomeFrg(userId);
        }
        return new FoodHomeFrg(userId);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
