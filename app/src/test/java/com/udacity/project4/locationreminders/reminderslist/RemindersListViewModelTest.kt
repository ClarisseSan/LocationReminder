package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.core.IsNull.notNullValue
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RemindersListViewModelTest {

    //provide testing to the RemindersListViewModel and its live data objects

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Subject under test
    private lateinit var viewModel: RemindersListViewModel

    private lateinit var fakeDataSource: FakeDataSource

    @Before
    fun setupViewModel() = runBlocking {
        fakeDataSource = FakeDataSource()
        viewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), fakeDataSource)
    }

    @After
    fun finish() {
        //Stop Koin Application that was started
        stopKoin()
    }

    @Test
    fun invalidateShowNoData_emptyList_showNoData(){
        //given
        val remindersList = MutableLiveData<List<ReminderDataItem>>()

        //when
        viewModel.showNoData.value = remindersList.value == null || remindersList.value!!.isEmpty()

        //then
        assertTrue(viewModel.showNoData.getOrAwaitValue())
    }
    @Test
    fun loadReminders_notEmptyList() = runBlocking {
        //given
        val reminder1 = ReminderDTO(
            "Grocery",
            "Buy apples and bananas",
            "Savemore Alabang",
            14.417399772472931,
            121.03937432329282
        )

        fakeDataSource.saveReminder(reminder1)

        //when
        viewModel.loadReminders()

        val result = viewModel.remindersList.getOrAwaitValue()

        //then
        assertThat(result, `is`(notNullValue()))
    }




}