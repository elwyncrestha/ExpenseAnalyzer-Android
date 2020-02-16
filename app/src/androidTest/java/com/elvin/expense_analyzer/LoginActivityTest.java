package com.elvin.expense_analyzer;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.elvin.expense_analyzer.ui.activity.auth.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> loginTest = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void testLoginActivity() {
        onView(ViewMatchers.withId(R.id.etLoginUsername)).perform(ViewActions.typeText("johndoe"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.etLoginPassword)).perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.btnLogin)).perform(ViewActions.click());
    }
}
