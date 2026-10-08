package com.example.ui.screens.todo

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.ui.components.CategoryChip
import com.example.ui.components.PriorityBadge
import com.example.ui.components.StylizedEmptyStateView
import com.example.ui.components.SubPageTopBar
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.TaskFilter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DateSelectorItem(
    val dateString: String,
    val dayOfWeek: String,
    val dayOfMonth: String,
    val monthShort: String,
    val isToday: Boolean,
    val isTomorrow: Boolean,
    val displayTop: String
)

@Composable
fun TodoListScreen(
    tasks: List<TaskEntity>,
    allTasks: List<TaskEntity>,
    taskFilter: TaskFilter,
    todayDate: String,
    onFilterChanged: (TaskFilter) -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    onDeleteTask: (TaskEntity) -> Unit,
    showAddDialog: Boolean,
    onSetShowAddDialog: (Boolean) -> Unit,
    onAddTask: (String, String, TaskPriority, String) -> Unit,
    onBackToHome: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Selected Date for horizontal date scroll bar - defaults to Today!
    var selectedDate by remember { mutableStateOf(todayDate) }

    // Generate date sequence: past 3 days to next 21 days
    val dateList = remember(todayDate) {
        val list = mutableListOf<DateSelectorItem>()
        val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfDayOfWeek = SimpleDateFormat("EEE", Locale.getDefault())
        val sdfDayOfMonth = SimpleDateFormat("d", Locale.getDefault())
        val sdfMonth = SimpleDateFormat("MMM", Locale.getDefault())

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -3) // 3 days back

        for (i in 0 until 25) {
            val dateStr = sdfDate.format(cal.time)
            val isToday = dateStr == todayDate

            val calTomorrow = Calendar.getInstance()
            calTomorrow.add(Calendar.DAY_OF_YEAR, 1)
            val isTomorrow = dateStr == sdfDate.format(calTomorrow.time)

            val displayTop = when {
                isToday -> "Today"
                isTomorrow -> "Tmrw"
                else -> sdfDayOfWeek.format(cal.time)
            }

            list.add(
                DateSelectorItem(
                    dateString = dateStr,
                    dayOfWeek = sdfDayOfWeek.format(cal.time),
                    dayOfMonth = sdfDayOfMonth.format(cal.time),
                    monthShort = sdfMonth.format(cal.time),
                    isToday = isToday,
                    isTomorrow = isTomorrow,
                    displayTop = displayTop
                )
            )
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    // Set of dates that have tasks scheduled
    val datesWithTasks = remember(allTasks) {
        allTasks.map { it.dueDate }.toSet()
    }

    // Tasks filtered by the selected date
    val tasksForSelectedDate = remember(allTasks, selectedDate) {
        allTasks.filter { it.dueDate == selectedDate }
    }

    val selectedDateTotal = tasksForSelectedDate.size
    val selectedDateCompleted = tasksForSelectedDate.count { it.isCompleted }
    val progressRatio = if (selectedDateTotal > 0) {
        selectedDateCompleted.toFloat() / selectedDateTotal.toFloat()
    } else 0f
    val animatedProgress by animateFloatAsState(targetValue = progressRatio, label = "date_task_progress")

    // Formatted title for selected date
    val selectedDateLabel = remember(selectedDate, todayDate) {
        formatSelectedDateHeader(selectedDate, todayDate)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("todo_list_container"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sub-page Top Bar with clear Back Button [←]
            if (onBackToHome != null) {
                item {
                    SubPageTopBar(
                        title = "Daily task",
                        subtitle = "দৈনিক কাজের সময়সূচী",
                        iconRes = R.drawable.ic_square_checklist,
                        onBack = onBackToHome
                    )
                }
            }

            // Sleek Horizontal Date Scroll Bar / Calendar Selector
            item {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 2.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Date / তারিখ নির্বাচন",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Gray400,
                                letterSpacing = 0.5.sp
                            )
                        )

                        // Quick Jump to Today button
                        if (selectedDate != todayDate) {
                            Text(
                                text = "Go to Today →",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                ),
                                modifier = Modifier
                                    .clickable { selectedDate = todayDate }
                                    .padding(4.dp)
                                    .testTag("date_selector_today_quick_btn")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("horizontal_date_selector_bar")
                    ) {
                        items(dateList, key = { it.dateString }) { item ->
                            val isSelected = item.dateString == selectedDate
                            val hasTasksOnDate = datesWithTasks.contains(item.dateString)

                            DateCardItem(
                                item = item,
                                isSelected = isSelected,
                                hasTasks = hasTasksOnDate,
                                onClick = { selectedDate = item.dateString }
                            )
                        }
                    }
                }
            }

            // Summary Card for Selected Date
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_summary_card"),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Square Checklist Icon
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(DarkElevated)
                                        .border(1.dp, BorderDark, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_square_checklist),
                                        contentDescription = "Square Checklist",
                                        tint = PureWhite,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = selectedDateLabel,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = PureWhite
                                        )
                                    )
                                    Text(
                                        text = if (selectedDateTotal > 0) {
                                            "$selectedDateCompleted of $selectedDateTotal tasks completed"
                                        } else {
                                            "No tasks scheduled for this day"
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(color = Gray400)
                                    )
                                }
                            }

                            Text(
                                text = if (selectedDateTotal > 0) "${(progressRatio * 100).toInt()}%" else "0%",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { animatedProgress },
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

            // Section Header with Add Quick Action
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tasks for $selectedDate ($selectedDateTotal)",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkElevated)
                            .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                            .clickable { onSetShowAddDialog(true) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                            .testTag("add_task_date_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Add Task",
                                color = PureWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Task List for Selected Date
            if (tasksForSelectedDate.isEmpty()) {
                item {
                    StylizedEmptyStateView(
                        painter = painterResource(id = R.drawable.ic_square_checklist),
                        title = "No tasks on $selectedDate",
                        subtitle = "এই তারিখের জন্য কোনো কাজ নেই। নতুন কাজ যোগ করতে + বাটনে চাপুন।",
                        actionButtonText = "Add Task for $selectedDate",
                        onActionClick = { onSetShowAddDialog(true) }
                    )
                }
            } else {
                items(tasksForSelectedDate, key = { it.id }) { task ->
                    TaskItemCard(
                        task = task,
                        isToday = task.dueDate == todayDate,
                        onToggle = { onToggleTask(task.id, !task.isCompleted) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // FAB to add task (pre-populated with selected date)
        FloatingActionButton(
            onClick = { onSetShowAddDialog(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_task_fab"),
            shape = RoundedCornerShape(8.dp),
            containerColor = PureWhite,
            contentColor = PureBlack
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            defaultDate = selectedDate, // Default due date to currently selected date!
            onDismiss = { onSetShowAddDialog(false) },
            onConfirm = onAddTask
        )
    }
}

/**
 * Individual Date Card in the Horizontal Date Selector Bar
 */
@Composable
fun DateCardItem(
    item: DateSelectorItem,
    isSelected: Boolean,
    hasTasks: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSelected) PureWhite else DarkElevated)
            .border(
                if (isSelected) 1.5.dp else if (item.isToday) 1.dp else 1.dp,
                if (isSelected) PureWhite else if (item.isToday) Gray400 else BorderDark,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .testTag("date_card_${item.dateString}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Day of week / "Today" / "Tmrw"
            Text(
                text = item.displayTop,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected || item.isToday) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) PureBlack else if (item.isToday) PureWhite else Gray400,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Day Number (7, 8, etc.)
            Text(
                text = item.dayOfMonth,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) PureBlack else PureWhite,
                    fontSize = 18.sp
                )
            )

            // Month (Oct)
            Text(
                text = item.monthShort,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (isSelected) PureBlack else Gray500,
                    fontSize = 10.sp
                )
            )

            // Dot indicator if tasks exist on this date
            if (hasTasks) {
                Spacer(modifier = Modifier.height(3.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(if (isSelected) PureBlack else PureWhite, RoundedCornerShape(1.dp))
                )
            } else {
                Spacer(modifier = Modifier.height(7.dp))
            }
        }
    }
}

@Composable
fun TaskItemCard(
    task: TaskEntity,
    isToday: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("task_card_${task.id}"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (task.isCompleted) BorderDark else BorderDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Crisp SQUARE Checkbox
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (task.isCompleted) PureWhite else DarkElevated)
                    .border(
                        1.5.dp,
                        if (task.isCompleted) PureWhite else BorderDark,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onToggle() }
                    .testTag("task_checkbox_${task.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = PureBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Task content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                        color = if (task.isCompleted) Gray500 else PureWhite,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Due Date chip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = if (isToday) PureWhite else Gray400,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = if (isToday) "Today" else task.dueDate,
                            fontSize = 11.sp,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) PureWhite else Gray400
                        )
                    }

                    // Priority
                    PriorityBadge(priority = task.priority)

                    // Category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category,
                            fontSize = 11.sp,
                            color = Gray400
                        )
                    }
                }
            }

            // Delete action
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("task_delete_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = Gray500,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddTaskDialog(
    defaultDate: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, dueDate: String, priority: TaskPriority, category: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(defaultDate) }
    var priority by remember { mutableStateOf(TaskPriority.MEDIUM) }
    var category by remember { mutableStateOf("Daily") }

    val categories = listOf("Daily", "Work", "Health", "Personal", "Errands")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Task for $dueDate",
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
                    label = { Text("Task Description / কাজের নাম") },
                    placeholder = { Text("e.g. প্রতিদিনের রুটিন সম্পন্ন করুন") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_task_input_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Due Date (YYYY-MM-DD)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_task_input_date"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    singleLine = true
                )

                // Priority selector
                Column {
                    Text(
                        text = "Priority Level",
                        style = MaterialTheme.typography.labelMedium.copy(color = Gray400)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            TaskPriority.HIGH to "High",
                            TaskPriority.MEDIUM to "Medium",
                            TaskPriority.LOW to "Low"
                        ).forEach { (p, label) ->
                            val isSelected = priority == p
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isSelected) PureWhite else DarkElevated)
                                    .border(1.dp, if (isSelected) PureWhite else BorderDark, RoundedCornerShape(4.dp))
                                    .clickable { priority = p }
                                    .padding(vertical = 8.dp)
                                    .testTag("task_priority_${p.name.lowercase()}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) PureBlack else Gray400
                                )
                            }
                        }
                    }
                }

                // Category selector
                Column {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium.copy(color = Gray400)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(categories) { cat ->
                            CategoryChip(
                                text = cat,
                                isSelected = category == cat,
                                onClick = { category = cat }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), dueDate.trim(), priority, category)
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = PureBlack
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.testTag("add_task_confirm_button")
            ) {
                Text("Add Task", fontWeight = FontWeight.Bold)
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
        shape = RoundedCornerShape(8.dp)
    )
}

fun formatSelectedDateHeader(dateString: String, todayDate: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(dateString) ?: return dateString
        when (dateString) {
            todayDate -> {
                val full = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(date)
                "Today • $full"
            }
            else -> {
                SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(date)
            }
        }
    } catch (_: Exception) {
        dateString
    }
}
