package com.uteating.foodapp.adapter.Cart;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.Interface.IAdapterItemListener;
import com.uteating.foodapp.R;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.activity.ProductInformation.ProductInfoActivity;
import com.uteating.foodapp.custom.CustomMessageBox.CustomAlertDialog;
import com.uteating.foodapp.custom.CustomMessageBox.FailToast;
import com.uteating.foodapp.custom.CustomMessageBox.SuccessfulToast;
import com.uteating.foodapp.databinding.ItemCartProductBinding;
import com.uteating.foodapp.helper.FirebaseNotificationHelper;
import com.uteating.foodapp.helper.FirebaseProductInfoHelper;
import com.uteating.foodapp.helper.FirebaseUserInfoHelper;
import com.uteating.foodapp.model.Cart;
import com.uteating.foodapp.model.CartInfo;
import com.uteating.foodapp.model.CartProduct;
import com.uteating.foodapp.model.Notification;
import com.uteating.foodapp.model.Product;
import com.uteating.foodapp.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartProductAdapter extends RecyclerView.Adapter<CartProductAdapter.ViewHolder> {
    private Context mContext;
    private List<CartInfo> mCartInfos;
    private String cartId;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private int checkedItemCount = 0;
    private long checkedItemPrice = 0;
    private IAdapterItemListener adapterItemListener;
    private boolean isCheckAll;
    private String userId;
    private String userName;
    private ArrayList<CartInfo> selectedItems = new ArrayList<>();
    APIService apiService;

    public CartProductAdapter(Context mContext, List<CartInfo> mCartInfos, String cartId, boolean isCheckAll, String id) {
        this.mContext = mContext;
        this.mCartInfos = mCartInfos;
        this.cartId = cartId;
        this.isCheckAll = isCheckAll;
        this.userId = id;
        viewBinderHelper.setOpenOnlyOne(true);

        new FirebaseUserInfoHelper(mContext).readUserInfo(userId, new FirebaseUserInfoHelper.DataStatus() {
            @Override
            public void DataIsLoaded(User user) {
                userName = user.getUserName();
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemCartProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartInfo cartInfo = mCartInfos.get(position);

        viewBinderHelper.bind(holder.binding.swipeRevealLayout, cartInfo.getCartInfoId());

        holder.binding.checkBox.setChecked(isCheckAll);

        apiService = RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductCart(cartInfo.getProductId()).enqueue(new Callback<CartProduct>() {
            @Override
            public void onResponse(Call<CartProduct> call, Response<CartProduct> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartProduct cartProduct = response.body();
                    holder.binding.productName.setText(cartProduct.getProductName());
                    holder.binding.productPrice.setText(convertToMoney(cartProduct.getProductPrice()) + "đ");
                    Glide.with(mContext.getApplicationContext()).load(cartProduct.getProductImage1()).placeholder(R.mipmap.ic_launcher).into(holder.binding.productImage);
                    holder.binding.productAmount.setText("Count: " + cartInfo.getAmount());
                    holder.binding.productAmount.setText(String.valueOf(cartInfo.getAmount()));

                    // Set the remainAmount for this ViewHolder
                    holder.setRemainAmount(cartProduct.getRemainAmount());
                } else {
                    Log.e("Retrofit", "Response not successful: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<CartProduct> call, Throwable t) {
                Log.e("Retrofit", "API call failed: " + t.getMessage());
            }
        });

        isLiked(holder.binding.like, cartInfo.getProductId());

        holder.binding.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int amount = Integer.parseInt(holder.binding.productAmount.getText().toString());
                if (amount >= holder.remainAmount) {
                    new FailToast(mContext, "Can't add anymore!").showToast();
                } else {
                    // Change display value
                    amount++;
                    holder.binding.productAmount.setText(String.valueOf(amount));
                    holder.binding.checkBox.setChecked(false);
                    isCheckAll = false;

                    if (adapterItemListener != null) {
                        adapterItemListener.onCheckedItemCountChanged(0, 0, new ArrayList<>());
                        adapterItemListener.onAddClicked();
                    }

                    // Save to firebase
                    FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cartId).child(cartInfo.getCartInfoId()).child("amount").setValue(amount);
                }
            }
        });


        holder.binding.subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.binding.productAmount.getText().toString().equals("1")) {
                    // Change display value
                    int amount = Integer.parseInt(holder.binding.productAmount.getText().toString());
                    amount--;
                    holder.binding.productAmount.setText(String.valueOf(amount));
                    isCheckAll = false;

                    if (adapterItemListener != null) {
                        adapterItemListener.onCheckedItemCountChanged(0, 0, new ArrayList<>());
                        adapterItemListener.onSubtractClicked();
                    }

                    // Save to firebase
                    FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cartId).child(cartInfo.getCartInfoId()).child("amount").setValue(amount);

                    FirebaseDatabase.getInstance().getReference().child("Carts").child(cartId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
                else {
                    new FailToast(mContext, "Can't reduce anymore!").showToast();
                }
            }
        });

        holder.binding.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.binding.like.getTag().equals("like")) {
                    FirebaseDatabase.getInstance().getReference().child("Favorites").child(userId).child(cartInfo.getProductId()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                pushNotificationFavourite(cartInfo);
                                new SuccessfulToast(mContext, "Added to your favourite list").showToast();
                                holder.binding.like.setImageResource(R.drawable.ic_liked);
                                holder.binding.like.setTag("liked");
                            }
                        }
                    });
                } else if (holder.binding.like.getTag().equals("liked")) {
                    FirebaseDatabase.getInstance().getReference().child("Favorites").child(userId).child(cartInfo.getProductId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                new SuccessfulToast(mContext, "Removed from your favourite list").showToast();
                                holder.binding.like.setImageResource(R.drawable.ic_like);
                                holder.binding.like.setTag("like");
                            }
                        }
                    });
                }
            }
        });

        holder.binding.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomAlertDialog(mContext, "Delete this product?");
                CustomAlertDialog.binding.btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CustomAlertDialog.alertDialog.dismiss();

                        FirebaseDatabase.getInstance().getReference().child("CartInfos").child(cartId).child(cartInfo.getCartInfoId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    new SuccessfulToast(mContext, "Delete product successfully!").showToast();
                                    if (adapterItemListener != null) {
                                        adapterItemListener.onDeleteProduct();
                                    }
                                    else{

                                    }
                                }
                            }
                        });
                    }
                });
                CustomAlertDialog.binding.btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CustomAlertDialog.alertDialog.dismiss();
                    }
                });
                CustomAlertDialog.showAlertDialog();
            }
        });



        holder.binding.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                apiService =  RetrofitClient.getRetrofit().create(APIService.class);
                apiService.getProductCart(cartInfo.getProductId()).enqueue(new Callback<CartProduct>() {
                    @Override
                    public void onResponse(Call<CartProduct> call, Response<CartProduct> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            CartProduct cartProduct = response.body();
                            if (isChecked) {
                                checkedItemCount += cartInfo.getAmount();
                                checkedItemPrice += cartInfo.getAmount() * cartProduct.getProductPrice();
                                selectedItems.add(cartInfo);
                            }
                            else {
                                checkedItemCount -= cartInfo.getAmount();
                                checkedItemPrice -= cartInfo.getAmount() * cartProduct.getProductPrice();
                                selectedItems.removeIf(c -> (c.getCartInfoId().equals(cartInfo.getCartInfoId())));
                            }

                            if (adapterItemListener != null) {
                                adapterItemListener.onCheckedItemCountChanged(checkedItemCount, checkedItemPrice, selectedItems);
                            }
                        } else {

                            Log.e("Retrofit", "Response not successful: " + response.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<CartProduct> call, Throwable t) {

                        Log.e("Retrofit", "API call failed: " + t.getMessage());
                    }
                });

            }
        });

        holder.binding.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apiService.getProductInfor(cartInfo.getProductId()).enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if(response.body() != null){
                            Product product = response.body();
                            Intent intent = new Intent(mContext, ProductInfoActivity.class);
                            intent.putExtra("productId", product.getProductId());
                            intent.putExtra("productName", product.getProductName());
                            intent.putExtra("productPrice", product.getProductPrice());
                            intent.putExtra("productImage1", product.getProductImage1());
                            intent.putExtra("productImage2", product.getProductImage2());
                            intent.putExtra("productImage3", product.getProductImage3());
                            intent.putExtra("productImage4", product.getProductImage4());
                            intent.putExtra("ratingStar", product.getRatingStar());
                            intent.putExtra("productDescription", product.getDescription());
                            intent.putExtra("publisherId", product.getPublisherId());
                            intent.putExtra("sold", product.getSold());
                            intent.putExtra("productType", product.getProductType());
                            intent.putExtra("remainAmount", product.getRemainAmount());
                            intent.putExtra("ratingAmount", product.getRatingAmount());
                            intent.putExtra("state", product.getState());
                            intent.putExtra("userId", userId);
                            intent.putExtra("userName", userName);
                            mContext.startActivity(intent);
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {

                    }
                });

            }
        });
    }

    private void isLiked(ImageButton imageButton, String productId) {
        FirebaseDatabase.getInstance().getReference().child("Favorites").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(productId).exists()) {
                    imageButton.setImageResource(R.drawable.ic_liked);
                    imageButton.setTag("liked");
                } else {
                    imageButton.setImageResource(R.drawable.ic_like);
                    imageButton.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors
            }
        });
    }

    private String convertToMoney(long price) {
        String temp = String.valueOf(price);
        String output = "";
        int count = 3;
        for (int i = temp.length() - 1; i >= 0; i--) {
            count--;
            if (count == 0) {
                count = 3;
                output = "," + temp.charAt(i) + output;
            }
            else {
                output = temp.charAt(i) + output;
            }
        }

        if (output.charAt(0) == ',')
            return output.substring(1);

        return output;

    }

    @Override
    public int getItemCount() {
        return mCartInfos == null ? 0 : mCartInfos.size();
    }

    public void saveStates(Bundle outState) {
        viewBinderHelper.saveStates(outState);
    }

    public void setAdapterItemListener(IAdapterItemListener adapterItemListener) {
        this.adapterItemListener = adapterItemListener;
    }

    public void restoreStates(Bundle instate) {
        viewBinderHelper.restoreStates(instate);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartProductBinding binding;
        private int remainAmount; // Local variable to hold remainAmount

        public ViewHolder(ItemCartProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setRemainAmount(int remainAmount) {
            this.remainAmount = remainAmount;
        }
    }

    public void pushNotificationFavourite(CartInfo cartInfo)
    {
        new FirebaseProductInfoHelper(cartInfo.getProductId()).readInformationById(new FirebaseProductInfoHelper.DataStatusInformationOfProduct() {
            @Override
            public void DataIsLoaded(Product product) {
                String title = "Favourite product";
                String content = userName + " liked your product: "+ product.getProductName() + ". Go to Product Information to check it.";
                Notification notification = FirebaseNotificationHelper.createNotification(title,content,product.getProductImage1(),product.getProductId(),"None","None", null);
                new FirebaseNotificationHelper(mContext).addNotification(product.getPublisherId(), notification, new FirebaseNotificationHelper.DataStatus() {
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
