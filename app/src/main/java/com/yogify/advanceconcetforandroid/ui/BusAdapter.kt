package com.yogify.advanceconcetforandroid.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.yogify.advanceconcetforandroid.databinding.BusstopItemBinding
import com.yogify.advanceconcetforandroid.models.BusStop

class BusAdapter : RecyclerView.Adapter<BusAdapter.BusStopViewHolder>() {

    inner class BusStopViewHolder(val binding: BusstopItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<BusStop>() {
        override fun areItemsTheSame(oldItem: BusStop, newItem: BusStop): Boolean {
            return oldItem.sourceId == newItem.sourceId
        }

        override fun areContentsTheSame(oldItem: BusStop, newItem: BusStop): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<BusStop>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusStopViewHolder {
        val binding = BusstopItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BusStopViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BusStopViewHolder, position: Int) {

        val item = differ.currentList[position]

        holder.binding.apply {
            stopName.text = "${item.stopName}"
            stopId.text = "${item.sourceId}"
        }

    }
}