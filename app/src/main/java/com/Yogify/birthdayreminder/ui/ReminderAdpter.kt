package com.Yogify.birthdayreminder.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.databinding.LayoutReminderGridBinding
import com.Yogify.birthdayreminder.databinding.LayoutReminderListBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ReminderAdpter : RecyclerView.Adapter<ReminderAdpter.ViewHolder>() {


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
    inner class ViewHolder(val binding: LayoutReminderGridBinding) :
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
            LayoutReminderGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.binding.apply {
            txtName.text = "${item?.name}"
            txtWish.text = "${item?.wish}"
            txtDateTime.text = utils.datetoFormate(item?.date!!)
            Glide.with(imgProfile.context).load(item?.imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).thumbnail(0.5f).into(imgProfile)
            txtName.setTextColor(Color.parseColor(item?.colorDark))
            txtWish.setTextColor(Color.parseColor(item?.colorDark))
            txtDateTime.setTextColor(Color.parseColor(item?.colorDark))

            cardView.setCardBackgroundColor(Color.parseColor(item?.colorLight))
            cardView.strokeColor= Color.parseColor(item?.colorDark)
            imgProfile.strokeColor = ColorStateList.valueOf(Color.parseColor(item?.colorDark))
            imgDelete.setColorFilter(Color.parseColor(item?.colorDark))




            imgDelete.setOnClickListener {
                mlistner.onItemClick(item!!)
            }
        }


    }
}