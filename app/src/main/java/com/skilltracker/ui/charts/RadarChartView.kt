package com.skilltracker.ui.charts

import android.graphics.Color
import android.view.MotionEvent
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.skilltracker.domain.model.Skill

@Composable
fun SkillRadarChart(
    skills: List<Skill>,
    modifier: Modifier = Modifier,
    onSkillLongPressed: (Skill) -> Unit = {}
) {
    if (skills.isEmpty()) return

    AndroidView(
        factory = { context ->
            RadarChart(context).apply {
                description.isEnabled = false
                legend.isEnabled = true
                legend.textSize = 12f
                setTouchEnabled(true)
                isRotationEnabled = false
                animateXY(800, 800)

                yAxis.apply {
                    axisMinimum = 0f
                    axisMaximum = 100f
                    setDrawLabels(false)
                    textSize = 10f
                }

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    textSize = 12f
                }

                webLineWidth = 1f
                webColor = Color.LTGRAY
                webLineWidthInner = 1f
                webColorInner = Color.LTGRAY
                webAlpha = 150

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
            val entries = skills.map { skill ->
                RadarEntry(skill.value.toFloat())
            }
            val dataSet = RadarDataSet(entries, "Skills").apply {
                color = Color.argb(200, 76, 175, 80)
                fillColor = Color.argb(80, 76, 175, 80)
                setDrawFilled(true)
                lineWidth = 2f
                valueTextSize = 10f
                valueTextColor = Color.DKGRAY
            }
            chart.data = RadarData(dataSet)
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(skills.map { it.name })
            chart.xAxis.labelCount = skills.size
            chart.invalidate()
        },
        modifier = modifier.height(350.dp)
    )
}
