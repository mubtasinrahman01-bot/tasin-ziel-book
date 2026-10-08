package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CalendarEventEntity
import com.example.data.model.GoalEntity
import com.example.data.model.GoalStatus
import com.example.data.model.NoteEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.repository.PlannerRepository
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppScreen {
    HOME_DASHBOARD,
    DAILY_TASK,
    NOTES,
    FUTURE_PLANS,
    REMIND_ME
}

enum class PlannerTab(val title: String) {
    TODO_LIST("Daily task"),
    PERSONAL_NOTES("Notes"),
    FUTURE_PLANS("Future Plans"),
    CALENDAR_REMINDERS("Remind me")
}

enum class GoalFilter {
    ALL,
    SHORT_TERM,
    LONG_TERM
}

enum class TaskFilter {
    ALL,
    TODAY,
    UPCOMING,
    COMPLETED
}

class PlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlannerRepository
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val todayDateString: String = sdf.format(Date())

    // Screen Navigation (Home 2x2 Grid Dashboard vs Sub-pages)
    private val _currentScreen = MutableStateFlow(AppScreen.HOME_DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Selected Dashboard Tab
    private val _currentTab = MutableStateFlow(PlannerTab.TODO_LIST)
    val currentTab: StateFlow<PlannerTab> = _currentTab.asStateFlow()

    // Goals State & Filters
    private val _goalFilter = MutableStateFlow(GoalFilter.ALL)
    val goalFilter: StateFlow<GoalFilter> = _goalFilter.asStateFlow()

    val allGoals: StateFlow<List<GoalEntity>>
    val filteredGoals: StateFlow<List<GoalEntity>>

    // Tasks State & Filters
    private val _taskFilter = MutableStateFlow(TaskFilter.ALL)
    val taskFilter: StateFlow<TaskFilter> = _taskFilter.asStateFlow()

    val allTasks: StateFlow<List<TaskEntity>>
    val filteredTasks: StateFlow<List<TaskEntity>>

    // Notes State & Search
    private val _noteSearchQuery = MutableStateFlow("")
    val noteSearchQuery: StateFlow<String> = _noteSearchQuery.asStateFlow()

    val allNotes: StateFlow<List<NoteEntity>>
    val filteredNotes: StateFlow<List<NoteEntity>>

    // Calendar & Reminders State
    private val _selectedDate = MutableStateFlow(todayDateString)
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val allEvents: StateFlow<List<CalendarEventEntity>>
    val eventsForSelectedDate: StateFlow<List<CalendarEventEntity>>

    // Dialog Visibilities
    private val _showAddGoalDialog = MutableStateFlow(false)
    val showAddGoalDialog: StateFlow<Boolean> = _showAddGoalDialog.asStateFlow()

    private val _showAddTaskDialog = MutableStateFlow(false)
    val showAddTaskDialog: StateFlow<Boolean> = _showAddTaskDialog.asStateFlow()

    private val _showAddNoteDialog = MutableStateFlow(false)
    val showAddNoteDialog: StateFlow<Boolean> = _showAddNoteDialog.asStateFlow()

    private val _showAddEventDialog = MutableStateFlow(false)
    val showAddEventDialog: StateFlow<Boolean> = _showAddEventDialog.asStateFlow()

    // Note in editing
    private val _editingNote = MutableStateFlow<NoteEntity?>(null)
    val editingNote: StateFlow<NoteEntity?> = _editingNote.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = PlannerRepository(
            goalDao = database.goalDao(),
            taskDao = database.taskDao(),
            noteDao = database.noteDao(),
            calendarEventDao = database.calendarEventDao()
        )

        allGoals = repository.allGoals.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        filteredGoals = combine(allGoals, _goalFilter) { goals, filter ->
            when (filter) {
                GoalFilter.ALL -> goals
                GoalFilter.SHORT_TERM -> goals.filter { !it.isLongTerm }
                GoalFilter.LONG_TERM -> goals.filter { it.isLongTerm }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allTasks = repository.allTasks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        filteredTasks = combine(allTasks, _taskFilter) { tasks, filter ->
            when (filter) {
                TaskFilter.ALL -> tasks
                TaskFilter.TODAY -> tasks.filter { it.dueDate == todayDateString }
                TaskFilter.UPCOMING -> tasks.filter { !it.isCompleted && it.dueDate >= todayDateString }
                TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allNotes = repository.allNotes.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        filteredNotes = combine(allNotes, _noteSearchQuery) { notes, query ->
            if (query.isBlank()) {
                notes
            } else {
                notes.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.content.contains(query, ignoreCase = true) ||
                            it.category.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allEvents = repository.allEvents.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
        eventsForSelectedDate = _selectedDate.flatMapLatest { date ->
            repository.getEventsByDate(date)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        NotificationHelper.createNotificationChannel(application)
    }

    fun navigateToScreen(screen: AppScreen) {
        _currentScreen.value = screen
        when (screen) {
            AppScreen.DAILY_TASK -> _currentTab.value = PlannerTab.TODO_LIST
            AppScreen.NOTES -> _currentTab.value = PlannerTab.PERSONAL_NOTES
            AppScreen.FUTURE_PLANS -> _currentTab.value = PlannerTab.FUTURE_PLANS
            AppScreen.REMIND_ME -> _currentTab.value = PlannerTab.CALENDAR_REMINDERS
            AppScreen.HOME_DASHBOARD -> {}
        }
    }

    fun navigateToHome() {
        _currentScreen.value = AppScreen.HOME_DASHBOARD
    }

    fun selectTab(tab: PlannerTab) {
        _currentTab.value = tab
        _currentScreen.value = when (tab) {
            PlannerTab.TODO_LIST -> AppScreen.DAILY_TASK
            PlannerTab.PERSONAL_NOTES -> AppScreen.NOTES
            PlannerTab.FUTURE_PLANS -> AppScreen.FUTURE_PLANS
            PlannerTab.CALENDAR_REMINDERS -> AppScreen.REMIND_ME
        }
    }

    // Goals actions
    fun setGoalFilter(filter: GoalFilter) {
        _goalFilter.value = filter
    }

    fun setGoalStatus(goalId: Long, status: GoalStatus) {
        viewModelScope.launch {
            repository.updateGoalStatus(goalId, status)
        }
    }

    fun addGoal(title: String, targetDate: String, isLongTerm: Boolean, category: String, notes: String) {
        viewModelScope.launch {
            repository.insertGoal(
                GoalEntity(
                    title = title,
                    targetDate = targetDate,
                    isLongTerm = isLongTerm,
                    category = category,
                    notes = notes
                )
            )
            _showAddGoalDialog.value = false
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun updateGoal(goal: GoalEntity) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun setShowAddGoalDialog(show: Boolean) {
        _showAddGoalDialog.value = show
    }

    // Tasks actions
    fun setTaskFilter(filter: TaskFilter) {
        _taskFilter.value = filter
    }

    fun toggleTask(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateTaskCompleted(taskId, isCompleted)
        }
    }

    fun addTask(title: String, dueDate: String, priority: TaskPriority, category: String) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    dueDate = dueDate,
                    priority = priority,
                    category = category
                )
            )
            _showAddTaskDialog.value = false
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun setShowAddTaskDialog(show: Boolean) {
        _showAddTaskDialog.value = show
    }

    // Notes actions
    fun setNoteSearchQuery(query: String) {
        _noteSearchQuery.value = query
    }

    fun addOrUpdateNote(title: String, content: String, category: String, existingId: Long? = null) {
        viewModelScope.launch {
            if (existingId != null && existingId > 0) {
                repository.updateNote(
                    NoteEntity(
                        id = existingId,
                        title = title,
                        content = content,
                        category = category,
                        updatedAt = System.currentTimeMillis()
                    )
                )
            } else {
                repository.insertNote(
                    NoteEntity(
                        title = title,
                        content = content,
                        category = category,
                        createdAt = System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                )
            }
            _showAddNoteDialog.value = false
            _editingNote.value = null
        }
    }

    fun deleteNote(note: NoteEntity) {
        viewModelScope.launch {
            repository.deleteNote(note)
        }
    }

    fun openEditNote(note: NoteEntity) {
        _editingNote.value = note
        _showAddNoteDialog.value = true
    }

    fun setShowAddNoteDialog(show: Boolean) {
        if (!show) _editingNote.value = null
        _showAddNoteDialog.value = show
    }

    // Calendar actions
    fun selectDate(date: String) {
        _selectedDate.value = date
    }

    fun addEvent(
        title: String,
        date: String,
        time: String,
        category: String,
        reminderEnabled: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertEvent(
                CalendarEventEntity(
                    title = title,
                    date = date,
                    time = time,
                    category = category,
                    reminderEnabled = reminderEnabled,
                    notes = notes
                )
            )
            if (reminderEnabled) {
                NotificationHelper.showReminderNotification(
                    getApplication(),
                    "Reminder: $title",
                    "Scheduled for $date at $time ($category)"
                )
            }
            _showAddEventDialog.value = false
        }
    }

    fun toggleEventCompleted(eventId: Long, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateEventCompleted(eventId, isCompleted)
        }
    }

    fun deleteEvent(event: CalendarEventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
        }
    }

    fun setShowAddEventDialog(show: Boolean) {
        _showAddEventDialog.value = show
    }
}
