package com.elkiplangat.barcodescanner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import com.elkiplangat.barcodescanner.barcode.BarCode
import kotlinx.android.synthetic.main.barcode_history_item.view.*

class HistoryDiffCallBack: DiffUtil.ItemCallback<BarCode>() {
    override fun areItemsTheSame(oldItem: BarCode, newItem: BarCode): Boolean {
        return newItem == oldItem
    }

    override fun areContentsTheSame(oldItem: BarCode, newItem: BarCode): Boolean {
        return newItem == oldItem
    }

}
class BarcodeHistoryRecyclerAdapter:ListAdapter<BarCode, BarcodeHistoryRecyclerAdapter.HistoryViewHolder>(HistoryDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.barcode_history_item, parent, false)
        return HistoryViewHolder(view)
    }

       override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.textViewDate.text = item.timeTaken.toString()
        holder.textViewType.text = item.type.toString()
        holder.textViewValue.text = item.value

        //holder.rootview.tag = item
    }


    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val deleteIcon = itemView.deleteIcon
        val textViewType = itemView.textViewType
        val textViewValue = itemView.textViewValue
        val textViewDate = itemView.textViewDate

    }
}