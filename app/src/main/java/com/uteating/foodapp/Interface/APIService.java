package com.uteating.foodapp.Interface;



import com.uteating.foodapp.model.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("api/auth/signup")
    Call<String> signUp(@Body UserDTO user);
}
