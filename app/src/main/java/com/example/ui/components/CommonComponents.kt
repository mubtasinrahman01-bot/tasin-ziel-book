package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.TaskPriority
import com.example.ui.theme.BorderDark
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkElevated
import com.example.ui.theme.Gray300
import com.example.ui.theme.Gray400
import com.example.ui.theme.Gray500
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.PlannerTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PlanPulseHeader(
    modifier: Modifier = Modifier
) {
    val dateDisplay = SimpleDateFormat("EEEE, MMM d", Locale.getDefault()).format(Date())

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(PureBlack)
                        .border(1.dp, BorderDark, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_app_logo),
                        contentDescription = "TAsin ZiEL Bo0k Official Logo",
                        modifier = Modifier
                            .size(18.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "TAsin ZiEL Bo0k",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Personal Productivity & Goal Planner",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PureWhite
                )
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(DarkCard)
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
}

/**
 * 2x2 Dashboard Cards Grid:
 * Top-Left Card: Daily task (Square Checklist icon)
 * Top-Right Card: Notes (Notebook & Pen icon)
 * Bottom-Left Card: Future Plans (Growth Line Chart icon)
 * Bottom-Right Card: Remind me (Calendar Grid icon)
 */
@Composable
fun DashboardCardsGrid(
    currentTab: PlannerTab,
    onTabSelected: (PlannerTab) -> Unit,
    totalTasks: Int,
    completedTasks: Int,
    totalNotes: Int,
    totalGoals: Int,
    completedGoals: Int,
    totalEvents: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dashboard_cards_grid"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top Row: Top-Left (Daily task) & Top-Right (Notes)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top-Left Card: Daily task (Square Checklist icon)
            DashboardSectionCard(
                title = "Daily task",
                caption = "$completedTasks/$totalTasks completed",
                iconRes = R.drawable.ic_square_checklist,
                isSelected = currentTab == PlannerTab.TODO_LIST,
                onClick = { onTabSelected(PlannerTab.TODO_LIST) },
                testTag = "dashboard_card_daily_task",
                modifier = Modifier.weight(1f)
            )

            // Top-Right Card: Notes (Notebook & Pen icon)
            DashboardSectionCard(
                title = "Notes",
                caption = "$totalNotes journal logs",
                iconRes = R.drawable.ic_coil_notebook_pen,
                isSelected = currentTab == PlannerTab.PERSONAL_NOTES,
                onClick = { onTabSelected(PlannerTab.PERSONAL_NOTES) },
                testTag = "dashboard_card_notes",
                modifier = Modifier.weight(1f)
            )
        }

        // Bottom Row: Bottom-Left (Future Plans) & Bottom-Right (Remind me)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Bottom-Left Card: Future Plans (Growth Line Chart icon)
            DashboardSectionCard(
                title = "Future Plans",
                caption = "$completedGoals/$totalGoals achieved",
                iconRes = R.drawable.ic_line_chart_growth,
                isSelected = currentTab == PlannerTab.FUTURE_PLANS,
                onClick = { onTabSelected(PlannerTab.FUTURE_PLANS) },
                testTag = "dashboard_card_future_plans",
                modifier = Modifier.weight(1f)
            )

            // Bottom-Right Card: Remind me (Calendar Grid icon)
            DashboardSectionCard(
                title = "Remind me",
                caption = "$totalEvents scheduled",
                iconRes = R.drawable.ic_monthly_calendar_grid,
                isSelected = currentTab == PlannerTab.CALENDAR_REMINDERS,
                onClick = { onTabSelected(PlannerTab.CALENDAR_REMINDERS) },
                testTag = "dashboard_card_remind_me",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DashboardSectionCard(
    title: String,
    caption: String,
    iconRes: Int,
    isSelected: Boolean,
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
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DarkElevated else DarkCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) PureWhite else BorderDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stylized Line-art Icon Container
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (isSelected) PureWhite else DarkElevated)
                    .border(1.dp, if (isSelected) PureWhite else BorderDark, RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    tint = if (isSelected) PureBlack else PureWhite,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        color = PureWhite,
                        fontSize = 14.sp
                    ),
                    maxLines = 1
                )
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isSelected) PureWhite else Gray400,
                        fontSize = 11.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

