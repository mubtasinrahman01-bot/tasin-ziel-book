package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val date: String, // YYYY-MM-DD, e.g. "2026-10-07"
    val time: String, // HH:mm, e.g. "14:30"
    val category: String = "General", // Work, Health, Personal, Meeting
    val reminderEnabled: Boolean = true,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
