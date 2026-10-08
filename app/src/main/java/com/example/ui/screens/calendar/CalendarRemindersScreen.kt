package com.example.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.CalendarEventEntity
import com.example.ui.components.CategoryChip
import com.example.ui.components.StylizedEmptyStateView
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarRemindersScreen(
    allEvents: List<CalendarEventEntity>,
    eventsForSelectedDate: List<CalendarEventEntity>,
    selectedDate: String,
    todayDate: String,
    onSelectDate: (String) -> Unit,
    onToggleEventCompleted: (Long, Boolean) -> Unit,
    onDeleteEvent: (CalendarEventEntity) -> Unit,
    showAddDialog: Boolean,
    onSetShowAddDialog: (Boolean) -> Unit,
    onAddEvent: (String, String, String, String, Boolean, String) -> Unit,
    onBackToHome: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var currentCalendarMonth by remember {
        val cal = Calendar.getInstance()
        mutableStateOf(cal)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("calendar_screen_container"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Sub-page Top Bar with clear Back Button [←]
            if (onBackToHome != null) {
                item {
                    com.example.ui.components.SubPageTopBar(
                        title = "Remind me",
                        subtitle = "ক্যালেন্ডার ও অনুস্মারক",
                        iconRes = R.drawable.ic_monthly_calendar_grid,
                        onBack = onBackToHome
                    )
                }
            }

            // Interactive Calendar Component
            item {
                InteractiveCalendarCard(
                    cal = currentCalendarMonth,
                    selectedDate = selectedDate,
                    todayDate = todayDate,
                    eventsDates = allEvents.map { it.date }.toSet(),
                    onMonthChange = { newCal -> currentCalendarMonth = newCal },
                    onDateSelected = onSelectDate
                )
            }

            // Events for the selected date header with Monthly Calendar Grid icon
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkElevated)
                                .border(1.dp, BorderDark, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_monthly_calendar_grid),
                                contentDescription = "Calendar Grid",
                                tint = PureWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Schedule & Reminders / সময়সূচী",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            )
                            Text(
                                text = formatReadableDate(selectedDate),
                                style = MaterialTheme.typography.bodySmall.copy(color = Gray400)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkElevated)
                            .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                            .clickable { onSetShowAddDialog(true) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("add_event_header_button"),
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
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "Add Event",
                                color = PureWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Events List for selected date
            if (eventsForSelectedDate.isEmpty()) {
                item {
                    StylizedEmptyStateView(
                        painter = painterResource(id = R.drawable.ic_monthly_calendar_grid),
                        title = "No events on this day",
                        subtitle = "তারিখ নির্বাচন করুন এবং মিটিং বা কাজের অনুস্মারক যুক্ত করুন।",
                        actionButtonText = "Schedule Event & Reminder",
                        onActionClick = { onSetShowAddDialog(true) }
                    )
                }
            } else {
                items(eventsForSelectedDate, key = { it.id }) { event ->
                    CalendarEventCard(
                        event = event,
                        onToggle = { onToggleEventCompleted(event.id, !event.isCompleted) },
                        onDelete = { onDeleteEvent(event) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        // FAB to add event
        FloatingActionButton(
            onClick = { onSetShowAddDialog(true) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_event_fab"),
            shape = RoundedCornerShape(8.dp),
            containerColor = PureWhite,
            contentColor = PureBlack
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Event")
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            initialDate = selectedDate,
            onDismiss = { onSetShowAddDialog(false) },
            onConfirm = onAddEvent
        )
    }
}

@Composable
fun InteractiveCalendarCard(
    cal: Calendar,
    selectedDate: String,
    todayDate: String,
    eventsDates: Set<String>,
    onMonthChange: (Calendar) -> Unit,
    onDateSelected: (String) -> Unit
) {
    val monthTitle = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)

    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val tempCal = cal.clone() as Calendar
    tempCal.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK)
    val leadingEmptyDays = (firstDayOfWeek + 5) % 7

    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) + 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("interactive_calendar_card"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Month Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthTitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = {
                            val newCal = cal.clone() as Calendar
                            newCal.add(Calendar.MONTH, -1)
                            onMonthChange(newCal)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("calendar_prev_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = Gray400,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Jump to Today
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkElevated)
                            .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                            .clickable {
                                val now = Calendar.getInstance()
                                onMonthChange(now)
                                onDateSelected(todayDate)
                            }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("calendar_today_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Today",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    }

                    IconButton(
                        onClick = {
                            val newCal = cal.clone() as Calendar
                            newCal.add(Calendar.MONTH, 1)
                            onMonthChange(newCal)
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("calendar_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = Gray400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Day of week labels
            val weekDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { dayName ->
                    Text(
                        text = dayName,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Gray500
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Days Grid
            val totalCells = leadingEmptyDays + daysInMonth
            val rows = (totalCells + 6) / 7

            for (r in 0 until rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (c in 0 until 7) {
                        val cellIndex = r * 7 + c
                        val dayNumber = cellIndex - leadingEmptyDays + 1

                        if (cellIndex < leadingEmptyDays || dayNumber > daysInMonth) {
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        } else {
                            val dateStr = String.format(Locale.US, "%04d-%02d-%02d", year, month, dayNumber)
                            val isSelected = dateStr == selectedDate
                            val isToday = dateStr == todayDate
                            val hasEvents = eventsDates.contains(dateStr)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        when {
                                            isSelected -> PureWhite
                                            isToday -> DarkElevated
                                            else -> Color.Transparent
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when {
                                            isSelected -> PureWhite
                                            isToday -> PureWhite
                                            else -> Color.Transparent
                                        },
                                        RoundedCornerShape(4.dp)
                                    )
                                    .clickable { onDateSelected(dateStr) }
                                    .testTag("calendar_day_$dateStr"),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "$dayNumber",
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) PureBlack else PureWhite
                                    )

                                    if (hasEvents) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(3.dp)
                                                .background(if (isSelected) PureBlack else PureWhite, RoundedCornerShape(1.dp))
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarEventCard(
    event: CalendarEventEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
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
                    .background(if (event.isCompleted) PureWhite else DarkElevated)
                    .border(
                        1.5.dp,
                        if (event.isCompleted) PureWhite else BorderDark,
                        RoundedCornerShape(4.dp)
                    )
                    .clickable { onToggle() }
                    .testTag("event_checkbox_${event.id}"),
                contentAlignment = Alignment.Center
            ) {
                if (event.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = PureBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Event Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Time Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkElevated)
                            .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = event.time,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                        }
                    }

                    // Category
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = event.category,
                            fontSize = 11.sp,
                            color = Gray400
                        )
                    }

                    // Reminder Badge
                    if (event.reminderEnabled) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Alarm Active",
                                tint = PureWhite,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Alarm",
                                fontSize = 10.sp,
                                color = PureWhite,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (event.isCompleted) FontWeight.Normal else FontWeight.Medium,
                        color = if (event.isCompleted) Gray500 else PureWhite,
                        textDecoration = if (event.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )

                if (event.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = event.notes,
                        style = MaterialTheme.typography.bodySmall.copy(color = Gray400)
                    )
                }
            }

            // Delete action
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(34.dp)
                    .testTag("event_delete_${event.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Event",
                    tint = Gray500,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AddEventDialog(
    initialDate: String,
    onDismiss: () -> Unit,
    onConfirm: (title: String, date: String, time: String, category: String, reminderEnabled: Boolean, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(initialDate) }
    var time by remember { mutableStateOf("10:00") }
    var category by remember { mutableStateOf("Meeting") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Meeting", "Work", "Health", "Personal", "Deadline")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Event & Reminder / নতুন ইভেন্ট",
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
                    label = { Text("Event Title / শিরোনাম") },
                    placeholder = { Text("e.g. টিম মিটিং বা পরিকল্পনা") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_event_input_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date") },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("add_event_input_date"),
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
                        value = time,
                        onValueChange = { time = it },
                        label = { Text("Time (HH:mm)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("add_event_input_time"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PureWhite,
                            unfocusedBorderColor = BorderDark,
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite,
                            cursorColor = PureWhite
                        ),
                        singleLine = true
                    )
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

                // Reminder / Alarm Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(DarkElevated)
                        .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "Reminder Notification",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PureWhite
                            )
                            Text(
                                text = "Trigger alert on device",
                                fontSize = 11.sp,
                                color = Gray400
                            )
                        }
                    }

                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { reminderEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PureBlack,
                            checkedTrackColor = PureWhite,
                            uncheckedThumbColor = Gray500,
                            uncheckedTrackColor = DarkCard
                        ),
                        modifier = Modifier.testTag("add_event_reminder_switch")
                    )
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("নোট বা বিস্তারিত তথ্য...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_event_input_notes"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PureWhite,
                        unfocusedBorderColor = BorderDark,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        cursorColor = PureWhite
                    ),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title.trim(), date.trim(), time.trim(), category, reminderEnabled, notes.trim())
                    }
                },
                enabled = title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PureWhite,
                    contentColor = PureBlack
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.testTag("add_event_confirm_button")
            ) {
                Text("Schedule Event", fontWeight = FontWeight.Bold)
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

fun formatReadableDate(dateString: String): String {
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = parser.parse(dateString)
        if (date != null) {
            SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(date)
        } else {
            dateString
        }
    } catch (_: Exception) {
        dateString
    }
}
