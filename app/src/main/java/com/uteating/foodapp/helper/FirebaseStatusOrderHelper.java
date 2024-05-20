package com.uteating.foodapp.helper;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.RetrofitClient;
import com.uteating.foodapp.activity.MyShop.AddFoodActivity;
import com.uteating.foodapp.custom.CustomMessageBox.FailToast;
import com.uteating.foodapp.custom.CustomMessageBox.SuccessfulToast;
import com.uteating.foodapp.model.Bill;
import com.uteating.foodapp.model.BillInfo;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseStatusOrderHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceStatusOrder;
    private List<Bill> bills = new ArrayList<>();
    private String userId;
    private List<BillInfo> billInfoList = new ArrayList<>();
    private List<Integer> soldValueList = new ArrayList<>();
    APIService apiService;

    public interface DataStatus{
        void DataIsLoaded(List<Bill> bills, boolean isExistingBill);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();

    }

    public FirebaseStatusOrderHelper(String user) {
        userId = user;
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceStatusOrder = mDatabase.getReference();
    }

    public FirebaseStatusOrderHelper() {
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceStatusOrder = mDatabase.getReference();
    }

    public void readConfirmBills(String userId, final DataStatus dataStatus)
    {
        //Đọc và lấy các hoá đơn có trạng thái "Confirm" của một user
        mReferenceStatusOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bills.clear();
                boolean isExistingBill = false;
                for (DataSnapshot keyNode : snapshot.child("Bills").getChildren()) {
                    if (keyNode.child("senderId").getValue(String.class).equals(userId)
                    &&  keyNode.child("orderStatus").getValue(String.class).equals("Confirm")) {
                        Bill bill = keyNode.getValue(Bill.class);
                        bills.add(bill);
                        isExistingBill = true;
                    }
                }

                if (dataStatus != null) {
                    dataStatus.DataIsLoaded(bills, isExistingBill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readShippingBills(String userId, final DataStatus dataStatus)
    {
        //ọc và lấy các hoá đơn có trạng thái "Shipping" của một user
        mReferenceStatusOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bills.clear();
                boolean isExistingShippintBill = false;
                for (DataSnapshot keyNode : snapshot.child("Bills").getChildren())
                {
                    if (keyNode.child("senderId").getValue(String.class).equals(userId)
                            &&  keyNode.child("orderStatus").getValue(String.class).equals("Shipping"))
                    {
                        Bill bill = keyNode.getValue(Bill.class);
                        bills.add(bill);
                        isExistingShippintBill = true;
                    }
                }

                if (dataStatus != null) {
                    dataStatus.DataIsLoaded(bills, isExistingShippintBill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readCompletedBills(String userId,final DataStatus dataStatus)
    {
        //Đọc và lấy các hoá đơn có trạng thái "Completed" của một user
        mReferenceStatusOrder.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bills.clear();
                boolean isExistingBill = false;
                for (DataSnapshot keyNode : snapshot.child("Bills").getChildren()) {
                    if (keyNode.child("senderId").getValue(String.class).equals(userId)
                            && keyNode.child("orderStatus").getValue(String.class).equals("Completed")) {
                        Bill bill = keyNode.getValue(Bill.class);
                        bills.add(bill);
                        isExistingBill = true;
                    }
                }

                if (dataStatus != null) {
                    dataStatus.DataIsLoaded(bills, isExistingBill);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void setConfirmToShipping(String billId,final DataStatus dataStatus) {
        //Cập nhật trạng thái của một hoá đơn từ "Confirm" sang "Shipping".
        mReferenceStatusOrder.child("Bills").child(billId).child("orderStatus").setValue("Shipping")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dataStatus != null) {
                            dataStatus.DataIsUpdated();
                        }
                    }
                });
        // set sold and remainAmount value of Product
        billInfoList = new ArrayList<>();
        soldValueList = new ArrayList<>();

        mReferenceStatusOrder.child("BillInfos").child(billId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keyNode: snapshot.getChildren())
                {
                    BillInfo billInfo = keyNode.getValue(BillInfo.class);
                    billInfoList.add(billInfo);
                }
                readSomeInfoOfBill();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void setShippingToCompleted(String billId,final DataStatus dataStatus) {
        //Cập nhật trạng thái của một hoá đơn từ "Shipping" sang "Completed".
        mReferenceStatusOrder.child("Bills").child(billId).child("orderStatus").setValue("Completed")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (dataStatus != null) {
                            dataStatus.DataIsUpdated();
                        }
                    }
                });


    }

    public void readSomeInfoOfBill() {
        // Đọc thông tin về số lượng bán và cập nhật các giá trị liên quan cho các sản phẩm trong hoá đơn.
        apiService = RetrofitClient.getRetrofit().create(APIService.class);

        apiService.getAllProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.body() != null && response.isSuccessful()) {
                    List<Product> products = response.body();
                    for (BillInfo info : billInfoList) {
                        for (Product pro : products) {
                            if (info.getProductId().equals(pro.getProductId())) {
                                int sold = info.getAmount() + pro.getSold();
                                int amount = pro.getRemainAmount();
                                soldValueList.add(sold);
                                pro.setSold(sold);
                                pro.setRemainAmount(amount - sold);
                            }
                        }
                    }
                    updateSoldValueOfProduct(products);
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Xử lý lỗi khi không thể lấy danh sách sản phẩm
            }
        });
    }


    public void updateSoldValueOfProduct(List<Product> listro) {
        for(Product pro : listro){
            apiService.updateProduct(pro).enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful()) {
                    }
                }
                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    // Xử lý lỗi khi không thể cập nhật sản phẩm
                }
            });
        }

    }

}
