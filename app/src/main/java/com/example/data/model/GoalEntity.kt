package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class GoalStatus {
    IN_PROGRESS,
    DONE,
    INCOMPLETE
}

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetDate: String, // e.g. "2026-11-15"
    val isLongTerm: Boolean, // false = Short-term, true = Long-term
    val category: String = "Personal", // Career, Health, Finance, Learning, Personal
    val status: GoalStatus = GoalStatus.IN_PROGRESS,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
