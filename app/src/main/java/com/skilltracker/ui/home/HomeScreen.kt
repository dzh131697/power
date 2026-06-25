package com.skilltracker.ui.home

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.skilltracker.domain.model.Skill
import com.skilltracker.export.ExportManager
import com.skilltracker.export.ImportManager
import com.skilltracker.ui.charts.SkillBarChart
import com.skilltracker.ui.charts.SkillRadarChart
import com.skilltracker.ui.charts.SkillTrendChart
import com.skilltracker.ui.components.SkillInfoDialog
import com.skilltracker.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (String) -> Unit
) {
    val skills by viewModel.skills.collectAsState()
    val history by viewModel.selectedSkillHistory.collectAsState()
    val trendSkillId by viewModel.trendSkillId.collectAsState()

    val context = LocalContext.current
    val exportManager = remember { ExportManager(context) }
    val importManager = remember { ImportManager(context) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }
    var showDeleteConfirm by remember { mutableStateOf<Skill?>(null) }

    val tabs = listOf("Bar", "Radar", "Trend")

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            val success = exportManager.exportToJson(skills, it)
            Toast.makeText(
                context,
                if (success) "Exported successfully" else "Export failed",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            val data = importManager.importFromJson(it)
            if (data != null) {
                viewModel.importSkills(data.skills)
                Toast.makeText(context, "Imported ${data.skills.size} skills", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(context, "Import failed: invalid file", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SkillTracker") },
                actions = {
                    IconButton(onClick = { exportLauncher.launch("SkillTracker.json") }) {
                        Icon(Icons.Default.Share, contentDescription = "Export")
                    }
                    IconButton(onClick = { importLauncher.launch(arrayOf("application/json")) }) {
                        Icon(Icons.Default.FileOpen, contentDescription = "Import")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Skill")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .padding(8.dp)
            ) {
                when (selectedTab) {
                    0 -> SkillBarChart(
                        skills = skills,
                        onSkillLongPressed = { selectedSkill = it },
                        modifier = Modifier.fillMaxSize()
                    )
                    1 -> SkillRadarChart(
                        skills = skills,
                        onSkillLongPressed = { selectedSkill = it },
                        modifier = Modifier.fillMaxSize()
                    )
                    2 -> TrendTab(
                        skills = skills,
                        history = history,
                        trendSkillId = trendSkillId,
                        onSkillSelected = { viewModel.loadHistoryForSkill(it) },
                        onPointLongPressed = {
                            val skill = skills.find { it.id == trendSkillId }
                            if (skill != null) selectedSkill = skill
                        }
                    )
                }

                if (skills.isEmpty()) {
                    Text(
                        text = "Tap + to add your first skill",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Text(
                text = "Skills (${skills.size})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(skills, key = { it.id }) { skill ->
                    SkillListItem(
                        skill = skill,
                        onClick = { onNavigateToDetail(skill.id) },
                        onLongClick = { selectedSkill = skill },
                        onDelete = { showDeleteConfirm = skill }
                    )
                }
            }
        }
    }

    selectedSkill?.let { skill ->
        SkillInfoDialog(skill = skill, onDismiss = { selectedSkill = null })
    }

    showDeleteConfirm?.let { skill ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Skill") },
            text = { Text("Delete \"${skill.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSkill(skill.id)
                    showDeleteConfirm = null
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendTab(
    skills: List<Skill>,
    history: List<com.skilltracker.domain.model.SkillHistory>,
    trendSkillId: String?,
    onSkillSelected: (String) -> Unit,
    onPointLongPressed: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedSkill = skills.find { it.id == trendSkillId }

    if (skills.isEmpty()) return

    Column(modifier = Modifier.fillMaxSize()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedSkill?.name ?: "Select a skill",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                textStyle = MaterialTheme.typography.bodyMedium
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                skills.forEach { skill ->
                    DropdownMenuItem(
                        text = { Text(skill.name) },
                        onClick = {
                            onSkillSelected(skill.id)
                            expanded = false
                        }
                    )
                }
            }
        }

        if (selectedSkill != null) {
            SkillTrendChart(
                skillName = selectedSkill.name,
                history = history,
                currentSkillValue = selectedSkill.value,
                chartColor = selectedSkill.color,
                onPointLongPressed = onPointLongPressed,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SkillListItem(
    skill: Skill,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color(skill.color))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = skill.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { skill.value / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(MaterialTheme.shapes.small),
                    color = Color(skill.color),
                    trackColor = Color(skill.color).copy(alpha = 0.15f)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${skill.value}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}
