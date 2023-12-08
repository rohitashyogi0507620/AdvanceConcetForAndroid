package com.Yogify.birthdayreminder.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.databinding.LayoutReminderGridBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_dd_MMMM
import com.Yogify.birthdayreminder.util.utils.Companion.datetoFormate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class MainAdapter : PagingDataAdapter<ReminderItem, MainAdapter.MainViewHolder>(DIFF_CALLBACK) {

    var viewcode = 0

    lateinit var mlistner: OnItemClickListner
    lateinit var mlonglistner: OnItemLongClickListner


    interface OnItemClickListner {
        fun onItemClick(item: ReminderItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListner) {
        mlistner = listener
    }

    interface OnItemLongClickListner {
        fun onItemLongClick(item: ReminderItem, type: Int)
    }

    fun setOnItemLOngClickListener(listener: OnItemLongClickListner) {
        mlonglistner = listener
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ReminderItem>() {
            override fun areItemsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ReminderItem, newItem: ReminderItem): Boolean =
                oldItem == newItem
        }
    }

    inner class MainViewHolder(val binding: LayoutReminderGridBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        if (viewType == 1) {
            return MainViewHolder(
                LayoutReminderGridBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        } else {
            return MainViewHolder(
                LayoutReminderGridBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.apply {
            txtName.text = "${item?.name}"
            txtWish.text = "${item?.wish}"
            txtDateTime.text = datetoFormate(item?.date!!, DATE_dd_MMMM)
            Glide.with(imgProfile.context).load(item?.bitmap).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).into(imgProfile)
            txtName.setTextColor(Color.parseColor(item?.colorDark))
          //  txtWish.setTextColor(Color.parseColor(item?.colorDark))
            txtDateTime.setTextColor(Color.parseColor(item?.colorDark))

            cardView.setCardBackgroundColor(Color.parseColor(item?.colorLight))
            cardView.strokeColor=Color.parseColor(item?.colorDark)
            imgProfile.strokeColor = ColorStateList.valueOf(Color.parseColor(item?.colorDark))
            imgDelete.setColorFilter(Color.parseColor(item?.colorDark))



            imgDelete.setOnClickListener {
                mlistner.onItemClick(item!!)
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        return viewcode
    }

    fun setViewType(layout: Int): Int {
        viewcode = layout
        return layout
    }
}