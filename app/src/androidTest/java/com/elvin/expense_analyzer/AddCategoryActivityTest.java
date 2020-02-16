package com.elvin.expense_analyzer;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.elvin.expense_analyzer.ui.activity.category.AddCategoryActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

/**
 * @author Elvin Shrestha on 2/16/2020
 */
@RunWith(AndroidJUnit4.class)
public class AddCategoryActivityTest {

    @Rule
    public ActivityTestRule<AddCategoryActivity> test = new ActivityTestRule<>(AddCategoryActivity.class);

    @Test
    public void test() {
        onView(withId(R.id.etCategoryName))
                .perform(ViewActions.typeText("Category 1"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.spCategoryType)).perform(click());
        onData(anything()).atPosition(0).perform(click());
//        onView(withId(R.id.spCategoryType)).check(matches(withSpinnerText(containsString("Expense"))));
        onView(withId(R.id.btnCategorySave)).perform(click());

    }
}
