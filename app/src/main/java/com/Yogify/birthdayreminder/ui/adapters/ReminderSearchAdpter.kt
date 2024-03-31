package com.Yogify.birthdayreminder.ui.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.databinding.LayoutReminderGridBinding
import com.Yogify.birthdayreminder.databinding.LayoutReminderListBinding
import com.Yogify.birthdayreminder.databinding.LayoutReminderSearchBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.Locale


class ReminderSearchAdpter : RecyclerView.Adapter<ReminderSearchAdpter.ViewHolder>() {


    lateinit var mlistner: OnItemClickListner
    var listdata = mutableListOf<ReminderItem>()

    interface OnItemClickListner {
        fun onItemClick(type: Int, item: ReminderItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListner) {
        mlistner = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<ReminderItem>() {
        override fun areItemsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<ReminderItem>) {
        differ.submitList(list)
        listdata.clear()
        listdata.addAll(listdata)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutReminderSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }


    inner class ViewHolder(var binding: LayoutReminderSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.binding.apply {
            txtName.text = "${item?.name}"
            Glide.with(imgProfile.context).load(item.imageUri).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(com.Yogify.birthdayreminder.R.drawable.ic_profile_demo).into(imgProfile)

            cardView.setOnClickListener {
                mlistner.onItemClick(1, item)
            }
        }

    }

    fun performItemClick(item: ReminderItem) {
        mlistner.onItemClick(1, item)
    }


}