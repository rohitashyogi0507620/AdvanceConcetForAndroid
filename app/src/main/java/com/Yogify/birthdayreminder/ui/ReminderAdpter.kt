package com.Yogify.birthdayreminder.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.databinding.LayoutReminderListBinding
import com.Yogify.birthdayreminder.model.ReminderItem

class ReminderAdpter : RecyclerView.Adapter<ReminderAdpter.ViewHolder>() {

    inner class ViewHolder(val binding: LayoutReminderListBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<ReminderItem>() {
        override fun areItemsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<ReminderItem>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutReminderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.binding.apply {
            txtName.text = "${item.name}"
            txtWish.text = "${item.wish}"
            txtDateTime.text = "${item.date} ${item.time}"
        }

    }
}