package com.example.findmyspot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import java.util.Map;

public interface ApiService {
    @POST("/login")
    Call<Map<String, String>> login(@Body Map<String, String> body);
}
