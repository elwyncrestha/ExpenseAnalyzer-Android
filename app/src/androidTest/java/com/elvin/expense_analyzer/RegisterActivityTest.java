package com.elvin.expense_analyzer;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.elvin.expense_analyzer.ui.activity.auth.RegisterActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> registerTest = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void testRegisterActivity() {
        onView(ViewMatchers.withId(R.id.etRegisterFirstName))
                .perform(ViewActions.typeText("Jack"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.etRegisterLastName))
                .perform(ViewActions.typeText("Doe"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.etRegisterEmail))
                .perform(ViewActions.typeText("jackdoe@gmail.com"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.etRegisterUsername))
                .perform(ViewActions.typeText("jackdoe"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.etRegisterPassword))
                .perform(ViewActions.typeText("12345678"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.btnRegister)).perform(ViewActions.click());
    }
}
