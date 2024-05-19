package com.uteating.foodapp.fragment.Home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.tabs.TabLayout;
import com.uteating.foodapp.R;
import com.uteating.foodapp.activity.Home.FindActivity;
import com.uteating.foodapp.adapter.ImagesViewPageAdapter;
import com.uteating.foodapp.adapter.ViewPager2Adapter;
import com.uteating.foodapp.databinding.FragmentHomeBinding;
import com.uteating.foodapp.model.DepthPageTransformer;
import com.uteating.foodapp.model.Images;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private String userId;
    private Handler handler = new Handler();
    private List<Images> imagesList;
    private ViewPager2Adapter viewPager2Adapter;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (binding.viewPager2.getCurrentItem() == imagesList.size() - 1) {
                binding.viewPager2.setCurrentItem(0);
            } else {
                binding.viewPager2.setCurrentItem(binding.viewPager2.getCurrentItem() + 1);
            }
        }
    };

    public HomeFragment(String id) {
        userId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        initUI();
        return binding.getRoot();
    }

    private void initUI() {
        binding.layoutSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), FindActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });
        //Tạo Tab
        binding.tabHome.addTab(binding.tabHome.newTab().setText("Food"));
        binding.tabHome.addTab(binding.tabHome.newTab().setText("Drink"));
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        viewPager2Adapter = new ViewPager2Adapter(fragmentManager, getLifecycle(), userId);
        binding.viewpaperHome.setAdapter(viewPager2Adapter);
        binding.tabHome.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewpaperHome.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        binding.viewpaperHome.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabHome.selectTab(binding.tabHome.getTabAt(position));
            }
        });
        //
        imagesList = getListImages();
        ImagesViewPageAdapter adapter = new ImagesViewPageAdapter(imagesList);
        binding.viewPager2.setAdapter(adapter);
        //liên kết viewpager và indicator
        binding.circleIndicator3.setViewPager(binding.viewPager2);

        binding.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                handler.postDelayed(runnable, 3000);
            }
        });
        binding.viewPager2.setPageTransformer(new DepthPageTransformer());

    }
    private List<Images> getListImages() {
        List<Images> list = new ArrayList<>();
        list.add(new Images(R.drawable.bg1));
        list.add(new Images(R.drawable.bg2));
        list.add(new Images(R.drawable.bg3));
        list.add(new Images(R.drawable.bg4));
        return list;
    }

}