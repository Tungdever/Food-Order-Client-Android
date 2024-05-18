package com.uteating.foodapp.adapter.Cart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
import com.uteating.foodapp.databinding.ItemOrderProductBinding;
import com.uteating.foodapp.model.CartInfo;
import com.uteating.foodapp.model.CartProduct;
import com.uteating.foodapp.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.ViewHolder>{
    private Context mContext;
    private List<CartInfo> mCartInfos;
    APIService apiService;
    public OrderProductAdapter(Context mContext, List<CartInfo> mCartInfos) {
        this.mContext = mContext;
        this.mCartInfos = mCartInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemOrderProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartInfo cartInfo = mCartInfos.get(position);
//
//        FirebaseDatabase.getInstance().getReference().child("Products").child(cartInfo.getProductId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Product product = snapshot.getValue(Product.class);
//                holder.binding.orderProductName.setText(product.getProductName());
//                holder.binding.orderProductPrice.setText(convertToMoney(product.getProductPrice())+"đ");
//                Glide.with(mContext.getApplicationContext()).load(product.getProductImage1()).placeholder(R.mipmap.ic_launcher).into(holder.binding.orderProductImage);
//                holder.binding.amount.setText(String.valueOf("Count: "+ cartInfo.getAmount()));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        apiService =  RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductCart(cartInfo.getProductId()).enqueue(new Callback<CartProduct>() {
            @Override
            public void onResponse(Call<CartProduct> call, Response<CartProduct> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CartProduct cartProduct = response.body();
                    holder.binding.orderProductName.setText(cartProduct.getProductName());
                    holder.binding.orderProductPrice.setText(convertToMoney(cartProduct.getProductPrice()) + "đ");
                    Glide.with(mContext.getApplicationContext()).load(cartProduct.getProductImage1()).placeholder(R.mipmap.ic_launcher).into(holder.binding.orderProductImage);
                    holder.binding.amount.setText(String.valueOf("Count: " + cartInfo.getAmount()));
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

    @Override
    public int getItemCount() {
        return mCartInfos == null ? 0 : mCartInfos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemOrderProductBinding binding;

        public ViewHolder(ItemOrderProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
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
}
