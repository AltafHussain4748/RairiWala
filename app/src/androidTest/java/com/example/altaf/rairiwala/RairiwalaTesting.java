package com.example.altaf.rairiwala;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.altaf.rairiwala.AccountManagment.AppStartUpPage;
import com.example.altaf.rairiwala.AccountManagment.UserLogin;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;

import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RairiwalaTesting {


    @Rule
    public ActivityTestRule<AppStartUpPage> mActivityRule =
            new ActivityTestRule<>(AppStartUpPage.class);

    @Test
    public void changeText_newActivity() {
        onView(withId(R.id.loginHaveAccount)).perform(click());
        onView(withId(R.id.login_phone_number)).perform(typeText("3470623241"),
                closeSoftKeyboard());
        onView(withId(R.id.login_up_pin)).perform(typeText("1122"), closeSoftKeyboard());
        onView(withId(R.id.login)).perform(click());

    }

}

