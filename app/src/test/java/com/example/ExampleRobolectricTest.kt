package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TAsin ZiEL Bo0k", appName)
  }

  @Test
  fun `goal entity holds done and incomplete states`() {
    val goal = com.example.data.model.GoalEntity(
      title = "Launch App",
      targetDate = "2026-12-31",
      isLongTerm = true,
      category = "Career",
      status = com.example.data.model.GoalStatus.DONE
    )
    assertEquals(com.example.data.model.GoalStatus.DONE, goal.status)
    val incompleteGoal = goal.copy(status = com.example.data.model.GoalStatus.INCOMPLETE)
    assertEquals(com.example.data.model.GoalStatus.INCOMPLETE, incompleteGoal.status)
  }

  @Test
  fun `task entity completion toggle test`() {
    val task = com.example.data.model.TaskEntity(
      title = "Write unit tests",
      dueDate = "2026-10-07",
      isCompleted = false
    )
    assertEquals(false, task.isCompleted)
    val completedTask = task.copy(isCompleted = true)
    assertEquals(true, completedTask.isCompleted)
  }
}
