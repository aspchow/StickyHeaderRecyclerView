package com.example.stickyheader

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stickyheader.databinding.ActivityMainBinding
import com.example.stickyheader.databinding.SortDialogBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun printMsg(msg: String) = Log.d("AVINASH", msg)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    lateinit var bugAdapter: BugListAdapter


    lateinit var viewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = AppViewModel(applicationContext)

        binding.insertBugs.setOnClickListener {
            addBugs()
        }

        binding.deleteBugs.setOnClickListener {
            deleteBugs()
        }

        binding.checkStatus.setOnClickListener {
            checkStatus()
        }

        binding.changeGroupBy.setOnClickListener {
            showChangeGroupDialog()
        }


        bugAdapter = BugListAdapter()

        binding.bugRecyclerView.apply {
            adapter = bugAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            addItemDecoration(
                RecyclerViewItemDecoration(
                    true,
                    object : SectionCallBack {
                        override fun isSectionHeader(position: Int): Boolean {
                            if ((position in 0 until bugAdapter.itemCount).not() || viewModel.currentOrder == Util.GROUP_BY_NONE) return false
                            if (position == 0)
                                return true
                            return getSectionHeaderName(position) != getSectionHeaderName(position - 1)
                        }

                        override fun getSectionHeaderName(position: Int): String =
                            if ((position in 0 until bugAdapter.itemCount).not()) "" else
                                when (viewModel.currentOrder) {
                                    Util.GROUP_BY_ALPHABETICAL_ORDER -> "${
                                        bugAdapter.currentList[position].bugName[0]
                                            .toUpperCase()
                                    }"
                                    Util.GROUP_BY_PROJECT -> bugAdapter.currentList[position].projectName

                                    else -> ""
                                }

                    })
            )
        }


        viewModel.bugs.asLiveData(context = Dispatchers.IO).observe(this, {
            bugAdapter.submitList(it)

        })


    }

    private fun checkStatus() {
        // var size = 0
       // val currentList = bugAdapter.currentList
        /*   while (size < currentList.size && currentList[size] != null) {
               size++
           }*/
        Toast.makeText(
            applicationContext,
            "The Current size is ${bugAdapter.currentList.size}",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteBugs() {
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.dao.deleteAllBugs()
        }
    }

    private fun addBugs() {
        val noOfBugs = binding.noOfBugsToInsert.text.toString()
        val bugs = mutableListOf<Bug>()
        repeat(noOfBugs.valid()) {
            bugs.add(
                Bug(
                    0, randomBugName(),
                    randomUser(),
                    "Project ${(1..6).random()}",
                    "Status ${(1..6).random()}"
                )
            )
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.dao.insertBugs(bugs)
        }
    }


    private fun showChangeGroupDialog() {
        Toast.makeText(applicationContext, "the change grouop is called", Toast.LENGTH_SHORT).show()
        val dialog = Dialog(this)
        val dialogBinding = SortDialogBinding.inflate(layoutInflater)

        dialogBinding.apply {
            when (viewModel.currentOrder) {
                Util.GROUP_BY_ALPHABETICAL_ORDER -> groupByAlphabeticalOrder.isChecked = true
                Util.GROUP_BY_PROJECT -> groupByProject.isChecked = true
                else -> groupByNone.isChecked = true
            }



            apply.setOnClickListener {
                viewModel.currentOrder = sortGroup.checkedRadioButtonId
                when (viewModel.currentOrder) {
                    Util.GROUP_BY_ALPHABETICAL_ORDER -> {
                        Toast.makeText(applicationContext, "checked alpha", Toast.LENGTH_SHORT)
                            .show()
                    }
                    Util.GROUP_BY_PROJECT -> {
                        Toast.makeText(applicationContext, "checked proj", Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Toast.makeText(applicationContext, "checked none", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                viewModel.changeGroupBy(viewModel.currentOrder)
                dialog.dismiss()

            }

            cancle.setOnClickListener {
                dialog.dismiss()
            }


        }


        dialog.setContentView(dialogBinding.root)
        dialog.show()
    }


}

private fun String.valid(): Int {
    return if (this.isEmpty()) 0
    else this.toInt()
}