package com.elvin.expense_analyzer;

import android.util.Log;

import com.elvin.expense_analyzer.endpoint.model.ExpenseStatus;
import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.model.dto.PageableDto;
import com.elvin.expense_analyzer.endpoint.service.ExpenseStatusService;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
public class ExpenseStatusServiceTest {

    private ExpenseStatusService service;
    private UserService userService;
    private String authenticatedUserToken;

    @Before
    public void setUp() {
        service = RetrofitUtils.getRetrofit().create(ExpenseStatusService.class);
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
        List<ExpenseStatus> expenseStatusList = new ArrayList<ExpenseStatus>() {{
            add(new ExpenseStatus("Expense status 1"));
            add(new ExpenseStatus("Expense status 3"));
            add(new ExpenseStatus("Expense status 4"));
            add(new ExpenseStatus("Expense status 2"));
            add(new ExpenseStatus("Expense status 5"));
            add(new ExpenseStatus("Expense status 6"));
            add(new ExpenseStatus("Expense status 7"));
            add(new ExpenseStatus("Expense status 8"));
            add(new ExpenseStatus("Expense status 9"));
            add(new ExpenseStatus("Expense status 10"));
            add(new ExpenseStatus("Expense status 11"));
            add(new ExpenseStatus("Expense status 12"));
        }};
        expenseStatusList.forEach(c -> {
            try {
                service.save(authenticatedUserToken, c).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        try {
            int size = 10;
            List<ExpenseStatus> all = service.getAll(authenticatedUserToken).execute().body().getDetail();
            PageableDto<ExpenseStatus> pageable = service.getPageable(authenticatedUserToken, 1, size).execute().body().getDetail();
            assertTrue(all.size() == pageable.getTotalElements() && pageable.getContent().size() == size);
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
