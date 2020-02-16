package com.elvin.expense_analyzer;

import android.util.Log;

import com.elvin.expense_analyzer.endpoint.model.User;
import com.elvin.expense_analyzer.endpoint.service.UserService;
import com.elvin.expense_analyzer.utils.RetrofitUtils;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
public class UserServiceTest {

    private UserService service;

    @Before
    public void setUp() {
        service = RetrofitUtils.getRetrofit().create(UserService.class);
    }

    @Test
    public void testSignUp() {
        User user = new User(
                "John", null, "Doe",
                "johndoe@gmail.com", "johndoe",
                "12345678", null, null
        );
        try {
            assertEquals(201, service.save(user).execute().code());
        } catch (IOException e) {
            Log.e("Register User Test", "Failed to save user", e);
        }
    }

    @Test
    public void testLogin() {
        User user = new User();
        user.setUsername("johndoe");
        user.setPassword("12345678");
        try {
            String token = service.login(user).execute().body().getDetail();
            User logged = service.getAuthenticated(token).execute().body().getDetail();
            service.logout(token).execute();
            assertEquals(user.getUsername(), logged.getUsername());
        } catch (IOException e) {
            Log.e("Login User Test", "Failed to login user", e);
        }
    }
}
