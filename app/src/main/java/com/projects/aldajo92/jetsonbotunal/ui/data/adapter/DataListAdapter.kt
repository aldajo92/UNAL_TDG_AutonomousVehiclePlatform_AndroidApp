package com.projects.aldajo92.jetsonbotunal.ui.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.projects.aldajo92.jetsonbotunal.R
import com.projects.aldajo92.jetsonbotunal.databinding.ItemDataBinding

class DataListAdapter : RecyclerView.Adapter<ItemHolder>() {

    private var dataImageModelList: List<DataImageModel> = emptyList()

    fun setItems(list: List<DataImageModel>) {
        dataImageModelList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ItemHolder(
            ItemDataBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bindData(dataImageModelList[position])
    }

    override fun getItemCount() = dataImageModelList.size
}

