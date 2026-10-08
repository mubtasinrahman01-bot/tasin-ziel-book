package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.PlanPulseBottomNav
import com.example.ui.screens.calendar.CalendarRemindersScreen
import com.example.ui.screens.dashboard.HomeDashboardScreen
import com.example.ui.screens.goals.FuturePlansScreen
import com.example.ui.screens.notes.PersonalNotesScreen
import com.example.ui.screens.todo.TodoListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureBlack
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.PlannerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                PlanPulseApp()
            }
        }
    }
}

@Composable
fun PlanPulseApp(
    viewModel: PlannerViewModel = viewModel()
) {
    val context = LocalContext.current

    // Notification permission request for Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!hasPermission) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()

    // Goals State
    val allGoals by viewModel.allGoals.collectAsStateWithLifecycle()
    val filteredGoals by viewModel.filteredGoals.collectAsStateWithLifecycle()
    val goalFilter by viewModel.goalFilter.collectAsStateWithLifecycle()
    val showAddGoalDialog by viewModel.showAddGoalDialog.collectAsStateWithLifecycle()

    // Tasks State
    val allTasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val filteredTasks by viewModel.filteredTasks.collectAsStateWithLifecycle()
    val taskFilter by viewModel.taskFilter.collectAsStateWithLifecycle()
    val showAddTaskDialog by viewModel.showAddTaskDialog.collectAsStateWithLifecycle()

    // Notes State
    val allNotes by viewModel.allNotes.collectAsStateWithLifecycle()
    val filteredNotes by viewModel.filteredNotes.collectAsStateWithLifecycle()
    val noteSearchQuery by viewModel.noteSearchQuery.collectAsStateWithLifecycle()
    val showAddNoteDialog by viewModel.showAddNoteDialog.collectAsStateWithLifecycle()
    val editingNote by viewModel.editingNote.collectAsStateWithLifecycle()

    // Calendar State
    val allEvents by viewModel.allEvents.collectAsStateWithLifecycle()
    val eventsForSelectedDate by viewModel.eventsForSelectedDate.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val showAddEventDialog by viewModel.showAddEventDialog.collectAsStateWithLifecycle()

    // Back handling: If on a sub-page, return back to the main 2x2 Grid Home Dashboard
    if (currentScreen != AppScreen.HOME_DASHBOARD) {
        BackHandler {
            viewModel.navigateToHome()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(PureBlack),
        containerColor = PureBlack,
        bottomBar = {
            // Persistent Bottom Navigation Bar on all sub-pages with 5 icons:
            // 1. Dashboard / Home [🏠] -> Returns back to the main 2x2 Grid Dashboard
            // 2. Daily task [✓]
            // 3. Notes [📓]
            // 4. Future Plans [📈]
            // 5. Remind me [📅]
            if (currentScreen != AppScreen.HOME_DASHBOARD) {
                PlanPulseBottomNav(
                    currentScreen = currentScreen,
                    onNavigateToScreen = { viewModel.navigateToScreen(it) },
                    onNavigateHome = { viewModel.navigateToHome() }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding()
        ) {
            // Screen Content Area with clean crossfade
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                    when (screen) {
                        // Main 2x2 Grid Home Dashboard
                        AppScreen.HOME_DASHBOARD -> {
                            HomeDashboardScreen(
                                tasks = allTasks,
                                notes = allNotes,
                                goals = allGoals,
                                events = allEvents,
                                onNavigateTo = { viewModel.navigateToScreen(it) },
                                onToggleTask = { id, completed -> viewModel.toggleTask(id, completed) }
                            )
                        }

                        // Sub-page 1: Daily task
                        AppScreen.DAILY_TASK -> {
                            TodoListScreen(
                                tasks = filteredTasks,
                                allTasks = allTasks,
                                taskFilter = taskFilter,
                                todayDate = viewModel.todayDateString,
                                onFilterChanged = { viewModel.setTaskFilter(it) },
                                onToggleTask = { id, completed -> viewModel.toggleTask(id, completed) },
                                onDeleteTask = { viewModel.deleteTask(it) },
                                showAddDialog = showAddTaskDialog,
                                onSetShowAddDialog = { viewModel.setShowAddTaskDialog(it) },
                                onAddTask = { title, dueDate, priority, category ->
                                    viewModel.addTask(title, dueDate, priority, category)
                                },
                                onBackToHome = { viewModel.navigateToHome() }
                            )
                        }

                        // Sub-page 2: Notes
                        AppScreen.NOTES -> {
                            PersonalNotesScreen(
                                notes = filteredNotes,
                                allNotes = allNotes,
                                searchQuery = noteSearchQuery,
                                onSearchQueryChanged = { viewModel.setNoteSearchQuery(it) },
                                onDeleteNote = { viewModel.deleteNote(it) },
                                onEditNote = { viewModel.openEditNote(it) },
                                showAddDialog = showAddNoteDialog,
                                editingNote = editingNote,
                                onSetShowAddDialog = { viewModel.setShowAddNoteDialog(it) },
                                onSaveNote = { title, content, category, id ->
                                    viewModel.addOrUpdateNote(title, content, category, id)
                                },
                                onBackToHome = { viewModel.navigateToHome() }
                            )
                        }

                        // Sub-page 3: Future Plans
                        AppScreen.FUTURE_PLANS -> {
                            FuturePlansScreen(
                                goals = filteredGoals,
                                allGoals = allGoals,
                                goalFilter = goalFilter,
                                onFilterChanged = { viewModel.setGoalFilter(it) },
                                onStatusChanged = { id, status -> viewModel.setGoalStatus(id, status) },
                                onDeleteGoal = { viewModel.deleteGoal(it) },
                                showAddDialog = showAddGoalDialog,
                                onSetShowAddDialog = { viewModel.setShowAddGoalDialog(it) },
                                onAddGoal = { title, targetDate, isLongTerm, category, notes ->
                                    viewModel.addGoal(title, targetDate, isLongTerm, category, notes)
                                },
                                onUpdateGoal = { updatedGoal ->
                                    viewModel.updateGoal(updatedGoal)
                                },
                                onBackToHome = { viewModel.navigateToHome() }
                            )
                        }

                        // Sub-page 4: Remind me
                        AppScreen.REMIND_ME -> {
                            CalendarRemindersScreen(
                                allEvents = allEvents,
                                eventsForSelectedDate = eventsForSelectedDate,
                                selectedDate = selectedDate,
                                todayDate = viewModel.todayDateString,
                                onSelectDate = { viewModel.selectDate(it) },
                                onToggleEventCompleted = { id, completed ->
                                    viewModel.toggleEventCompleted(id, completed)
                                },
                                onDeleteEvent = { viewModel.deleteEvent(it) },
                                showAddDialog = showAddEventDialog,
                                onSetShowAddDialog = { viewModel.setShowAddEventDialog(it) },
                                onAddEvent = { title, date, time, category, reminder, notes ->
                                    viewModel.addEvent(title, date, time, category, reminder, notes)
                                },
                                onBackToHome = { viewModel.navigateToHome() }
                            )
                        }
                    }
                }
            }
        }
    }
}
