package com.internet.unetspeedmeter.adapter



import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.internet.unetspeedmeter.R
import com.internet.unetspeedmeter.data.InternetDataItem
import com.internet.unetspeedmeter.databinding.ItemInternetBinding
import com.internet.unetspeedmeter.math.kbToString


class HomeAdapter : ListAdapter<InternetDataItem, HomeAdapter.HomeViewHolder>(DiffCallback()){
    lateinit var context:Context

    inner class HomeViewHolder(
        private val binding: ItemInternetBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(itemModel: InternetDataItem) {

            binding.textViewDate.text =  context.getString(R.string.textview_item_date,itemModel.time)
            binding.textViewDownloadData.text = context.getString(R.string.textview_item_download_data,kbToString(itemModel.byteReceived))
            binding.textViewUploadData.text = context.getString(R.string.textview_item_upload_data,kbToString(itemModel.byteSend))


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        return HomeViewHolder(ItemInternetBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))

    }



}

class DiffCallback : DiffUtil.ItemCallback<InternetDataItem>() {
    override fun areItemsTheSame(oldItem: InternetDataItem, newItem: InternetDataItem): Boolean {
        return oldItem.time == newItem.time
    }

    override fun areContentsTheSame(oldItem: InternetDataItem, newItem: InternetDataItem): Boolean {
        return oldItem == newItem
    }
}