package com.Yogify.birthdayreminder.db

import androidx.room.*
import com.Yogify.birthdayreminder.model.ReminderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DataDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminderItem: ReminderItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(entities: List<ReminderItem>)

    @Delete
    suspend fun deleteReminder(reminderItem: ReminderItem)

    @Update
    suspend fun updateReminder(reminderItem: ReminderItem)

    @Query("select * from REMINDER_TABLE ORDER BY id DESC")
    fun getReminder():Flow<List<ReminderItem>>


//    @Query("SELECT * FROM REMINDER_TABLE ORDER BY id DESC LIMIT :limit OFFSET :offset")
//    suspend fun getReminder(limit: Int, offset: Int): Flow<List<ReminderItem>>

//    @Query("select * from MotorRecentSearch WHERE productCode = :productCode order by quotationNumber desc")
//    fun getRecentSearch(productCode: String): Flow<List<ReminderItem>>

}