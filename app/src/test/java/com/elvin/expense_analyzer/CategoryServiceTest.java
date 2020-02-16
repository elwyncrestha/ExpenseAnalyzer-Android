package com.elvin.expense_analyzer;

import android.util.Log;

import com.elvin.expense_analyzer.endpoint.model.Category;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.model.enums.CategoryType;
import com.elvin.expense_analyzer.endpoint.service.CategoryService;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
public class CategoryServiceTest {

    private CategoryService service;
    private UserService userService;
    private String authenticatedUserToken;

    @Before
    public void setUp() {
        service = RetrofitUtils.getRetrofit().create(CategoryService.class);
        userService = RetrofitUtils.getRetrofit().create(UserService.class);
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("12345678");
        try {
            authenticatedUserToken = userService.login(user).execute().body().getDetail();
        } catch (IOException e) {
            Log.e("Login User", "Failed to login user", e);
        }
    }

    @Test
    public void test() {
        List<Category> categoryList = new ArrayList<Category>() {{
            add(new Category("Transportation", CategoryType.EXPENSE.ordinal()));
            add(new Category("Food", CategoryType.EXPENSE.ordinal()));
            add(new Category("Salary", CategoryType.INCOME.ordinal()));
            add(new Category("Business", CategoryType.INCOME.ordinal()));
            add(new Category("EMI Payment", CategoryType.EXPENSE.ordinal()));
        }};
        categoryList.forEach(c -> {
            try {
                service.save(authenticatedUserToken, c).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            List<Category> all = service.getAll(authenticatedUserToken).execute().body().getDetail();
            assertEquals(categoryList.size(), all.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void cleanUp() {
        try {
            userService.logout(authenticatedUserToken).execute();
        } catch (IOException e) {
            Log.e("Logout User Test", "Failed to logout user", e);
        }
    }


}
