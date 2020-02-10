package com.elvin.expense_analyzer.endpoint.service;

import com.elvin.expense_analyzer.endpoint.model.PaymentMethod;
import com.elvin.expense_analyzer.endpoint.model.dto.PageableDto;
import com.elvin.expense_analyzer.endpoint.model.dto.PaymentMethodCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * @author Elvin Shrestha on 2/10/2020
 */
public interface PaymentMethodService {

    String URL = "v1/payment-method";

    @POST(PaymentMethodService.URL)
    Call<ResponseDto<PaymentMethod>> save(@Header("Authorization") String token, @Body PaymentMethod paymentMethod);

    @PATCH(PaymentMethodService.URL)
    Call<ResponseDto<PaymentMethod>> update(@Header("Authorization") String token, @Body PaymentMethod paymentMethod);

    @GET(PaymentMethodService.URL + "/all")
    Call<ResponseDto<List<PaymentMethod>>> getAll(@Header("Authorization") String token);

    @POST(PaymentMethodService.URL + "/all")
    Call<ResponseDto<List<PaymentMethod>>> getAllWithSearch(@Header("Authorization") String token, @Body Object object);

    @DELETE(PaymentMethodService.URL + "/{id}")
    Call<Void> delete(@Header("Authorization") String token, @Path("id") String id);

    @GET(PaymentMethodService.URL + "/list?page={page}&size={size}")
    Call<ResponseDto<PageableDto<PaymentMethod>>> getPageable(@Header("Authorization") String token, @Path("page") Integer page, @Path("size") Integer size);

    @GET(PaymentMethodService.URL + "/status-count")
    Call<ResponseDto<PaymentMethodCountDto>> statusCount(@Header("Authorization") String token);

    @GET(PaymentMethodService.URL + "/{id}")
    Call<ResponseDto<PaymentMethod>> getById(@Header("Authorization") String token, @Path("id") String id);
}
