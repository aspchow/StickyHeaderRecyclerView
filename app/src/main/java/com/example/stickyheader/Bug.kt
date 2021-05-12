package com.example.stickyheader

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

fun randomUser() = "User ${Random.nextInt(1, 4)}"

fun randomBugName(): String {
    val a = ('A'..'Z')
    return (1..6).map {
        a.random()
    }.joinToString("")
}


@Entity
data class Bug(
    @PrimaryKey(autoGenerate = true)
    val bugId: Int,
    val bugName: String,
    val user: String,
    val projectName: String,
    val status: String,
)