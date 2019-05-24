package com.project.studenthub.APIRequests;


import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ImageAPI {

    String BASE_URL = "https://vision.googleapis.com/";

    @POST("v1/images:annotate")
    Call<ResponseBody> sendImage(@Query("key") String key, @Body JSONObject body);

}
