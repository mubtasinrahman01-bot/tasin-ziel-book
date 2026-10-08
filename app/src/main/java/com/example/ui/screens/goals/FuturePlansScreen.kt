package com.example.ui.screens.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.GoalEntity
import com.example.data.model.GoalStatus
import com.example.ui.components.StylizedEmptyStateView
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.Gray600
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.GoalFilter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun FuturePlansScreen(
    goals: List<GoalEntity>,
    allGoals: List<GoalEntity>,
    goalFilter: GoalFilter,
    onFilterChanged: (GoalFilter) -> Unit,
    onStatusChanged: (Long, GoalStatus) -> Unit,
    onDeleteGoal: (GoalEntity) -> Unit,
    showAddDialog: Boolean,
    onSetShowAddDialog: (Boolean) -> Unit,
    onAddGoal: (String, String, Boolean, String, String) -> Unit,
    onUpdateGoal: ((GoalEntity) -> Unit)? = null,
    onBackToHome: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val totalCount = allGoals.size
    val completedCount = allGoals.count { it.status == GoalStatus.DONE }
    val incompleteCount = allGoals.count { it.status == GoalStatus.INCOMPLETE }
    val progressRatio = if (totalCount > 0) completedCount.toFloat() / totalCount.toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progressRatio, label = "goal_progress")

    // State for viewing full details modal
    var selectedGoalForDetail by remember { mutableStateOf<GoalEntity?>(null) }
    var goalToEdit by remember { mutableStateOf<GoalEntity?>(null) }
    var goalToDeleteConfirm by remember { mutableStateOf<GoalEntity?>(null) }

    Box(modifier = modifier.fillMaxSize().background(PureBlack)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("future_plans_list"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sub-page Top Bar with clear Back Button [←]
            if (onBackToHome != null) {
                item {
                    SubPageTopBar(
                        title = "Future Plans",
                        subtitle = "ভবিষ্যতের পরিকল্পনা ও লক্ষ্য",
                        iconRes = R.drawable.ic_line_chart_growth,
                        onBack = onBackToHome
                    )
                }
            }

            // Header stats summary card featuring the Line Chart growth icon
            item {
                GoalSummaryCard(
                    totalCount = totalCount,
                    completedCount = completedCount,
                    incompleteCount = incompleteCount,
                    progress = animatedProgress
                )
            }

            // Filter Tabs (All / Short-term / Long-term)
            item {
                GoalFilterTabs(
                    selectedFilter = goalFilter,
                    onFilterSelected = onFilterChanged
                )
            }

            // Header label showing total count
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PLANS LIST (${goals.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Gray500
                        )
                    )
                    Text(
                        text = "Tap a plan for full details",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Gray600
                        )
                    )
                }
            }

            // Numbered Minimal Cards: Display each plan as a sleek, compact card showing ONLY
            // a numbered title in bold font (e.g. "1. বই পড়া ও গবেষণা") along with a right arrow icon [➔]
            if (goals.isEmpty()) {
                item {
                    StylizedEmptyStateView(
                        painter = painterResource(id = R.drawable.ic_line_chart_growth),
                        title = "No Plans in this category",
                        subtitle = "নতুন পরিকল্পনা তৈরি করতে নিচের + বাটনে চাপুন।",
                        actionButtonText = "Create New Plan",
                        onActionClick = { onSetShowAddDialog(true) }
                    )
                }
            } else {
                itemsIndexed(goals, key = { _, goal -> goal.id }) { index, goal ->
                    // Calculate 1-based sequential number for clean display
                    val sequentialNumber = index + 1
                    NumberedPlanCard(
                        sequentialNumber = sequentialNumber,
                        goal = goal,
                        onClick = { selectedGoalForDetail = goal }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }

        // Floating Action Button (+) with crisp square styling
        FloatingActionButton(
            onClick = { onSetShowAddDialog(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_goal_fab"),
            shape = RoundedCornerShape(12.dp),
            containerColor = PureWhite,
            contentColor = PureBlack
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add New Plan",
                modifier = Modifier.size(24.dp)
            )
        }
    }

    // Detail Modal Dialog on click
    selectedGoalForDetail?.let { currentGoal ->
        GoalDetailDialog(
            goal = currentGoal,
            onDismiss = { selectedGoalForDetail = null },
            onStatusChanged = { newStatus ->
                onStatusChanged(currentGoal.id, newStatus)
                selectedGoalForDetail = currentGoal.copy(status = newStatus)
            },
            onEdit = {
                selectedGoalForDetail = null
                goalToEdit = currentGoal
            },
            onDelete = {
                selectedGoalForDetail = null
                goalToDeleteConfirm = currentGoal
            }
        )
    }

    // Edit Goal Dialog
    goalToEdit?.let { existingGoal ->
        EditGoalDialog(
            goal = existingGoal,
            onDismiss = { goalToEdit = null },
            onConfirm = { updatedGoal ->
                onUpdateGoal?.invoke(updatedGoal)
                goalToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    goalToDeleteConfirm?.let { goalToDelete ->
        AlertDialog(
            onDismissRequest = { goalToDeleteConfirm = null },
            title = {
                Text(
                    text = "Delete Plan / পরিকল্পনা মুছুন",
                    color = PureWhite,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "\"${goalToDelete.title}\" পরিকল্পনাটি কি মুছে ফেলতে চান?",
                    color = Gray300
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteGoal(goalToDelete)
                        goalToDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PureWhite,
                        contentColor = PureBlack
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { goalToDeleteConfirm = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = Gray400)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = DarkCard,
            shape = RoundedCornerShape(12.dp)
        )
    }

    // Add Plan Dialog (calculates next sequential number for placeholder / hint)
    if (showAddDialog) {
        val nextSequentialNumber = allGoals.size + 1
        AddGoalDialog(
            nextPlanNumber = nextSequentialNumber,
            onDismiss = { onSetShowAddDialog(false) },
            onConfirm = onAddGoal
        )
    }
}

/**
 * Numbered Minimal Card:
 * - Sleek, compact card height
 * - Shows ONLY a numbered title in bold font (e.g., "1. বই পড়া ও গবেষণা", "2. আমার ভবিষ্যৎ ক্যারিয়ার")
 * - Right arrow icon [➔] on the side
 * - Deep black background with clean rounded borders
 * - Hides internal descriptions or sub-texts from main list view for clean, high-density minimal look
 */
@Composable
fun NumberedPlanCard(
    sequentialNumber: Int,
    goal: GoalEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // If title already starts with a number like "1. ", use title directly; else prepend sequential number
    val cleanTitle = remember(goal.title, sequentialNumber) {
        val trimmed = goal.title.trim()
        val regex = Regex("^\\d+[.)\\-]\\s*")
        if (regex.containsMatchIn(trimmed)) {
            trimmed
        } else {
            "$sequentialNumber. $trimmed"
        }
    }

    val isDone = goal.status == GoalStatus.DONE

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("plan_card_${goal.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (isDone) Gray600 else BorderDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Numbered bold title with Bengali and English support
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Status indicator dot or square
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isDone) PureWhite else DarkElevated)
                        .border(1.dp, if (isDone) PureWhite else Gray600, RoundedCornerShape(2.dp))
                )

                Text(
                    text = cleanTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isDone) Gray400 else PureWhite,
                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right arrow icon [➔] on the side
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_right_monochrome),
                contentDescription = "View Details",
                tint = if (isDone) Gray500 else PureWhite,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun GoalSummaryCard(
    totalCount: Int,
    completedCount: Int,
    incompleteCount: Int,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("goal_summary_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Line Chart Growth Icon as primary visual
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkElevated)
                            .border(1.dp, BorderDark, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_line_chart_growth),
                            contentDescription = "Growth Over Time",
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Future Plans & Growth",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        )
                        Text(
                            text = "$completedCount completed • $incompleteCount in progress",
                            style = MaterialTheme.typography.bodySmall.copy(color = Gray400)
                        )
                    }
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = PureWhite,
                trackColor = BorderDark
            )
        }
    }
}

