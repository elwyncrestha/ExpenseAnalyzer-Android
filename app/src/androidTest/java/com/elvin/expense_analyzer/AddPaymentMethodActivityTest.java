package com.elvin.expense_analyzer;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.elvin.expense_analyzer.ui.activity.paymentmethod.AddPaymentMethodActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
@RunWith(AndroidJUnit4.class)
public class AddPaymentMethodActivityTest {

    @Rule
    public ActivityTestRule<AddPaymentMethodActivity> test = new ActivityTestRule<>(AddPaymentMethodActivity.class);

    @Test
    public void test() {
        onView(ViewMatchers.withId(R.id.etPaymentMethodName))
                .perform(ViewActions.typeText("Direct Cash"), ViewActions.closeSoftKeyboard());
        onView(ViewMatchers.withId(R.id.btnPaymentMethodSave)).perform(ViewActions.click());

    }
}
