package com.uteating.foodapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.uteating.foodapp.adapter.NotificationListAdapter;
import com.uteating.foodapp.databinding.FragmentNotificationBinding;
import com.uteating.foodapp.helper.FirebaseNotificationHelper;
import com.uteating.foodapp.model.Notification;

import java.util.List;


public class NotificationFragment extends Fragment {
    private FragmentNotificationBinding binding;
    private String userId;

    public NotificationFragment(String Id) {
        userId = Id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        readNotification();

        return view;
    }

    public void readNotification()
    {
        new FirebaseNotificationHelper(getContext()).readNotification(userId, new FirebaseNotificationHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Notification> notificationList, List<Notification> notificationListToNotify) {
                NotificationListAdapter adapter = new NotificationListAdapter(getContext(),notificationList,userId);
                binding.recNotification.setHasFixedSize(true);
                binding.recNotification.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.recNotification.setAdapter(adapter);
                binding.progressBarNotification.setVisibility(View.GONE);
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
    }
}