package com.skilltracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(
    selectedColor: Long,
    onColorSelected: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        0xFFF44336L, 0xFFE91E63L, 0xFF9C27B0L, 0xFF673AB7L,
        0xFF3F51B5L, 0xFF2196F3L, 0xFF03A9F4L, 0xFF00BCD4L,
        0xFF009688L, 0xFF4CAF50L, 0xFF8BC34AL, 0xFFCDDC39L,
        0xFFFFEB3BL, 0xFFFFC107L, 0xFFFF9800L, 0xFFFF5722L,
        0xFF795548L, 0xFF9E9E9EL, 0xFF607D8BL
    )

    FlowRow(
        modifier = modifier.padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { color ->
            val isSelected = color == selectedColor
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(color))
                    .then(
                        if (isSelected) {
                            Modifier.border(3.dp, Color.White, CircleShape)
                        } else {
                            Modifier.border(1.dp, Color.Gray.copy(alpha = 0.3f), CircleShape)
                        }
                    )
                    .clickable { onColorSelected(color) }
            )
        }
    }
}
