package es.uniovi.eii.cows.view;


import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import es.uniovi.eii.cows.R;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static es.uniovi.eii.cows.MatcherManager.firstItem;
import static es.uniovi.eii.cows.MatcherManager.getText;
import static org.hamcrest.Matchers.equalToIgnoringCase;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    @Rule
    public ActivityScenarioRule<LaunchActivity> mActivityTestRule = new ActivityScenarioRule<>(LaunchActivity.class);

    @Test
    public void testClickFirst() {
        ViewInteraction recyclerView = onView(withId(R.id.idRecycler_main));
        // Get title of first news item
        String titleNews0 = getText(firstItem(withId(R.id.idTitle)));
        // Click on first news item
        recyclerView.perform(actionOnItemAtPosition(0, click()));
        // Assert that item has the title
        assertThat(getText(withId(R.id.idTitle_news)), equalToIgnoringCase(titleNews0));
    }

    @Test
    public void testScroll() {
        ViewInteraction recyclerView = onView(withId(R.id.idRecycler_main));
        try { Thread.sleep(3000); } catch (InterruptedException e) { e.printStackTrace(); }
        // Get title of first news item
        String titleNews0 = getText(firstItem(withId(R.id.idTitle)));
        // Try to scroll down
        recyclerView.perform(swipeUp());
        // Check that first item is not visible
        onView(withText(titleNews0)).check(doesNotExist());
    }

    @Test
    public void testRefresh() {
        ViewInteraction recyclerView = onView(withId(R.id.idRecycler_main));
        // Try to refresh news
        recyclerView.perform(swipeDown());
    }
}