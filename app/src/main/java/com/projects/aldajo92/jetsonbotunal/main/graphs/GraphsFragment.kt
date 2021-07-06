package com.projects.aldajo92.jetsonbotunal.main.graphs

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.utils.ColorTemplate
import com.projects.aldajo92.jetsonbotunal.R
import com.projects.aldajo92.jetsonbotunal.main.MainActivity
import com.projects.aldajo92.jetsonbotunal.main.MainViewModel
import com.projects.aldajo92.jetsonbotunal.view.SingleRealTimeWrapper
import kotlinx.android.synthetic.main.fragment_graphs.view.lineChart_input
import kotlinx.android.synthetic.main.fragment_graphs.view.lineaChart_output

class GraphsFragment : Fragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var lineChartOutput: SingleRealTimeWrapper

    private lateinit var lineChartInput: SingleRealTimeWrapper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_graphs, container, false)

        lineChartOutput =
            SingleRealTimeWrapper.getInstance(
                view.lineaChart_output,
                Color.rgb(200, 200, 200)
            )

        lineChartInput = SingleRealTimeWrapper.getInstance(
            view.lineChart_input,
            ColorTemplate.getHoloBlue()
        )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.velocityLiveData.observe(viewLifecycleOwner, { velocityEncoder ->
            velocityEncoder?.let {
                lineChartOutput.addEntry(velocityEncoder.velocityEncoder)
                lineChartInput.addEntry(velocityEncoder.input)
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance() = GraphsFragment()
    }
}