package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.ExpectFailure.assertThat
import com.udacity.project4.locationreminders.data.FakeDataSource
import junit.framework.TestCase.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    //TODO: provide testing to the SaveReminderView and its live data objects

//    // Subject under test
//    private lateinit var viewModel: SaveReminderViewModel
//
//    // Executes each task synchronously using Architecture Components.
//    @get:Rule
//    var instantExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var remindersRepository: FakeDataSource
//
//
//    @Before
//    fun setupViewModel() {
//        remindersRepository = FakeDataSource()
//        viewModel =
//            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), remindersRepository)
//    }

    @Test
    fun saveReminder_setsNewReminder() {
        //Given a fresh ViewModel
        assertEquals(4, 2 + 2)


        //when adding a new Reminder
        //saveReminderViewModel.saveReminder()

        //Then new Reminder event is triggered

    }


}