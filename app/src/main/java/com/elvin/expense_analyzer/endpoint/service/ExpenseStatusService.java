package com.elvin.expense_analyzer.endpoint.service;

import com.elvin.expense_analyzer.endpoint.model.ExpenseStatus;
import com.elvin.expense_analyzer.endpoint.model.dto.ExpenseStatusCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.PageableDto;
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
public interface ExpenseStatusService {

    String URL = "v1/expense-status";

    @POST(ExpenseStatusService.URL)
    Call<ResponseDto<ExpenseStatus>> save(@Header("Authorization") String token, @Body ExpenseStatus expenseStatus);

    @PATCH(ExpenseStatusService.URL)
    Call<ResponseDto<ExpenseStatus>> update(@Header("Authorization") String token, @Body ExpenseStatus expenseStatus);

    @GET(ExpenseStatusService.URL + "/all")
    Call<ResponseDto<List<ExpenseStatus>>> getAll(@Header("Authorization") String token);

    @POST(ExpenseStatusService.URL + "/all")
    Call<ResponseDto<List<ExpenseStatus>>> getAllWithSearch(@Header("Authorization") String token, @Body Object object);

    @DELETE(ExpenseStatusService.URL + "/{id}")
    Call<Void> delete(@Header("Authorization") String token, @Path("id") String id);

    @GET(ExpenseStatusService.URL + "/list?page={page}&size={size}")
    Call<ResponseDto<PageableDto<ExpenseStatus>>> getPageable(@Header("Authorization") String token, @Path("page") Integer page, @Path("size") Integer size);

    @GET(ExpenseStatusService.URL + "/status-count")
    Call<ResponseDto<ExpenseStatusCountDto>> statusCount(@Header("Authorization") String token);

    @GET(ExpenseStatusService.URL + "/{id}")
    Call<ResponseDto<ExpenseStatus>> getById(@Header("Authorization") String token, @Path("id") String id);
}
