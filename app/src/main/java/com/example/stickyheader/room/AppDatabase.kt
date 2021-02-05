package com.example.stickyheader.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stickyheader.Bug

@Database(entities = [Bug::class] , version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getBugDao() : BugDao

    companion object{
       private lateinit var roomDatabase: AppDatabase
        fun getRoomDb(context: Context): AppDatabase {
            if (::roomDatabase.isInitialized.not()){
                roomDatabase = Room.databaseBuilder(context, AppDatabase::class.java , "AppDb").build()
            }
            return roomDatabase
        }
    }
}