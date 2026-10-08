package com.example.data.repository

import com.example.data.db.CalendarEventDao
import com.example.data.db.GoalDao
import com.example.data.db.NoteDao
import com.example.data.db.TaskDao
import com.example.data.model.CalendarEventEntity
import com.example.data.model.GoalEntity
import com.example.data.model.GoalStatus
import com.example.data.model.NoteEntity
import com.example.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

class PlannerRepository(
    private val goalDao: GoalDao,
    private val taskDao: TaskDao,
    private val noteDao: NoteDao,
    private val calendarEventDao: CalendarEventDao
) {
    // Goals
    val allGoals: Flow<List<GoalEntity>> = goalDao.getAllGoals()
    suspend fun insertGoal(goal: GoalEntity): Long = goalDao.insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = goalDao.updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = goalDao.deleteGoal(goal)
    suspend fun updateGoalStatus(id: Long, status: GoalStatus) = goalDao.updateGoalStatus(id, status)

    // Tasks
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()
    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)
    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)
    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)
    suspend fun updateTaskCompleted(id: Long, isCompleted: Boolean) = taskDao.updateTaskCompleted(id, isCompleted)

    // Notes
    val allNotes: Flow<List<NoteEntity>> = noteDao.getAllNotes()
    suspend fun insertNote(note: NoteEntity): Long = noteDao.insertNote(note)
    suspend fun updateNote(note: NoteEntity) = noteDao.updateNote(note)
    suspend fun deleteNote(note: NoteEntity) = noteDao.deleteNote(note)

    // Calendar Events
    val allEvents: Flow<List<CalendarEventEntity>> = calendarEventDao.getAllEvents()
    fun getEventsByDate(date: String): Flow<List<CalendarEventEntity>> = calendarEventDao.getEventsByDate(date)
    suspend fun insertEvent(event: CalendarEventEntity): Long = calendarEventDao.insertEvent(event)
    suspend fun updateEvent(event: CalendarEventEntity) = calendarEventDao.updateEvent(event)
    suspend fun deleteEvent(event: CalendarEventEntity) = calendarEventDao.deleteEvent(event)
    suspend fun updateEventCompleted(id: Long, isCompleted: Boolean) = calendarEventDao.updateEventCompleted(id, isCompleted)
}
