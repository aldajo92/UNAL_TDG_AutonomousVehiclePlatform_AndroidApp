package com.projects.aldajo92.jetsonbotunal.ui.data.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ItemHolder(val view: View): RecyclerView.ViewHolder(view) {

    private lateinit var dataModel: DataModel

    fun bindData(dataModel : DataModel){
        this.dataModel = dataModel
    }

}