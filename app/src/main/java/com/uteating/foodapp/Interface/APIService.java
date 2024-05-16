package com.uteating.foodapp.Interface;



import com.uteating.foodapp.model.Product;
import com.uteating.foodapp.model.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIService {
    @POST("api/auth/signup")
    Call<String> signUp(@Body UserDTO user);
    @GET("/api/products")
    Call<List<Product>> getProducts();
}
