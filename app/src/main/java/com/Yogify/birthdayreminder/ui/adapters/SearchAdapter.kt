package com.Yogify.birthdayreminder.ui.adapters

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.databinding.LayoutReminderSearchBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class SearchAdapter(listData: List<ReminderItem>) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var listData: List<ReminderItem>

    lateinit var mlistner: OnItemClickListner

    interface OnItemClickListner {
        fun onItemClick(type: Int, item: ReminderItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListner) {
        mlistner = listener
    }

    init {
        this.listData = listData
    }

    fun filterList(filterlist: List<ReminderItem>) {
        listData = filterlist
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutReminderSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listData.get(position)
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

    override fun getItemCount(): Int {
        return listData.size
    }

    inner class ViewHolder(var binding: LayoutReminderSearchBinding) :
        RecyclerView.ViewHolder(binding.root)

}