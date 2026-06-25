package com.skilltracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ValueControls(
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Quick Adjust",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            listOf(-10, -5, -1).forEach { delta ->
                FilledTonalButton(onClick = { onValueChange(delta) }) {
                    Text(text = "$delta")
                }
            }
            listOf(1, 5, 10).forEach { delta ->
                FilledTonalButton(onClick = { onValueChange(delta) }) {
                    Text(text = "+$delta")
                }
            }
        }
    }
}
