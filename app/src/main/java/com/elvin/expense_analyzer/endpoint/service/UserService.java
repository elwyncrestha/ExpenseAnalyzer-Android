package com.elvin.expense_analyzer.endpoint.service;

import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.model.dto.ResetPasswordDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Elvin Shrestha on 2/3/2020
 */
public interface UserService {

    String URL = "v1/users";

    @POST(UserService.URL)
    Call<ResponseDto<User>> save(@Body User user);

    @PATCH(UserService.URL)
    Call<ResponseDto<User>> update(@Body User user);

    @POST(UserService.URL + "/login")
    Call<ResponseDto<String>> login(@Body User user);

    @GET(UserService.URL + "/authenticated")
    Call<ResponseDto<User>> getAuthenticated(@Header("Authorization") String token);

    @GET(UserService.URL + "/logout")
    Call<Void> logout(@Header("Authorization") String token);

    @GET(UserService.URL + "/logout/all")
    Call<Void> logoutAll(@Header("Authorization") String token);

    @GET(UserService.URL + "/reset-password/email/{value}")
    Call<ResponseDto<User>> initiateResetPassword(@Path("value") String email);

    @POST(UserService.URL + "/reset-password")
    Call<Void> resetPassword(@Body ResetPasswordDto dto);
}
