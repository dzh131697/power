package com.skilltracker.ui.charts

import android.graphics.Color
import android.view.MotionEvent
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.skilltracker.domain.model.Skill

@Composable
fun SkillBarChart(
    skills: List<Skill>,
    modifier: Modifier = Modifier,
    onSkillLongPressed: (Skill) -> Unit = {}
) {
    if (skills.isEmpty()) return

    AndroidView(
        factory = { context ->
            BarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true
                legend.textSize = 12f
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(false)
                setPinchZoom(false)
                setDrawBarShadow(false)
                setDrawValueAboveBar(true)
                setFitBars(true)
                animateY(800)

                xAxis.apply {
                    position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    textSize = 11f
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
                    override fun onChartLongPressed(me: MotionEvent) {
                        val highlight = getHighlightByTouchPoint(me.x, me.y)
                        if (highlight != null) {
                            val index = highlight.x.toInt()
                            val currentSkills = tag as? List<*> ?: return
                            if (index in currentSkills.indices) {
                                @Suppress("UNCHECKED_CAST")
                                (currentSkills as? List<Skill>)?.get(index)
                                    ?.let { onSkillLongPressed(it) }
                            }
                        }
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
            chart.tag = skills
            val entries = skills.mapIndexed { index, skill ->
                BarEntry(index.toFloat(), skill.value.toFloat())
            }
            val dataSet = BarDataSet(entries, "Skills").apply {
                colors = skills.map { it.color.toInt() }
                valueTextSize = 12f
                valueTextColor = Color.DKGRAY
            }
            chart.data = BarData(dataSet).apply { barWidth = 0.6f }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(skills.map { it.name })
            chart.xAxis.labelCount = skills.size
            chart.invalidate()
        },
        modifier = modifier.height(300.dp)
    )
}
