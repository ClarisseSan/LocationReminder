package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()) :
    ReminderDataSource {

    //Create a fake data source to act as a double to the real data source
    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun getReminders(): Result<List<ReminderDTO>> {

        if (shouldReturnError) {
            return Result.Error("Reminders Error")
        }

        return try {
            Result.Success(reminders)
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage)
        }
    }


    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        if (shouldReturnError) {
            return Result.Error("Reminder Error")
        }

        try {
            val reminder = reminders.find {
                it.id == id
            }
            if (reminder != null) {
                return Result.Success(reminder)
            } else {
                return Result.Error("Reminder not found!")
            }
        } catch (e: Exception) {
            return Result.Error("Reminder Error")
        }


    }


}