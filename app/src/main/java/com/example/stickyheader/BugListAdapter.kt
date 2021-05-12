package com.example.stickyheader

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stickyheader.databinding.BugListViewBinding

class BugListAdapter : ListAdapter<Bug, BugViewHolder>(object :DiffUtil.ItemCallback<Bug>(){
    override fun areItemsTheSame(oldItem: Bug, newItem: Bug): Boolean {
        return oldItem.bugId == newItem.bugId
    }

    override fun areContentsTheSame(oldItem: Bug, newItem: Bug): Boolean {
        return oldItem == newItem
    }

}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BugViewHolder {
        return BugViewHolder(BugListViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: BugViewHolder, position: Int) {
      val bug = getItem(position) ?: return
       holder.binding.apply {
           bugId.text =  bug.bugId.toString()
           bugName.text = bug.bugName
           bugsUser.text = bug.user
           bugProjectName.text = bug.projectName
       }
    }
}

class BugViewHolder(val binding:  BugListViewBinding) : RecyclerView.ViewHolder(binding.root)