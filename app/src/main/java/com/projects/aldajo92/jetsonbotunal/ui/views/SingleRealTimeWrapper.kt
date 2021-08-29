package com.projects.aldajo92.jetsonbotunal.ui.views

import android.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class SingleRealTimeWrapper(
    private val chart: LineChart,
    private val colorLines: Int,
    private val description: String
) {

    fun configureChart() {
        chart.description.isEnabled = true
        chart.description.textColor = Color.WHITE
        chart.description.text = description

        // touch gestures
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setDrawGridBackground(false)

        chart.setPinchZoom(false)
        chart.isScaleXEnabled = false
        chart.isScaleYEnabled = false

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true)

        val data = LineData()
        data.setValueTextColor(Color.WHITE)

        // add empty data
        chart.data = data

        chart.legend.isEnabled = false

        val xl: XAxis = chart.xAxis
        xl.enableGridDashedLine(10f, 10f, 0f)
        xl.position = XAxis.XAxisPosition.BOTH_SIDED
        xl.setDrawLabels(false)
        xl.granularity = 1f

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.granularity = 0.2f
        leftAxis.setDrawGridLines(true)

        val rightAxis: YAxis = chart.axisRight
        rightAxis.isEnabled = true
        leftAxis.setDrawGridLines(false)
        rightAxis.setDrawLabels(false)
    }

    fun addEntry(y: Float) {
        val data: LineData? = chart.data
        if (data != null) {
            // Here move to for
            var set = data.getDataSetByIndex(0)
            if (set == null) {
                set = createSet(colorLines)
                data.addDataSet(set)
            }
            data.addEntry(Entry(set.entryCount.toFloat(), y), 0)
            data.notifyDataChanged()

            // let the chart know it's data has changed
            chart.notifyDataSetChanged()

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(50F)
            // chart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            chart.moveViewToX(data.entryCount.toFloat())
        }
    }

    private fun createSet(colorLine: Int): LineDataSet {
        val set = LineDataSet(null, "Data 1")
        set.axisDependency = AxisDependency.LEFT
        set.color = colorLine
        set.setCircleColor(colorLine)
        set.lineWidth = 2f
        set.setCircleColorHole(colorLine)
        set.circleRadius = 2f
        set.fillColor = colorLine
        set.highLightColor = Color.rgb(244, 117, 117)
        set.valueTextColor = Color.WHITE
        set.valueTextSize = 9f
        set.mode = LineDataSet.Mode.STEPPED
        set.setDrawValues(false)
        return set
    }

    companion object {
        fun getInstance(chart: LineChart, colorLines: Int, description: String) =
            SingleRealTimeWrapper(chart, colorLines, description).apply {
                configureChart()
            }
    }

}
