package com.example.stickyheader

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.stickyheader.room.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class AppViewModel(context: Context) : ViewModel() {

    var currentOrder = Util.GROUP_BY_PROJECT
    val dao = AppDatabase.getRoomDb(context).getBugDao()
    private val sortOrder = MutableStateFlow(currentOrder)

    private val mainBugsFlow = dao.getBugs()
    val bugs = mainBugsFlow.flatMapLatest { bugs ->
        sortOrder.map {
            when (it) {
                Util.GROUP_BY_ALPHABETICAL_ORDER -> {
                    bugs.sortedBy { it.bugName[0].toUpperCase() }
                }
                Util.GROUP_BY_PROJECT -> {
                    bugs.sortedBy { it.projectName }
                }
                else -> bugs
            }
        }
    }.distinctUntilChanged()


    fun changeGroupBy(currentOrder: Int) {
        sortOrder.value = currentOrder
    }
}