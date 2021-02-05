package com.example.stickyheader.room

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.stickyheader.Bug

@Dao
interface BugDao {
    @Insert
    fun insertBugs(bugs : List<Bug>)

    @Delete
    fun deleteBugs(bug: Bug)

    @Query("SELECT * FROM Bug")
    fun getBugDataSource() : DataSource.Factory<Int,Bug>

    @Query("DELETE FROM Bug")
    fun deleteAllBugs()
}