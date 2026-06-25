package com.skilltracker.ui.charts

import android.graphics.Color
import android.view.MotionEvent
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.skilltracker.domain.model.SkillHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SkillTrendChart(
    skillName: String,
    history: List<SkillHistory>,
    currentSkillValue: Int,
    chartColor: Long = 0xFF2196F3,
    modifier: Modifier = Modifier,
    onPointLongPressed: () -> Unit = {}
) {
    if (history.isEmpty()) return

    val sortedHistory = remember(history) { history.sortedBy { it.timestamp } }
    val dateFormat = remember { SimpleDateFormat("MM/dd", Locale.getDefault()) }

    val totalDelta = sortedHistory.sumOf { it.delta }
    val initialValue = currentSkillValue - totalDelta

    val cumulativeValues = remember(sortedHistory, currentSkillValue) {
        var running = initialValue
        sortedHistory.map { h ->
            running += h.delta
            running.toFloat()
        }
    }

    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true
                legend.textSize = 12f
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(false)
                setPinchZoom(false)
                animateX(800)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textSize = 11f
                    labelRotationAngle = -45f
                }

                axisLeft.apply {
                    axisMinimum = 0f
                    axisMaximum = 100f
                    textSize = 11f
                }
                axisRight.isEnabled = false

                setOnChartGestureListener(object : OnChartGestureListener {
                    override fun onChartGestureStart(
                        me: MotionEvent?,
                        lastPerformedGesture: ChartTouchListener.ChartGesture?
                    ) {}
                    override fun onChartGestureEnd(
                        me: MotionEvent?,
                        lastPerformedGesture: ChartTouchListener.ChartGesture?
                    ) {}
                    override fun onChartLongPressed(me: MotionEvent?) {
                        onPointLongPressed()
                    }
                    override fun onChartDoubleTapped(me: MotionEvent?) {}
                    override fun onChartSingleTapped(me: MotionEvent?) {}
                    override fun onChartFling(
                        me1: MotionEvent?,
                        me2: MotionEvent?,
                        velocityX: Float,
                        velocityY: Float
                    ) {}
                    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
                    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {}
                })
            }
        },
        update = { chart ->
            val entries = cumulativeValues.mapIndexed { index, value ->
                Entry(index.toFloat(), value)
            }
            val dataSet = LineDataSet(entries, skillName).apply {
                color = chartColor.toInt()
                setCircleColor(chartColor.toInt())
                lineWidth = 2.5f
                circleRadius = 4f
                setDrawCircleHole(false)
                setDrawValues(false)
                mode = LineDataSet.Mode.CUBIC_BEZIER
                cubicIntensity = 0.15f
                setDrawFilled(true)
                fillColor = chartColor.toInt()
                fillAlpha = 30
            }
            chart.data = LineData(dataSet)
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val index = value.toInt()
                    return if (index in sortedHistory.indices) {
                        dateFormat.format(Date(sortedHistory[index].timestamp))
                    } else ""
                }
            }
            chart.xAxis.labelCount = sortedHistory.size.coerceAtMost(8)
            chart.invalidate()
        },
        modifier = modifier.height(250.dp)
    )
}
