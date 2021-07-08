package com.projects.aldajo92.jetsonbotunal.ui.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.projects.aldajo92.jetsonbotunal.databinding.FragmentDataListBinding
import com.projects.aldajo92.jetsonbotunal.ui.data.adapter.DataListAdapter
import com.projects.aldajo92.jetsonbotunal.ui.data.adapter.DataModel

class DataListFragment : Fragment() {

    lateinit var binding: FragmentDataListBinding

    private val dataListAdapter by lazy { DataListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDataListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerViewData.adapter = dataListAdapter
        binding.recyclerViewData.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        val list = listOf(
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel(),
            DataModel()
        )

        dataListAdapter.setItems(list)

    }
}