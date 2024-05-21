package com.uteating.foodapp.helper;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uteating.foodapp.Interface.APIService;
import com.uteating.foodapp.model.Product;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseFavouriteUserHelper {
    private APIService productService;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceFavourite;
    private ArrayList<String> keyProducts;
    private ArrayList<Product> favouriteList;


    public interface DataStatus {
        void DataIsLoaded(ArrayList<Product> favouriteProducts, ArrayList<String> keys);

        void DataIsInserted();

        void DataIsUpdated();

        void DataIsDeleted();
    }

    public FirebaseFavouriteUserHelper(APIService productService) {
        this.productService = productService;
        mDatabase = FirebaseDatabase.getInstance();
        mReferenceFavourite = mDatabase.getReference();
    }

    public void readFavouriteList(String userId, final DataStatus dataStatus) {
        mReferenceFavourite.child("Favorites").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                keyProducts = new ArrayList<>();
                favouriteList = new ArrayList<>();
                for (DataSnapshot keyNode : snapshot.getChildren()) {
                    keyProducts.add(keyNode.getKey());
                }
                readProductInfo(keyProducts, dataStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readProductInfo(ArrayList<String> keys, final DataStatus dataStatus) {
        // Khởi tạo danh sách sản phẩm yêu thích
        favouriteList = new ArrayList<>();

        // Lặp qua từng key để gửi yêu cầu đến API để lấy thông tin sản phẩm
        for (String key : keys) {
            Call<Product> call = productService.getProductInfor(key);
            call.enqueue(new Callback<Product>() {
                @Override
                public void onResponse(Call<Product> call, Response<Product> response) {
                    if (response.isSuccessful()) {
                        Product product = response.body();
                        if (product != null) {
                            favouriteList.add(product);
                            if (dataStatus != null && favouriteList.size() == keys.size()) {
                                // Nếu đã lấy thông tin của tất cả các sản phẩm yêu thích, gọi callback
                                dataStatus.DataIsLoaded(favouriteList, keys);
                            }
                        }
                    } else {
                        // Xử lý khi gọi API không thành công
                    }
                }

                @Override
                public void onFailure(Call<Product> call, Throwable t) {
                    // Xử lý lỗi khi gửi yêu cầu không thành công
                }
            });
        }
    }
}
