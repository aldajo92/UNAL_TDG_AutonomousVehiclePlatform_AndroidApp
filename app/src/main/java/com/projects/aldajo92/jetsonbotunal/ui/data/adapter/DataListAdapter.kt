package com.projects.aldajo92.jetsonbotunal.ui.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.aldajo92.jetsonbotunal.R

class DataListAdapter : RecyclerView.Adapter<ItemHolder>() {

    private lateinit var dataModelList: List<DataModel>

    fun setItems(list: List<DataModel>) {
        dataModelList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_data, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bindData(dataModelList[position])
    }

    override fun getItemCount() = dataModelList.size
}

