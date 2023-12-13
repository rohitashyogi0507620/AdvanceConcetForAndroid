package com.Yogify.birthdayreminder.ui

import android.R
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.databinding.LayoutReminderGridBinding
import com.Yogify.birthdayreminder.model.ReminderItem
import com.Yogify.birthdayreminder.util.utils
import com.Yogify.birthdayreminder.util.utils.Companion.DATE_dd_MMMM
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class ReminderAdpter : RecyclerView.Adapter<ReminderAdpter.ViewHolder>() {


    lateinit var mlistner: OnItemClickListner
    lateinit var mlonglistner: OnItemLongClickListner


    interface OnItemClickListner {
        fun onItemClick(view: View, type: Int, item: ReminderItem)
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
            txtDateTime.text = utils.datetoFormate(item?.date!!, DATE_dd_MMMM)

            txtName.setTextColor(Color.parseColor(item.colorDark))
            txtWish.setTextColor(Color.parseColor(item.colorDark))
            txtDateTime.setTextColor(Color.parseColor(item.colorDark))

            cardView.setCardBackgroundColor(Color.parseColor(item.colorLight))
            cardView.strokeColor = Color.parseColor(item.colorDark)
            imgProfile.strokeColor = ColorStateList.valueOf(Color.parseColor(item?.colorDark))
            imgDelete.setColorFilter(Color.parseColor(item?.colorDark))


//            Glide.with(imgProfile.context).asBitmap().load("https://drive.google.com/uc?id=1T-GhZwnKo2kefgKVV6zImrb341qRIkTs&export=download")
//                .error(com.Yogify.birthdayreminder.R.drawable.ic_profile_demo).centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                .listener(object : RequestListener<Bitmap?> {
//                    override fun onLoadFailed(
//                        e: GlideException?,
//                        model: Any?,
//                        target: Target<Bitmap?>?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        Log.d("IMAGEERROR",e.toString())
//                        return false
//                    }
//
//                    override fun onResourceReady(
//                        resource: Bitmap?,
//                        model: Any?,
//                        target: Target<Bitmap?>?,
//                        dataSource: DataSource?,
//                        isFirstResource: Boolean
//                    ): Boolean {
//                        imgProfile.setImageBitmap(resource)
//                        return true
//                    }
//                }).submit()


            Glide.with(imgProfile.context).load(item.imageUri).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .error(com.Yogify.birthdayreminder.R.drawable.ic_profile_demo).into(imgProfile)


            imgDelete.setOnClickListener {
                mlistner.onItemClick(it, 1, item)
            }
        }


    }
}