@Composable
fun GoalFilterTabs(
    selectedFilter: GoalFilter,
    onFilterSelected: (GoalFilter) -> Unit
) {
    val tabs = listOf(
        GoalFilter.ALL to "All Plans",
        GoalFilter.SHORT_TERM to "Short-term",
        GoalFilter.LONG_TERM to "Long-term"
    )

    TabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == selectedFilter },
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, BorderDark, RoundedCornerShape(8.dp)),
        containerColor = DarkCard,
        contentColor = PureWhite,
        indicator = { tabPositions ->
            val index = tabs.indexOfFirst { it.first == selectedFilter }
            if (index in tabPositions.indices) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = PureWhite,
                    height = 2.dp
                )
            }
        }
    ) {
        tabs.forEach { (filter, title) ->
            val isSelected = selectedFilter == filter
            Tab(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                text = {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) PureWhite else Gray400
                    )
                },
                modifier = Modifier.testTag("filter_tab_${filter.name.lowercase()}")
            )
        }
    }
}

/**
 * Full detail modal on plan click:
 * - View full details (title, category, horizon, target date, sub-tasks/notes)
 * - Status toggle [✓ Done], [✗ Incomplete], [Reset]
 * - Edit and Delete actions
 */
@Composable
fun GoalDetailDialog(
    goal: GoalEntity,
    onDismiss: () -> Unit,
    onStatusChanged: (GoalStatus) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isDone = goal.status == GoalStatus.DONE
    val isIncomplete = goal.status == GoalStatus.INCOMPLETE

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = DarkCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header Bar: Chips & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkElevated)
                                .border(1.dp, BorderDark, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (goal.isLongTerm) "Long-Term" else "Short-Term",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PureWhite
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkElevated)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = goal.category,
                                fontSize = 11.sp,
                                color = Gray300
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("goal_detail_close")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Full Title
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Target Date row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Target Date",
                            tint = Gray400,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Target Date: ${goal.targetDate}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Gray400,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(BorderDark)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Status Control Block with square buttons
                    Text(
                        text = "PLAN STATUS / বর্তমান অবস্থা",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            color = Gray500,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Square Checkmark [✓] Done button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDone) PureWhite else DarkElevated)
                                .border(
                                    1.5.dp,
                                    if (isDone) PureWhite else BorderDark,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onStatusChanged(GoalStatus.DONE) }
                                .padding(vertical = 10.dp)
                                .testTag("modal_goal_status_done"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done",
                                    tint = if (isDone) PureBlack else PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Done [✓]",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDone) PureBlack else PureWhite,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Square Cross [✗] Incomplete button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isIncomplete) PureWhite else DarkElevated)
                                .border(
                                    1.5.dp,
                                    if (isIncomplete) PureWhite else BorderDark,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onStatusChanged(GoalStatus.INCOMPLETE) }
                                .padding(vertical = 10.dp)
                                .testTag("modal_goal_status_incomplete"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Incomplete",
                                    tint = if (isIncomplete) PureBlack else PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Incomplete [✗]",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isIncomplete) PureBlack else PureWhite,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sub-tasks / Milestones / Notes Section
                    Text(
                        text = "NOTES & SUB-TASKS / বিস্তারিত বিবরণ ও ধাপসমূহ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            color = Gray500,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkElevated),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            if (goal.notes.isNotBlank()) {
                                Text(
                                    text = goal.notes,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Gray300,
                                        lineHeight = 22.sp,
                                        fontSize = 14.sp
                                    )
                                )
                            } else {
                                Text(
                                    text = "No sub-tasks or extra notes added yet. Tap 'Edit' below to add steps.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Gray500,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Actions: Delete and Edit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("goal_detail_delete"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Gray400,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Delete", color = PureWhite, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onEdit,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("goal_detail_edit"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PureWhite,
                            contentColor = PureBlack
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = PureBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit Plan", color = PureBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun EditGoalDialog(
    goal: GoalEntity,
    onDismiss: () -> Unit,
    onConfirm: (GoalEntity) -> Unit
) {
    var title by remember { mutableStateOf(goal.title) }
    var notes by remember { mutableStateOf(goal.notes) }
    var isLongTerm by remember { mutableStateOf(goal.isLongTerm) }
    var category by remember { mutableStateOf(goal.category) }
    var targetDate by remember { mutableStateOf(goal.targetDate) }

    val categories = listOf("Career", "Health", "Finance", "Learning", "Personal", "Business")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Plan / পরিকল্পনা সম্পাদনা",
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Plan Title / বিবরণ", color = Gray400) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_goal_input_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Horizon Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isLongTerm) PureWhite else DarkElevated)
                            .border(1.dp, if (!isLongTerm) PureWhite else BorderDark, RoundedCornerShape(6.dp))
                            .clickable { isLongTerm = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Short-term",
                            fontWeight = FontWeight.Bold,
                            color = if (!isLongTerm) PureBlack else PureWhite,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isLongTerm) PureWhite else DarkElevated)
                            .border(1.dp, if (isLongTerm) PureWhite else BorderDark, RoundedCornerShape(6.dp))
                            .clickable { isLongTerm = true }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Long-term",
                            fontWeight = FontWeight.Bold,
                            color = if (isLongTerm) PureBlack else PureWhite,
                            fontSize = 12.sp
                        )
                    }
                }

                // Target Date
                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Date (YYYY-MM-DD)", color = Gray400) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Category selector
                LazyColumn(
                    modifier = Modifier.height(48.dp)
                ) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            categories.forEach { cat ->
                                CategoryPill(
                                    title = cat,
                                    isSelected = category == cat,
                                    onSelect = { category = cat }
                                )
                            }
                        }
                    }
                }

                // Notes / Sub-tasks
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Sub-tasks & Notes / ধাপ ও নোট", color = Gray400) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 6
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            goal.copy(
                                title = title.trim(),
                                targetDate = targetDate.trim(),
                                isLongTerm = isLongTerm,
                                category = category,
                                notes = notes.trim()
                            )
                        )
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = PureBlack
                ),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text("Update Plan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Gray400)
            ) {
                Text("Cancel")
            }
        },
        containerColor = DarkCard,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun AddGoalDialog(
    nextPlanNumber: Int,
    onDismiss: () -> Unit,
    onConfirm: (title: String, targetDate: String, isLongTerm: Boolean, category: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var isLongTerm by remember { mutableStateOf(false) }
    var category by remember { mutableStateOf("Career") }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val defaultTargetDate = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 30)
        sdf.format(cal.time)
    }
    var targetDate by remember { mutableStateOf(defaultTargetDate) }

    val categories = listOf("Career", "Health", "Finance", "Learning", "Personal", "Business")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New Plan #$nextPlanNumber / নতুন লক্ষ্য",
                fontWeight = FontWeight.Bold,
                color = PureWhite
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Plan Title / লক্ষ্যের বিবরণ", color = Gray400) },
                    placeholder = { Text("e.g. বই পড়া ও গবেষণা", color = Gray500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_goal_input_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Horizon Toggle
                Column {
                    Text(
                        text = "Goal Horizon",
                        style = MaterialTheme.typography.labelMedium.copy(color = Gray400)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isLongTerm) PureWhite else DarkElevated)
                                .border(1.dp, if (!isLongTerm) PureWhite else BorderDark, RoundedCornerShape(6.dp))
                                .clickable { isLongTerm = false }
                                .padding(vertical = 8.dp)
                                .testTag("goal_horizon_short_term"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Short-term",
                                fontWeight = FontWeight.Bold,
                                color = if (!isLongTerm) PureBlack else PureWhite,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isLongTerm) PureWhite else DarkElevated)
                                .border(1.dp, if (isLongTerm) PureWhite else BorderDark, RoundedCornerShape(6.dp))
                                .clickable { isLongTerm = true }
                                .padding(vertical = 8.dp)
                                .testTag("goal_horizon_long_term"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Long-term",
                                fontWeight = FontWeight.Bold,
                                color = if (isLongTerm) PureBlack else PureWhite,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Target Date
                OutlinedTextField(
                    value = targetDate,
                    onValueChange = { targetDate = it },
                    label = { Text("Target Date (YYYY-MM-DD)", color = Gray400) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_goal_input_date"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = true
                )

                // Category Selector
                Column {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium.copy(color = Gray400)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.take(3).forEach { cat ->
                            CategoryPill(
                                title = cat,
                                isSelected = category == cat,
                                onSelect = { category = cat },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.drop(3).forEach { cat ->
                            CategoryPill(
                                title = cat,
                                isSelected = category == cat,
                                onSelect = { category = cat },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Notes / Milestones
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Sub-tasks / ধাপসমূহ (Optional)", color = Gray400) },
                    placeholder = { Text("Key steps, milestones or motivation", color = Gray500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_goal_input_notes"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        // Title will be numbered automatically if user doesn't prefix
                        val cleanTitle = title.trim()
                        val regex = Regex("^\\d+[.)\\-]\\s*")
                        val finalTitle = if (regex.containsMatchIn(cleanTitle)) cleanTitle else "$nextPlanNumber. $cleanTitle"
                        onConfirm(finalTitle, targetDate.trim(), isLongTerm, category, notes.trim())
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = PureBlack
                ),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.testTag("add_goal_confirm_button")
            ) {
                Text("Save Plan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Gray400)
            ) {
                Text("Cancel")
            }
        },
        containerColor = DarkCard,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun CategoryPill(
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) PureWhite else DarkElevated)
            .border(1.dp, if (isSelected) PureWhite else BorderDark, RoundedCornerShape(6.dp))
            .clickable { onSelect() }
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PureBlack else Gray300
        )
    }
}
