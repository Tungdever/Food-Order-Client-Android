package com.uteating.foodapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.R;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.activity.Home.ChatDetailActivity;
import com.uteating.foodapp.activity.ProductInformation.ProductInfoActivity;
import com.uteating.foodapp.activity.order.OrderDetailActivity;
import com.uteating.foodapp.activity.orderSellerManagement.DeliveryManagementActivity;
import com.uteating.foodapp.databinding.ItemNotificationBinding;
import com.uteating.foodapp.helper.FirebaseNotificationHelper;
import com.uteating.foodapp.helper.FirebaseProductInfoHelper;
import com.uteating.foodapp.model.Bill;
import com.uteating.foodapp.model.Notification;
import com.uteating.foodapp.model.Product;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.ViewHolder> {
    private Context mContext;
    private List<Notification> notificationList;
    private String userId;

    private APIService apiService;

    public NotificationListAdapter(Context mContext, List<Notification> notificationList,String id) {
        this.mContext = mContext;
        this.notificationList = notificationList;
        userId = id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(mContext), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.binding.txtTitleNotification.setText(notification.getTitle());
        holder.binding.txtContentNotification.setText(notification.getContent());
        holder.binding.txtTimeNotification.setText(notification.getTime());
        if (notification.getImageURL().isEmpty())
        {
            holder.binding.imgNotification.setImageResource(R.drawable.ic_launcher_background);
        }
        else {
            holder.binding.imgNotification.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(mContext)
                    .asBitmap()
                    .load(notificationList.get(position).getImageURL())
                    .into(holder.binding.imgNotification);
        }
        if (!notification.isRead())
        {
            holder.binding.dotStatusRead.setVisibility(View.VISIBLE);
            holder.binding.backgroundNotificationItem.setBackgroundColor(Color.parseColor("#e3e3e3"));
        }
        else {
            holder.binding.dotStatusRead.setVisibility(View.GONE);
            holder.binding.backgroundNotificationItem.setBackgroundColor(Color.TRANSPARENT);
        }
        // trigger for navigate to another activity
        holder.binding.backgroundNotificationItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo write code to navigate to the activity refer to notification
                if (!notification.isRead()) {
                    notification.setRead(true);
                    new FirebaseNotificationHelper(mContext).updateNotification(userId, notification, new FirebaseNotificationHelper.DataStatus() {
                        @Override
                        public void DataIsLoaded(List<Notification> notificationList, List<Notification> notificationListToNotify) {

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

                if (!notification.getBillId().equals("None"))
                {
                    Bill bill = new Bill();
                    bill.setBillId(notification.getBillId());
                    Intent intent=new Intent(mContext, OrderDetailActivity.class);
                    intent.putExtra("Bill", bill);
                    intent.putExtra("userId",userId);
                    mContext.startActivity(intent);
                }
                else if (!notification.getProductId().equals("None"))
                {
                    final String[] userName = new String[1];
                    FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            userName[0] = snapshot.child("userName").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    apiService = RetrofitClient.getRetrofit().create(APIService.class);
                    apiService.getProductInfor(notification.getProductId()).enqueue(new Callback<Product>() {
                        @Override
                        public void onResponse(Call<Product> call, Response<Product> response) {
                            if (response.isSuccessful()) {
                                Product item = response.body();
                                Intent intent = new Intent(mContext, ProductInfoActivity.class);
                                intent.putExtra("productId", item.getProductId());
                                intent.putExtra("productName", item.getProductName());
                                intent.putExtra("productPrice", item.getProductPrice());
                                intent.putExtra("productImage1", item.getProductImage1());
                                intent.putExtra("productImage2", item.getProductImage2());
                                intent.putExtra("productImage3", item.getProductImage3());
                                intent.putExtra("productImage4", item.getProductImage4());
                                intent.putExtra("ratingStar", item.getRatingStar());
                                intent.putExtra("productDescription", item.getDescription());
                                intent.putExtra("publisherId", item.getPublisherId());
                                intent.putExtra("sold", item.getSold());
                                intent.putExtra("productType", item.getProductType());
                                intent.putExtra("remainAmount", item.getRemainAmount());
                                intent.putExtra("ratingAmount", item.getRatingAmount());
                                intent.putExtra("state", item.getState());
                                intent.putExtra("userId", userId);
                                intent.putExtra("userName", userName);
                                mContext.startActivity(intent);
                            } else {
                                Log.d("ProductNoti", "unsuccessful");
                            }
                        }

                        @Override
                        public void onFailure(Call<Product> call, Throwable t) {
                            Log.d("ProductNotiFailure", t.getMessage());
                        }
                    });
                }
                else if (!notification.getConfirmId().equals("None"))
                {
                    Intent intent=new Intent(mContext, DeliveryManagementActivity.class);
                    intent.putExtra("userId",userId);
                    mContext.startActivity(intent);
                }
                else if (notification.getPublisher() != null) {
                    Intent intent = new Intent(mContext, ChatDetailActivity.class);
                    intent.setAction("chatActivity");
                    intent.putExtra("publisher", notification.getPublisher());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList == null ? 0 : notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final ItemNotificationBinding binding;

        public ViewHolder(@NonNull ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
