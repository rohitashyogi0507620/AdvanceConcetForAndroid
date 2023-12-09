package com.Yogify.birthdayreminder.ui

import android.R
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.databinding.LayoutColorItemBinding
import com.Yogify.birthdayreminder.model.ThemeColor


class ColorAdpter : RecyclerView.Adapter<ColorAdpter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(val binding: LayoutColorItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<ThemeColor>() {
        override fun areItemsTheSame(oldItem: ThemeColor, newItem: ThemeColor): Boolean {
            return oldItem.colorLight == newItem.colorLight
        }

        override fun areContentsTheSame(oldItem: ThemeColor, newItem: ThemeColor): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<ThemeColor>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutColorItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.binding.cardColor.setCardBackgroundColor(Color.parseColor(item?.colorLight))
        holder.binding.cardColor.strokeColor = Color.parseColor(item?.colorDark)

        if (selectedPosition === position) {
            holder.itemView.isSelected = true
        } else {
            holder.itemView.isSelected = false
        }
        holder.binding.cardColor.setOnClickListener {
            if (selectedPosition >= 0) notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
        }

    }

    fun getItem():ThemeColor{
        return differ.currentList[selectedPosition]
    }
}