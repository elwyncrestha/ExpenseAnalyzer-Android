package com.elvin.expense_analyzer.endpoint.service;

import com.elvin.expense_analyzer.endpoint.model.Expense;
import com.elvin.expense_analyzer.endpoint.model.dto.ExpenseCountDto;
import com.elvin.expense_analyzer.endpoint.model.dto.PageableDto;
import com.elvin.expense_analyzer.endpoint.model.dto.ResponseDto;
import com.elvin.expense_analyzer.endpoint.model.dto.TransactionDurationDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * @author Elvin Shrestha on 2/12/2020
 */
public interface TransactionService {

    String URL = "v1/expense";

    @POST(TransactionService.URL)
    Call<ResponseDto<Expense>> save(@Header("Authorization") String token, @Body Expense expense);

    @PATCH(TransactionService.URL)
    Call<ResponseDto<Expense>> update(@Header("Authorization") String token, @Body Expense expense);

    @GET(TransactionService.URL + "/all")
    Call<ResponseDto<List<Expense>>> getAll(@Header("Authorization") String token);

    @POST(TransactionService.URL + "/all")
    Call<ResponseDto<List<Expense>>> getAllWithSearch(@Header("Authorization") String token, @Body Object object);

    @DELETE(TransactionService.URL + "/{id}")
    Call<Void> delete(@Header("Authorization") String token, @Path("id") String id);

    @GET(TransactionService.URL + "/list")
    Call<ResponseDto<PageableDto<Expense>>> getPageable(@Header("Authorization") String token, @Query("page") Integer page, @Query("size") Integer size);

    @GET(TransactionService.URL + "/status-count")
    Call<ResponseDto<ExpenseCountDto>> statusCount(@Header("Authorization") String token);

    @GET(TransactionService.URL + "/{id}")
    Call<ResponseDto<Expense>> getById(@Header("Authorization") String token, @Path("id") String id);

    @GET(TransactionService.URL + "/chart/transaction-duration")
    Call<ResponseDto<TransactionDurationDto>> getByDurations(@Header("Authorization") String token);
}