data class SubPageNavItem(
    val screen: AppScreen,
    val label: String,
    val contentDescription: String,
    val iconRes: Int,
    val testTag: String
)

@Composable
fun PlanPulseBottomNav(
    currentScreen: AppScreen,
    onNavigateToScreen: (AppScreen) -> Unit,
    onNavigateHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        SubPageNavItem(
            screen = AppScreen.HOME_DASHBOARD,
            label = "Home",
            contentDescription = "Dashboard / Home",
            iconRes = R.drawable.ic_clean_home,
            testTag = "nav_item_home"
        ),
        SubPageNavItem(
            screen = AppScreen.DAILY_TASK,
            label = "Daily task",
            contentDescription = "Daily task",
            iconRes = R.drawable.ic_square_checklist,
            testTag = "nav_item_daily_task"
        ),
        SubPageNavItem(
            screen = AppScreen.NOTES,
            label = "Notes",
            contentDescription = "Notes",
            iconRes = R.drawable.ic_coil_notebook_pen,
            testTag = "nav_item_notes"
        ),
        SubPageNavItem(
            screen = AppScreen.FUTURE_PLANS,
            label = "Future Plans",
            contentDescription = "Future Plans",
            iconRes = R.drawable.ic_line_chart_growth,
            testTag = "nav_item_future_plans"
        ),
        SubPageNavItem(
            screen = AppScreen.REMIND_ME,
            label = "Remind me",
            contentDescription = "Remind me",
            iconRes = R.drawable.ic_monthly_calendar_grid,
            testTag = "nav_item_remind_me"
        )
    )

    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = BorderDark,
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
            )
            .navigationBarsPadding()
            .testTag("bottom_navigation_bar"),
        containerColor = PureBlack,
        tonalElevation = 0.dp
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                modifier = Modifier.testTag(item.testTag),
                selected = isSelected,
                onClick = {
                    if (item.screen == AppScreen.HOME_DASHBOARD) {
                        onNavigateHome()
                    } else {
                        onNavigateToScreen(item.screen)
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconRes),
                        contentDescription = item.contentDescription,
                        tint = if (isSelected) PureBlack else Gray400,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        maxLines = 1,
                        softWrap = false,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PureBlack,
                    selectedTextColor = PureWhite,
                    indicatorColor = PureWhite,
                    unselectedIconColor = Gray500,
                    unselectedTextColor = Gray500
                )
            )
        }
    }
}

@Composable
fun PriorityBadge(priority: TaskPriority) {
    val (label, isHigh) = when (priority) {
        TaskPriority.HIGH -> Pair("High", true)
        TaskPriority.MEDIUM -> Pair("Medium", false)
        TaskPriority.LOW -> Pair("Low", false)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isHigh) PureWhite else DarkElevated)
            .border(1.dp, if (isHigh) PureWhite else BorderDark, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = if (isHigh) PureBlack else Gray300,
            fontSize = 11.sp,
            fontWeight = if (isHigh) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val bg = if (isSelected) PureWhite else DarkElevated
    val fg = if (isSelected) PureBlack else Gray300
    val borderCol = if (isSelected) PureWhite else BorderDark

    val modifier = Modifier
        .clip(RoundedCornerShape(6.dp))
        .background(bg)
        .border(1.dp, borderCol, RoundedCornerShape(6.dp))
        .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
        .padding(horizontal = 10.dp, vertical = 5.dp)

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = text,
            color = fg,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun StylizedEmptyStateView(
    painter: Painter,
    title: String,
    subtitle: String,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkCard)
                .border(1.dp, BorderDark, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                tint = PureWhite,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PureWhite
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(color = Gray400),
            textAlign = TextAlign.Center
        )
        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(PureWhite)
                    .clickable { onActionClick() }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = actionButtonText,
                    color = PureBlack,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun SubPageTopBar(
    title: String,
    subtitle: String? = null,
    iconRes: Int? = null,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Clear Back Button [←]
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkCard)
                    .border(1.dp, BorderDark, RoundedCornerShape(6.dp))
                    .clickable { onBack() }
                    .testTag("top_back_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Home Dashboard",
                    tint = PureWhite,
                    modifier = Modifier.size(18.dp)
                )
            }

            if (iconRes != null) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkElevated)
                        .border(1.dp, BorderDark, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
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

