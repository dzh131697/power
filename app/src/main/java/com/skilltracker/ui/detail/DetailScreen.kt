package com.skilltracker.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.skilltracker.domain.model.Skill
import com.skilltracker.domain.model.SkillHistory
import com.skilltracker.ui.charts.SkillTrendChart
import com.skilltracker.ui.components.ValueControls
import com.skilltracker.viewmodel.DetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    viewModel: DetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit
) {
    val skill by viewModel.skill.collectAsState()
    val history by viewModel.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(skill?.name ?: "Skill Detail") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                }
            )
        }
    ) { paddingValues ->
        skill?.let { currentSkill ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ValueDisplay(currentSkill) }
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        ValueControls(
                            onValueChange = { viewModel.updateValue(it) },
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                item {
                    StrengthsSection(
                        strengths = currentSkill.strengths,
                        onAdd = { viewModel.addStrength(it) },
                        onRemove = { viewModel.removeStrength(it) }
                    )
                }
                item {
                    ImprovementsSection(
                        improvements = currentSkill.improvements,
                        onAdd = { viewModel.addImprovement(it) },
                        onRemove = { viewModel.removeImprovement(it) }
                    )
                }
                item {
                    Text("Growth Trend", style = MaterialTheme.typography.titleMedium)
                }
                item {
                    SkillTrendChart(
                        skillName = currentSkill.name,
                        history = history,
                        currentSkillValue = currentSkill.value,
                        chartColor = currentSkill.color,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (history.isEmpty()) {
                    item {
                        Text(
                            text = "No history yet. Use the controls above to track progress.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                items(history, key = { it.id }) { entry ->
                    HistoryItem(entry)
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun ValueDisplay(skill: Skill) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(skill.color))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            Text(
                text = "${skill.value}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun StrengthsSection(
    strengths: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    StringListSection(
        title = "Strengths",
        items = strengths,
        onAdd = onAdd,
        onRemove = onRemove,
        titleColor = MaterialTheme.colorScheme.secondary
    )
}

@Composable
private fun ImprovementsSection(
    improvements: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    StringListSection(
        title = "Areas to Improve",
        items = improvements,
        onAdd = onAdd,
        onRemove = onRemove,
        titleColor = MaterialTheme.colorScheme.tertiary
    )
}

@Composable
private fun StringListSection(
    title: String,
    items: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit,
    titleColor: Color = Color.Unspecified
) {
    var text by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            items.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\u2022 $item",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onRemove(item) }, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Add item") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                IconButton(onClick = {
                    if (text.isNotBlank()) {
                        onAdd(text.trim())
                        text = ""
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(entry: SkillHistory) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()) }
    val isPositive = entry.delta >= 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = dateFormat.format(Date(entry.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!entry.reason.isNullOrBlank()) {
                Text(
                    text = entry.reason,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Text(
            text = if (isPositive) "+${entry.delta}" else "${entry.delta}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPositive) Color(0xFF4CAF50) else Color(0xFFF44336)
        )
    }
}
