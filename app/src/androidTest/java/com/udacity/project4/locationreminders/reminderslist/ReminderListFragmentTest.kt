package com.udacity.project4.locationreminders.reminderslist

import android.app.Application
import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.FakeDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    // An Idling Resource that waits for Data Binding to have no pending bindings

    private lateinit var repository: FakeDataSource
    private lateinit var appContext: Application
    private lateinit var viewModel: RemindersListViewModel
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup()
    {
        appContext = getApplicationContext()
        repository = FakeDataSource()
        viewModel = RemindersListViewModel(getApplicationContext(), repository)
        stopKoin()
        val myModule = module {
            single {
                viewModel
            }
        }
        startKoin {
            modules(listOf(myModule))
        }
    }
    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun saveReminderNavigation()
    {

        val navScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        val navController = mock(NavController::class.java)
        navScenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }

        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }
    @Test
    fun dataDisplay() = runBlockingTest{
        val reminder = ReminderDTO("Test Title",
            "Test Description",
            "",
            10.456465,
            5.321232)
        repository.saveReminder(reminder)
        val navScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withText(reminder.title)).check(matches(isDisplayed()))
    }
    @Test
    fun noDataDisplay() = runBlockingTest {
        repository.deleteAllReminders()
        val navScenario = launchFragmentInContainer<ReminderListFragment>(Bundle(), R.style.AppTheme)
        onView(withId(R.id.noDataTextView)).check(matches(isDisplayed()))
    }
}