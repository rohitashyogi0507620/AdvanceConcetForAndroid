package com.Yogify.birthdayreminder.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.Yogify.birthdayreminder.databinding.LayoutColorItemBinding
import com.Yogify.birthdayreminder.model.ThemeColor


class ColorAdpter : RecyclerView.Adapter<ColorAdpter.ViewHolder>() {

    private var selectedPosition = 0

    val _themeColor = MutableLiveData<ThemeColor>()
    val themeColor = _themeColor

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
        _themeColor.postValue(differ.currentList[selectedPosition])

        if (selectedPosition === position) {
            holder.itemView.isSelected = true
        } else {
            holder.itemView.isSelected = false
        }
        holder.binding.cardColor.setOnClickListener {
            if (selectedPosition >= 0) notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            _themeColor.postValue(differ.currentList[selectedPosition])
            notifyItemChanged(selectedPosition)
        }

    }

    fun autoSetColor(lightColor: String, darkColor: String) {
        differ.currentList.forEachIndexed { index, themeColor ->
            if (themeColor.colorLight.equals(lightColor) && themeColor.colorDark.equals(darkColor)) {
                selectedPosition = index
                if (selectedPosition >= 0) notifyItemChanged(selectedPosition)
                _themeColor.postValue(differ.currentList[selectedPosition])
                notifyItemChanged(selectedPosition)
            }
        }

    }

}