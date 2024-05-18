package com.uteating.foodapp.Interface;



import com.uteating.foodapp.model.Cart;
import com.uteating.foodapp.model.CartProduct;
import com.uteating.foodapp.model.Product;
import com.uteating.foodapp.model.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {
    @POST("api/auth/signup")
    Call<String> signUp(@Body UserDTO user);
    @GET("/api/products")
    Call<List<Product>> getAllProducts();
    @GET("/api/products/filter")
    Call<List<Product>> getProductsByType(@Query("type") String type);
    @GET("/api/products/search")
    Call<List<Product>> searchProduct(@Query("keyword") String keyword);

    @GET("/api/cart/productCart")
    Call<CartProduct> getProductCart(@Query("idProduct") String idProduct);
}
