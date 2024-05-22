package com.uteating.foodapp.Interface;



import com.uteating.foodapp.model.Cart;
import com.uteating.foodapp.model.CartProduct;
import com.uteating.foodapp.model.Comment;
import com.uteating.foodapp.model.Product;
import com.uteating.foodapp.model.User;
import com.uteating.foodapp.model.UserDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
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
    @GET("/api/products/{productId}")
    Call<Product> getProductInfor(@Path("productId") String productId);

    @GET("/api/user/{userId}")
    Call<User> getUserByUserId(@Path("userId") String userId);
    @PUT("/api/user/update")
    Call<User> updateUser(@Body User user);
    @POST("/api/user/product/add")
    Call<Product> addProduct(@Body Product product);
    @PUT("/api/user/product/edit")
    Call<Product> updateProduct(@Body Product product);
    @GET("/api/user/products")
    Call<List<Product>> getProductsPublisherId(@Query("publisherId") String publisherId);
    @PUT("/api/user/feedback")
    Call<Product> addComment(@Query("ratingAmount") int ratingAmount, @Query("ratingStar") double ratingStar, @Query("productId") String productId);

    @GET("/api/cart/productCart")
    Call<CartProduct> getProductCart(@Query("idProduct") String idProduct);

    @PUT("/api/admin/product/check")
    Call<Product> checkProduct(@Query("userId") String userId, @Query("productId") String productId);
}
