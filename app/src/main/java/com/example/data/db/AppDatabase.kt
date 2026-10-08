package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.CalendarEventEntity
import com.example.data.model.GoalEntity
import com.example.data.model.GoalStatus
import com.example.data.model.NoteEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        GoalEntity::class,
        TaskEntity::class,
        NoteEntity::class,
        CalendarEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao
    abstract fun taskDao(): TaskDao
    abstract fun noteDao(): NoteDao
    abstract fun calendarEventDao(): CalendarEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "plan_pulse_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        prepopulateDatabase(database)
                    }
                }
            }
        }

        suspend fun prepopulateDatabase(db: AppDatabase) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = sdf.format(Date())

            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 3)
            val in3Days = sdf.format(cal.time)

            cal.add(Calendar.DAY_OF_YEAR, 25)
            val nextMonth = sdf.format(cal.time)

            cal.add(Calendar.DAY_OF_YEAR, 180)
            val nextYear = sdf.format(cal.time)

            // Prepopulate Initial Sample Goals (Bengali & English)
            val sampleGoals = listOf(
                GoalEntity(
                    title = "বাংলায় ব্লগ ও প্রযুক্তি আর্টিকেল লেখা শুরু করা",
                    targetDate = in3Days,
                    isLongTerm = false,
                    category = "Career",
                    status = GoalStatus.DONE,
                    notes = "প্রতি সপ্তাহে ২টি মানসম্মত প্রযুক্তি নিবন্ধ প্রকাশ।"
                ),
                GoalEntity(
                    title = "Complete Jetpack Compose & Android Mastery",
                    targetDate = nextMonth,
                    isLongTerm = false,
                    category = "Learning",
                    status = GoalStatus.IN_PROGRESS,
                    notes = "Architecture, Room, Flow, and Material Design 3 guidelines."
                ),
                GoalEntity(
                    title = "নিয়মিত শরীরচর্চা ও সুস্বাস্থ্য বজায় রাখা (5K Run)",
                    targetDate = nextMonth,
                    isLongTerm = false,
                    category = "Health",
                    status = GoalStatus.IN_PROGRESS,
                    notes = "সপ্তাহে ৪ দিন কার্ডিও ও পুষ্টিকর খাবার গ্রহণ।"
                ),
                GoalEntity(
                    title = "Build & Launch SaaS Product Platform",
                    targetDate = nextYear,
                    isLongTerm = true,
                    category = "Business",
                    status = GoalStatus.IN_PROGRESS,
                    notes = "Reach first 1,000 active users with sustainable revenue."
                ),
                GoalEntity(
                    title = "জরুরি সঞ্চয় তহবিল গঠন (Emergency Fund)",
                    targetDate = nextYear,
                    isLongTerm = true,
                    category = "Finance",
                    status = GoalStatus.DONE,
                    notes = "৬ মাসের পারিবারিক খরচের সমান সঞ্চয় নিশ্চিত করা।"
                )
            )
            db.goalDao().insertGoals(sampleGoals)

            // Prepopulate Tasks (Bengali & English)
            val sampleTasks = listOf(
                TaskEntity(
                    title = "সকালের জরুরি কাজের তালিকা (MITs) প্রস্তুত করা",
                    dueDate = today,
                    isCompleted = true,
                    priority = TaskPriority.HIGH,
                    category = "Daily"
                ),
                TaskEntity(
                    title = "Review product roadmap & sprint priorities",
                    dueDate = today,
                    isCompleted = false,
                    priority = TaskPriority.HIGH,
                    category = "Work"
                ),
                TaskEntity(
                    title = "৩০ মিনিট বই পড়া ও নোট নেওয়া",
                    dueDate = today,
                    isCompleted = false,
                    priority = TaskPriority.MEDIUM,
                    category = "Learning"
                ),
                TaskEntity(
                    title = "Evening mobility and relaxation routine",
                    dueDate = in3Days,
                    isCompleted = false,
                    priority = TaskPriority.LOW,
                    category = "Health"
                )
            )
            db.taskDao().insertTasks(sampleTasks)

            // Prepopulate Notes / Journal Entries (Bengali & English)
            val sampleNotes = listOf(
                NoteEntity(
                    title = "আজকের আত্মদর্শন ও শিক্ষা (Daily Reflection)",
                    content = "স্পষ্ট লক্ষ্য এবং একাগ্রতা জীবনের সবচেয়ে বড় শক্তি। বিভ্রান্তি দূর করে মূল কাজগুলোতে সময় দিলে উৎপাদনশীলতা বহুগুণ বৃদ্ধি পায়।",
                    category = "Daily Log",
                    createdAt = System.currentTimeMillis() - 86400000L,
                    updatedAt = System.currentTimeMillis() - 86400000L
                ),
                NoteEntity(
                    title = "Atomic Habits & System Design",
                    content = "1. Make good habits obvious and frictionless.\n2. Keep daily review under 5 minutes.\n3. Celebrate small consistent wins every evening.",
                    category = "Ideas",
                    createdAt = System.currentTimeMillis() - 172800000L,
                    updatedAt = System.currentTimeMillis() - 172800000L
                ),
                NoteEntity(
                    title = "কৃতজ্ঞতা ও সাপ্তাহিক অর্জন",
                    content = "এই সপ্তাহে সফলভাবে নতুন প্রকল্পে কাজ সম্পন্ন হয়েছে। পরিবার ও কাজের মধ্যে ভারসাম্য বজায় রাখার অনুপ্রেরণা বজায় থাকুক।",
                    category = "Reflection",
                    createdAt = System.currentTimeMillis() - 259200000L,
                    updatedAt = System.currentTimeMillis() - 259200000L
                )
            )
            db.noteDao().insertNotes(sampleNotes)

            // Prepopulate Calendar Events (Bengali & English)
            val sampleEvents = listOf(
                CalendarEventEntity(
                    title = "টিম স্ট্র্যাটেজি ও পরিকল্পনা মিটিং",
                    date = today,
                    time = "10:00",
                    category = "Meeting",
                    reminderEnabled = true,
                    isCompleted = true,
                    notes = "পরবর্তী কোয়ার্টারের লক্ষ্য নির্ধারণ"
                ),
                CalendarEventEntity(
                    title = "Deep Focus Work & Coding Session",
                    date = today,
                    time = "14:30",
                    category = "Work",
                    reminderEnabled = true,
                    isCompleted = false,
                    notes = "Feature development and testing"
                ),
                CalendarEventEntity(
                    title = "ব্যায়াম ও সাঁতার (Fitness & Swimming)",
                    date = today,
                    time = "18:00",
                    category = "Health",
                    reminderEnabled = true,
                    isCompleted = false,
                    notes = "স্বাস্থ্য ও মানসিক সতেজতা"
                ),
                CalendarEventEntity(
                    title = "Monthly Goal & Budget Review",
                    date = in3Days,
                    time = "11:00",
                    category = "Personal",
                    reminderEnabled = true,
                    isCompleted = false,
                    notes = "Review spending and future milestones"
                )
            )
            db.calendarEventDao().insertEvents(sampleEvents)
        }
    }
}
