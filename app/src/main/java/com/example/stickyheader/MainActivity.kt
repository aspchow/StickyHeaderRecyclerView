package com.example.stickyheader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.paging.toLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stickyheader.databinding.ActivityMainBinding
import com.example.stickyheader.room.AppDatabase
import com.example.stickyheader.room.BugDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    lateinit var bugAdapter : BugPagedListAdapter
    lateinit var dao : BugDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dao = AppDatabase.getRoomDb(applicationContext).getBugDao()

        binding.insertBugs.setOnClickListener {
            addBugs()
        }

        binding.deleteBugs.setOnClickListener {
            deleteBugs()
        }

        binding.checkStatus.setOnClickListener {
            checkStatus()
        }


         bugAdapter = BugPagedListAdapter()

        binding.bugRecyclerView.apply {
            adapter = bugAdapter
            layoutManager = LinearLayoutManager(applicationContext)
            addItemDecoration(RecyclerViewItemDecoration(applicationContext, 100 , true, object : SectionCallBack{
                override fun isSectionHeader(position: Int): Boolean {
                    if(position%10==0)return true
                    return false
                }

                override fun getSectionHeaderName(position: Int): String {
                    return "${position/10}"
                }

            }))
        }


        dao.getBugDataSource().toLiveData(pageSize = 10 , initialLoadKey = 20,).observe(this, {
            bugAdapter.submitList(it)
        })





    }

    private fun checkStatus() {
        var size = 0
        val currentList = bugAdapter.currentList ?: return
        while (size < currentList.size && currentList[size] != null){
            size++
        }
        Toast.makeText(
            applicationContext,
            "The Current size is $size",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun deleteBugs() {
        lifecycleScope.launch(Dispatchers.IO){
            dao.deleteAllBugs()
        }
    }

    private fun addBugs() {
        val noOfBugs = binding.noOfBugsToInsert.text.toString()
        val bugs = mutableListOf<Bug>()
        repeat(noOfBugs.valid()){
            bugs.add(Bug(0,"BugName ${it+1}" , randomUser()))
        }
        lifecycleScope.launch(Dispatchers.IO){
            dao.insertBugs(bugs)
        }
    }
}

private fun String.valid(): Int {
    return if(this.isEmpty()) 0
    else this.toInt()
}
