package com.uteating.foodapp.adapter.orderAdapter;

import android.content.Context;
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
import com.uteating.foodapp.databinding.ItemBillinfoBinding;
import com.uteating.foodapp.model.BillInfo;
import com.uteating.foodapp.model.CurrencyFormatter;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailAdapter  extends RecyclerView.Adapter{
    Context context;
    ArrayList<BillInfo> ds;
    APIService apiService;

    public OrderDetailAdapter(Context context, ArrayList<BillInfo> ds) {
        this.context = context;
        this.ds = ds;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemBillinfoBinding.inflate(LayoutInflater.from(context),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        BillInfo billInfo = ds.get(position);
        apiService =  RetrofitClient.getRetrofit().create(APIService.class);
        apiService.getProductInfor(billInfo.getProductId()).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.body() != null){
                    Product product = response.body();
                    viewHolder.binding.txtName.setText(product.getProductName());
                    viewHolder.binding.txtPrice.setText(CurrencyFormatter.getFormatter().format(Double.valueOf(product.getProductPrice())* billInfo.getAmount())+"");
                    Glide.with(context)
                            .load(product.getProductImage1())
                            .placeholder(R.drawable.default_image)
                            .into(viewHolder.binding.imgFood);
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {

            }
        });

        viewHolder.binding.txtCount.setText("Count: "+ billInfo.getAmount()+"");
    }

    @Override
    public int getItemCount() {
        return ds == null ? 0 : ds.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemBillinfoBinding binding;
        public ViewHolder(@NonNull ItemBillinfoBinding tmp) {
            super(tmp.getRoot());
            binding=tmp;
        }
    }
}
