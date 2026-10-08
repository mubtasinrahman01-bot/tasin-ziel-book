package com.example.ui.screens.dashboard

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CalendarEventEntity
import com.example.data.model.GoalEntity
import com.example.data.model.GoalStatus
import com.example.data.model.NoteEntity
import com.example.data.model.TaskEntity
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeDashboardScreen(
    tasks: List<TaskEntity>,
    notes: List<NoteEntity>,
    goals: List<GoalEntity>,
    events: List<CalendarEventEntity>,
    onNavigateTo: (AppScreen) -> Unit,
    onToggleTask: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateDisplay = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date())

    val totalTasks = tasks.size
    val completedTasks = tasks.count { it.isCompleted }

    val totalNotes = notes.size

    val totalGoals = goals.size
    val completedGoals = goals.count { it.status == GoalStatus.DONE }

    val totalEvents = events.size

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome and Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
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
                            // Official TAsin ZiEL Bo0k Line-Art Logo (Thinking person with birds)
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PureBlack)
                                    .border(1.dp, BorderDark, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_app_logo),
                                    contentDescription = "TAsin ZiEL Bo0k Official Logo",
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Fit
                                )
                            }
                            Text(
                                text = "TAsin ZiEL Bo0k",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    letterSpacing = 1.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkElevated)
                                .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = dateDisplay,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Gray400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Dashboard Overview / মূল নিয়ন্ত্রণ কেন্দ্র",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PureWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Select any section below to manage your productivity.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Gray400)
                    )
                }
            }
        }

        // 2x2 Grid Cards
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top Row: Card 1 (Top-Left) & Card 2 (Top-Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1 (Top-Left): "Daily task" — Square Checklist line-art icon
                    GridDashboardCard(
                        title = "Daily task",
                        subtitle = "কাজের তালিকা",
                        metric = "$completedTasks/$totalTasks Done",
                        iconRes = R.drawable.ic_square_checklist,
                        onClick = { onNavigateTo(AppScreen.DAILY_TASK) },
                        testTag = "dashboard_card_daily_task",
                        modifier = Modifier.weight(1f)
                    )

                    // Card 2 (Top-Right): "Notes" — Coil Notebook & Pen line-art icon
                    GridDashboardCard(
                        title = "Notes",
                        subtitle = "জার্নাল ও নোট",
                        metric = "$totalNotes Entries",
                        iconRes = R.drawable.ic_coil_notebook_pen,
                        onClick = { onNavigateTo(AppScreen.NOTES) },
                        testTag = "dashboard_card_notes",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bottom Row: Card 3 (Bottom-Left) & Card 4 (Bottom-Right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 3 (Bottom-Left): "Future Plans" — Growth Line Chart icon
                    GridDashboardCard(
                        title = "Future Plans",
                        subtitle = "ভবিষ্যতের লক্ষ্য",
                        metric = "$completedGoals/$totalGoals Achieved",
                        iconRes = R.drawable.ic_line_chart_growth,
                        onClick = { onNavigateTo(AppScreen.FUTURE_PLANS) },
                        testTag = "dashboard_card_future_plans",
                        modifier = Modifier.weight(1f)
                    )

                    // Card 4 (Bottom-Right): "Remind me" — Monthly Calendar Grid icon
                    GridDashboardCard(
                        title = "Remind me",
                        subtitle = "সময়সূচী ও অ্যালার্ম",
                        metric = "$totalEvents Scheduled",
                        iconRes = R.drawable.ic_monthly_calendar_grid,
                        onClick = { onNavigateTo(AppScreen.REMIND_ME) },
                        testTag = "dashboard_card_remind_me",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Quick Focus: Active daily tasks
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Tasks / দ্রুত কাজ",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                )

                Text(
                    text = "View All →",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PureWhite,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onNavigateTo(AppScreen.DAILY_TASK) }
                        .padding(4.dp)
                )
            }
        }

        if (tasks.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderDark)
                ) {
                    Text(
                        text = "No pending tasks. Tap Daily task above to create one.",
                        style = MaterialTheme.typography.bodySmall.copy(color = Gray400),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(tasks.take(4), key = { it.id }) { task ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                        // Square Checkbox
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (task.isCompleted) PureWhite else DarkElevated)
                                .border(
                                    1.5.dp,
                                    if (task.isCompleted) PureWhite else BorderDark,
                                    RoundedCornerShape(4.dp)
                                )
                                .clickable { onToggleTask(task.id, !task.isCompleted) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (task.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = PureBlack,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Medium,
                                    color = if (task.isCompleted) Gray500 else PureWhite,
                                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                )
                            )
                            Text(
                                text = task.dueDate,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Gray400,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Individual High-Contrast 2x2 Grid Card
 */
@Composable
fun GridDashboardCard(
    title: String,
    subtitle: String,
    metric: String,
    iconRes: Int,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = DarkCard),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top row: Line-art Icon Container & Navigate arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkElevated)
                        .border(1.dp, BorderDark, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = title,
                        tint = PureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Navigate to $title",
                    tint = Gray400,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Title and Subtitle
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite,
                        fontSize = 16.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Gray400,
                        fontSize = 11.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metric Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(DarkElevated)
                    .border(1.dp, BorderDark, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = metric,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PureWhite,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
