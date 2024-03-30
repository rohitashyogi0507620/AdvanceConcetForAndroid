package com.Yogify.birthdayreminder.ui.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.R
import com.Yogify.birthdayreminder.databinding.LayoutReminderGridBinding
import com.Yogify.birthdayreminder.databinding.LayoutReminderListBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_dd_MMMM
import com.Yogify.birthdayreminder.util.utils.Companion.LAYOUT_GRID
import com.Yogify.birthdayreminder.util.utils.Companion.calculateRemainDays
import com.Yogify.birthdayreminder.util.utils.Companion.longToDate
import com.Yogify.birthdayreminder.util.utils.Companion.remainDaysformate
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy


class ReminderAdpter(var type: Int) : RecyclerView.Adapter<ReminderAdpter.ViewHolder>() {


    lateinit var mlistner: OnItemClickListner
    lateinit var mlonglistner: OnItemLongClickListner
    var layoutType = LAYOUT_GRID


    interface OnItemClickListner {
        fun onItemClick(type: Int, item: ReminderItem)
    }

    fun setOnItemClickListener(listener: OnItemClickListner) {
        mlistner = listener
    }

    interface OnItemLongClickListner {
        fun onItemLongClick(type: Int, item: ReminderItem)
    }

    fun setOnItemLOngClickListener(listener: OnItemLongClickListner) {
        mlonglistner = listener
    }


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
        val bindingGrid =
            LayoutReminderGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val bindingList =
            LayoutReminderListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(bindingGrid, bindingList)
    }


    inner class ViewHolder(
        var bindingGrid: LayoutReminderGridBinding, var bindingList: LayoutReminderListBinding
    ) : RecyclerView.ViewHolder(if (layoutType == LAYOUT_GRID) bindingGrid.root else bindingList.root)

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.bindingList.apply {
            txtName.text = "${item?.name}"
            txtWish.text = "${item?.wish}"
            txtDateTime.text = longToDate(item?.date!!, DATE_dd_MMMM)
            var remainingDays = calculateRemainDays(item.date!!)
            if (remainingDays == 0) {
                lottie.visibility = View.VISIBLE
                lottie.setAnimation(R.raw.celebration)
            } else lottie.visibility = View.GONE
            txtDayRemains.text = remainDaysformate(imgProfile.context, remainingDays)

            txtName.setTextColor(Color.parseColor(item.colorDark))
            txtWish.setTextColor(Color.parseColor(item.colorDark))
            txtDateTime.setTextColor(Color.parseColor(item.colorDark))
            txtDayRemains.setTextColor(Color.parseColor(item.colorDark))

            cardView.setCardBackgroundColor(Color.parseColor(item.colorLight))
            cardView.strokeColor = Color.parseColor(item.colorDark)
            imgProfile.strokeColor = ColorStateList.valueOf(Color.parseColor(item.colorDark))

            Glide.with(imgProfile.context).load(item.imageUri).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(com.Yogify.birthdayreminder.R.drawable.ic_profile_demo).into(imgProfile)

            cardView.setOnClickListener {
                mlistner.onItemClick(1, item)
            }
        }

        holder.bindingGrid.apply {
            txtName.text = "${item?.name}"
            txtWish.text = "${item?.wish}"
            txtDateTime.text = longToDate(item?.date!!, DATE_dd_MMMM)
            var remainingDays = calculateRemainDays(item.date!!)
            if (remainingDays == 0) {
                lottie.visibility = View.VISIBLE
                lottie.setAnimation(R.raw.celebration)
            } else lottie.visibility = View.GONE
            txtDayRemains.text = remainDaysformate(imgProfile.context, remainingDays)

            txtName.setTextColor(Color.parseColor(item.colorDark))
            txtWish.setTextColor(Color.parseColor(item.colorDark))
            txtDateTime.setTextColor(Color.parseColor(item.colorDark))
            txtDayRemains.setTextColor(Color.parseColor(item.colorDark))

            cardView.setCardBackgroundColor(Color.parseColor(item.colorLight))
            cardView.strokeColor = Color.parseColor(item.colorDark)
            imgProfile.strokeColor = ColorStateList.valueOf(Color.parseColor(item.colorDark))

            Glide.with(imgProfile.context).load(item.imageUri).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(com.Yogify.birthdayreminder.R.drawable.ic_profile_demo).into(imgProfile)



            cardView.setOnClickListener {
                mlistner.onItemClick(1, item)
            }
        }


    }

    fun changeLayout(viewType: Int) {
        layoutType = viewType
    }

    override fun getItemViewType(position: Int): Int {
        return layoutType
    }


}