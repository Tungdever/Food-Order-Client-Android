package com.uteating.foodapp.helper;

import androidx.annotation.NonNull;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.model.BillInfo;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseOrderDetailHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceStatusOrder;

    List<BillInfo> billInfos = new ArrayList<>();
    APIService apiService;
    public interface DataStatus{
        void DataIsLoaded(String addresss, List<BillInfo> billInfos);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();

    }
    public interface DataStatus2{
        void DataIsLoaded(Product product);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();

    }

    public FirebaseOrderDetailHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceStatusOrder = mDatabase.getReference();
    }

    public void readOrderDetail(String addressId,String userId,String billId,final DataStatus dataStatus )
    {
        mReferenceStatusOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String addressDetail = snapshot.child("Address").child(userId).child(addressId).child("detailAddress").getValue(String.class);
                billInfos.clear();
                for (DataSnapshot keyNode: snapshot.child("BillInfos").child(billId).getChildren())
                {
                    billInfos.add(keyNode.getValue(BillInfo.class));
                }
                if (dataStatus != null) {
                    dataStatus.DataIsLoaded(addressDetail,billInfos);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readProductInfo(String productId, final DataStatus2 dataStatus)
    {
        mReferenceStatusOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                apiService =  RetrofitClient.getRetrofit().create(APIService.class);
                apiService.getProductInfor(productId).enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if (response.body() != null ){
                            Product product = response.body();
                            if (dataStatus != null) {
                                dataStatus.DataIsLoaded(product);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {

                    }
                });
               // Product product = snapshot.child("Products").child(productId).getValue(Product.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
