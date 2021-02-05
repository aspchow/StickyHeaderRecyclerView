package com.example.stickyheader

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

fun randomUser() = "User ${Random.nextInt(1,4)}"

@Entity
data class Bug(
    @PrimaryKey(autoGenerate = true)
    val bugId : Int, val bugName : String, val user : String)