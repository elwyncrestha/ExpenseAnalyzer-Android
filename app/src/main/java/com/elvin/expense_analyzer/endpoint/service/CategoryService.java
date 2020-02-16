package com.elvin.expense_analyzer.endpoint.service;

import com.elvin.expense_analyzer.endpoint.model.Category;
import com.elvin.expense_analyzer.endpoint.model.dto.CategoryCountDto;
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
import retrofit2.http.Query;

/**
 * @author Elvin Shrestha on 2/9/2020
 */
public interface CategoryService {

    String URL = "v1/category";

    @POST(CategoryService.URL)
    Call<ResponseDto<Category>> save(@Header("Authorization") String token, @Body Category category);

    @PATCH(CategoryService.URL)
    Call<ResponseDto<Category>> update(@Header("Authorization") String token, @Body Category category);

    @GET(CategoryService.URL + "/all")
    Call<ResponseDto<List<Category>>> getAll(@Header("Authorization") String token);

    @POST(CategoryService.URL + "/all")
    Call<ResponseDto<List<Category>>> getAllWithSearch(@Header("Authorization") String token, @Body Object object);

    @DELETE(CategoryService.URL + "/{id}")
    Call<Void> delete(@Header("Authorization") String token, @Path("id") String id);

    @GET(CategoryService.URL + "/list")
    Call<ResponseDto<PageableDto<Category>>> getPageable(@Header("Authorization") String token, @Query("page") Integer page, @Query("size") Integer size);

    @GET(CategoryService.URL + "/status-count")
    Call<ResponseDto<CategoryCountDto>> statusCount(@Header("Authorization") String token);

    @GET(CategoryService.URL + "/{id}")
    Call<ResponseDto<Category>> getById(@Header("Authorization") String token, @Path("id") String id);
}
