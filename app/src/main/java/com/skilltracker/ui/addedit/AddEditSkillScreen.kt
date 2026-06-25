package com.skilltracker.ui.addedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
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
import androidx.compose.ui.unit.dp
import com.skilltracker.ui.components.ColorPicker
import com.skilltracker.viewmodel.AddEditViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSkillScreen(
    viewModel: AddEditViewModel,
    onNavigateBack: () -> Unit
) {
    val name by viewModel.skillName.collectAsState()
    val value by viewModel.skillValue.collectAsState()
    val color by viewModel.skillColor.collectAsState()
    val strengths by viewModel.strengths.collectAsState()
    val improvements by viewModel.improvements.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Skill" else "New Skill") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = name,
                    onValueChange = { viewModel.updateName(it) },
                    label = { Text("Skill Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Value: $value",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(
                            value = value.toFloat(),
                            onValueChange = { viewModel.updateValue(it.toInt()) },
                            valueRange = 0f..100f,
                            steps = 99
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Color",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ColorPicker(
                            selectedColor = color,
                            onColorSelected = { viewModel.updateColor(it) }
                        )
                    }
                }
            }

            item {
                StringListEditor(
                    title = "Strengths",
                    items = strengths,
                    onAdd = { viewModel.addStrength(it) },
                    onRemove = { viewModel.removeStrength(it) }
                )
            }

            item {
                StringListEditor(
                    title = "Areas to Improve",
                    items = improvements,
                    onAdd = { viewModel.addImprovement(it) },
                    onRemove = { viewModel.removeImprovement(it) }
                )
            }

            item {
                Button(
                    onClick = { viewModel.saveSkill(onNavigateBack) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = name.isNotBlank()
                ) {
                    Text(
                        text = if (isEditing) "Save Changes" else "Create Skill",
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StringListEditor(
    title: String,
    items: List<String>,
    onAdd: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
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
                    IconButton(onClick = { onRemove(item) }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Remove"
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
