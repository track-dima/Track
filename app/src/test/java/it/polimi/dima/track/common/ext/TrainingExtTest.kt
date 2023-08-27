package it.polimi.dima.track.common.ext

import it.polimi.dima.track.model.Training
import it.polimi.dima.track.model.TrainingStep
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Assert
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrainingExtTest {
  private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

  /*
   * HAS DUE DATE TESTS
   */

  @Test
  fun hasDueDate() {
    val training = Training(dueDate = Date(), dueDateString = "01/01/2023")
    assertTrue(training.hasDueDate())
  }

  @Test
  fun hasDueDate_BlankDueDateString() {
    val training = Training(dueDate = Date())
    assertFalse(training.hasDueDate())
  }

  @Test
  fun hasDueDate_NullDueDate() {
    val training = Training(dueDateString = "01/01/2023")
    assertFalse(training.hasDueDate())
  }

  @Test
  fun hasDueDate_NullDueDateAndBlankDueDateString() {
    val training = Training()
    assertFalse(training.hasDueDate())
  }

  @Test
  fun hasDueDate_NullTraining() {
    assertFalse(null.hasDueDate())
  }

  /*
   * HAS DUE TIME TESTS
   */

  @Test
  fun hasDueTime() {
    val training = Training(dueTime = mapOf("hour" to 10, "minute" to 30), dueTimeString = "10:30")
    assertTrue(training.hasDueTime())
  }

  @Test
  fun hasDueTime_BlankDueTimeString() {
    val training = Training(dueTime = mapOf("hour" to 10, "minute" to 30))
    assertFalse(training.hasDueTime())
  }

  @Test
  fun hasDueTime_NullDueTime() {
    val training = Training(dueTimeString = "10:30")
    assertFalse(training.hasDueTime())
  }

  @Test
  fun hasDueTime_NullDueTimeAndBlankDueTimeString() {
    val training = Training()
    assertFalse(training.hasDueTime())
  }

  @Test
  fun hasDueTime_NullTraining() {
    assertFalse(null.hasDueTime())
  }

  /*
   * GET COMPLETE TIME TESTS
   */

  @Test
  fun getCompleteTime() {
    val training = Training(
      dueDate = dateFormat.parse("01/01/2023"),
      dueTime = mapOf("hour" to 10, "minute" to 30)
    )
    val expected = 1672482600000
    assertEquals(expected, training.getCompleteTime())
  }

  @Test
  fun getCompleteTime_NullDueDate() {
    val training = Training(dueTime = mapOf("hour" to 10, "minute" to 30))
    val expected = 10 * 60 * 60 * 1000 + 30 * 60 * 1000
    assertEquals(expected.toLong(), training.getCompleteTime())
  }

  @Test
  fun getCompleteTime_NullTraining() {
    assertEquals(0, null.getCompleteTime())
  }

  /*
   * IS SCHEDULED TESTS
   */

  @Test
  fun isScheduled_Tomorrow() {
    val training = Training(
      dueDate = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000),
      dueDateString = "01/01/2023"
    )
    assertTrue(training.isScheduled())
  }

  @Test
  fun isScheduled_Today() {
    val training = Training(dueDate = Date(), dueDateString = "01/01/2023")
    assertFalse(training.isScheduled())
  }

  @Test
  fun isScheduled_Yesterday() {
    val training = Training(
      dueDate = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000),
      dueDateString = "01/01/2023"
    )
    assertFalse(training.isScheduled())
  }

  @Test
  fun isScheduled_NullDueDate() {
    val training = Training()
    assertFalse(training.isScheduled())
  }

  /*
   * GET DUE DATE AND TIME TESTS
   */

  @Test
  fun getDueDateAndTime() {
    val training = Training(
      dueDate = dateFormat.parse("01/01/2023"),
      dueDateString = "01/01/2023",
      dueTime = mapOf("hour" to 10, "minute" to 30),
      dueTimeString = "10:30"
    )
    val expected = "01/01/2023 at 10:30"
    assertEquals(expected, training.getDueDateAndTime())
  }

  @Test
  fun getDueDateAndTime_NullDueDate() {
    val training = Training(
      dueTime = mapOf("hour" to 10, "minute" to 30),
      dueTimeString = "10:30"
    )
    val expected = "at 10:30"
    assertEquals(expected, training.getDueDateAndTime())
  }

  @Test
  fun getDueDateAndTime_NullDueTime() {
    val training = Training(
      dueDate = dateFormat.parse("01/01/2023"),
      dueDateString = "01/01/2023"
    )
    val expected = "01/01/2023"
    assertEquals(expected, training.getDueDateAndTime())
  }

  @Test
  fun getDueDateAndTime_NullDueDateAndDueTime() {
    val training = Training()
    val expected = ""
    assertEquals(expected, training.getDueDateAndTime())
  }

  /*
   * CALCULATE REPETITIONS TESTS
   */

  @Test
  fun calculateRepetitions() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(
          type = TrainingStep.Type.REPETITION_BLOCK,
          repetitions = 3,
          stepsInRepetition = listOf(
            TrainingStep(type = TrainingStep.Type.REPETITION),
            TrainingStep(type = TrainingStep.Type.REPETITION)
          )
        )
      )
    )
    val training = Training(
      trainingSteps = listOf(
        step,
        TrainingStep(type = TrainingStep.Type.REPETITION),
      )
    )
    assertEquals(17, training.calculateRepetitions())
  }

  @Test
  fun calculateRepetitions_EmptyTraining() {
    val training = Training()
    assertEquals(0, training.calculateRepetitions())
  }

  /*
   * CALCULATE TREE TESTS
   */

  @Test
  fun calculateTree() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(
          type = TrainingStep.Type.REPETITION_BLOCK,
          repetitions = 3,
          stepsInRepetition = listOf(
            TrainingStep(type = TrainingStep.Type.REPETITION),
            TrainingStep(type = TrainingStep.Type.REPETITION)
          )
        )
      )
    )
    val training = Training(
      trainingSteps = listOf(
        step,
        TrainingStep(type = TrainingStep.Type.REPETITION),
      )
    )
    assertEquals(Pair(5, 2), training.calculateTree())
  }

  @Test
  fun calculateTree_EmptyTraining() {
    val training = Training()
    assertEquals(Pair(0, 0), training.calculateTree())
  }

  /*
   * CALCULATE TOTAL TIME TESTS
   */

  @Test
  fun calculateTotalTime() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30,
      extraRecoverType = TrainingStep.DurationType.TIME,
      extraRecoverDuration = 60,
      stepsInRepetition = listOf(
        TrainingStep(
          id = "1",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        ),
        TrainingStep(
          id = "2",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 60,
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )
    val training = Training(
      trainingSteps = listOf(
        step,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )
    assertEquals(1710, training.calculateTotalTime())
  }

  @Test
  fun calculateTotalTime_EmptyTraining() {
    val training = Training()
    assertEquals(0, training.calculateTotalTime())
  }

  /*
   * PARSE TRAINING STEPS TESTS
   */

  @Test
fun parseTrainingSteps() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30,
      extraRecoverType = TrainingStep.DurationType.TIME,
      extraRecoverDuration = 60,
      stepsInRepetition = listOf(
        TrainingStep(
          id = "1",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        ),
        TrainingStep(
          id = "2",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )
    val training = Training(
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        step,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )
    val expected = "• Warm up\n" +
        "• 2 sets of:\n" +
        "\t\t\t◦ 1000m with 1000m recovery\n" +
        "\t\t\t◦ 1000m with 30'' recovery\n" +
        "\t\t\t◦ 1' recovery after the sets\n" +
        "• 5:00"
    assertEquals(expected, training.parseTrainingSteps())
  }

  @Test
  fun parseTrainingSteps_EmptyTraining() {
    val training = Training()
    val expected = ""
    assertEquals(expected, training.parseTrainingSteps())
  }

  /*
   * PARSE TRAINING TESTS
   */

  @Test
  fun parseTraining() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30,
      extraRecoverType = TrainingStep.DurationType.TIME,
      extraRecoverDuration = 60,
      stepsInRepetition = listOf(
        TrainingStep(
          id = "1",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        ),
        TrainingStep(
          id = "2",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )
    val training = Training(
      title = "Training",
      dueDate = dateFormat.parse("01/01/2023"),
      dueDateString = "01/01/2023",
      dueTime = mapOf("hour" to 10, "minute" to 30),
      dueTimeString = "10:30",
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        step,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )
    val expected = "Training\n" +
        "01/01/2023 at 10:30\n\n" +
        "• Warm up\n" +
        "• 2 sets of:\n" +
        "\t\t\t◦ 1000m with 1000m recovery\n" +
        "\t\t\t◦ 1000m with 30'' recovery\n" +
        "\t\t\t◦ 1' recovery after the sets\n" +
        "• 5:00"
    assertEquals(expected, training.parseTraining())
  }

  @Test
  fun parseTraining_NullDueDateAndDueTime() {
    val training = Training(
      title = "Training",
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )
    val expected = "Training\n\n" +
        "• Warm up\n" +
        "• 5:00"
    assertEquals(expected, training.parseTraining())
  }

  @Test
  fun parseTraining_EmptyTraining() {
    val training = Training()
    val expected = ""
    assertEquals(expected, training.parseTraining())
  }

  /*
   * CALCULATE SEARCH TOKENS TESTS
   */

  @Test
  fun calculateSearchTokens() {
    val training = Training(
      title = "Training",
      description = "Description",
      notes = "Notes",
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )
    val expected = listOf("training", "description", "notes", "5:00")
    assertEquals(expected, training.calculateSearchTokens())
  }

  @Test
  fun calculateSearchTokens_EmptyTraining() {
    val training = Training()
    val expected = listOf<String>()
    assertEquals(expected, training.calculateSearchTokens())
  }

  /*
   * GET BEST RESULTS TESTS
   */

  @Test
  fun getBestResults() {
    val step1 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:30", "1:40", "1:20")
    )
    val step2 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:25", "1:45", "1:15")
    )
    val step3 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 90,
      results = listOf("3:30 min/km", "3:10 min/km", "3:25 min/km")
    )
    val step4 = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(step1, step2, step3)
    )

    val training = Training(
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        step4,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 90,
          results = listOf("3:30 min/mi")
        )
      )
    )

    val (bestTimeResults, bestPaceResults) = training.getBestResults()

    assertEquals("3:30 min/mi", bestPaceResults[90])
    assertEquals("1:15", bestTimeResults[500])
  }

  @Test
  fun getBestResults_EmptyTraining() {
    val training = Training()
    val (bestTimeResults, bestPaceResults) = training.getBestResults()
    assertEquals(0, bestTimeResults.size)
    assertEquals(0, bestPaceResults.size)
  }

  /*
   * EMPTY RESULTS TESTS
   */

  @Test
  fun emptyResults() {
    val step1 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:30", "1:40", "1:20")
    )
    val step2 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:25", "1:45", "1:15")
    )
    val step3 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 90,
      results = listOf("3:30 min/km", "3:10 min/km", "3:25 min/km")
    )
    val step4 = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(step1, step2, step3)
    )

    val training = Training(
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        step4,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 90,
          results = listOf("3:30 min/mi")
        )
      )
    )

    val expected = listOf(
      TrainingStep(
        type = TrainingStep.Type.WARM_UP
      ),
      TrainingStep(
        type = TrainingStep.Type.REPETITION_BLOCK,
        repetitions = 3,
        stepsInRepetition = listOf(
          TrainingStep(
            type = TrainingStep.Type.REPETITION,
            durationType = TrainingStep.DurationType.DISTANCE,
            distance = 500,
            distanceUnit = "m",
            results = listOf()
          ),
          TrainingStep(
            type = TrainingStep.Type.REPETITION,
            durationType = TrainingStep.DurationType.DISTANCE,
            distance = 500,
            distanceUnit = "m",
            results = listOf()
          ),
          TrainingStep(
            type = TrainingStep.Type.REPETITION,
            durationType = TrainingStep.DurationType.TIME,
            duration = 90,
            results = listOf()
          )
        )
      ),
      TrainingStep(
        type = TrainingStep.Type.REPETITION,
        durationType = TrainingStep.DurationType.TIME,
        duration = 90,
        results = listOf()
      )
    )

    assertEquals(expected, emptyResults(training.trainingSteps))
  }

  @Test
  fun emptyResults_EmptyTraining() {
    val training = Training()
    val expected = listOf<TrainingStep>()
    assertEquals(expected, emptyResults(training.trainingSteps))
  }

  /*
   * PERFORMANCE TESTS
   */

  @Test
  fun performanceTest_hasDueDate() {
    val startTime = System.currentTimeMillis()
    val training = Training(dueDate = Date(), dueDateString = "01/01/2023")

    for (i in 1..10000) {
      training.hasDueDate()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_hasDueTime() {
    val startTime = System.currentTimeMillis()
    val training = Training(dueTime = mapOf("hour" to 10, "minute" to 30), dueTimeString = "10:30")

    for (i in 1..10000) {
      training.hasDueTime()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_getCompleteTime() {
    val startTime = System.currentTimeMillis()
    val training = Training(
      dueDate = dateFormat.parse("01/01/2023"),
      dueTime = mapOf("hour" to 10, "minute" to 30)
    )

    for (i in 1..10000) {
      training.getCompleteTime()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_isScheduled() {
    val startTime = System.currentTimeMillis()
    val training = Training(
      dueDate = Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000),
      dueDateString = "01/01/2023"
    )

    for (i in 1..10000) {
      training.isScheduled()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_getDueDateAndTime() {
    val startTime = System.currentTimeMillis()
    val training = Training(
      dueDate = dateFormat.parse("01/01/2023"),
      dueDateString = "01/01/2023",
      dueTime = mapOf("hour" to 10, "minute" to 30),
      dueTimeString = "10:30"
    )

    for (i in 1..10000) {
      training.getDueDateAndTime()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_calculateRepetitions() {
    val startTime = System.currentTimeMillis()
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(
          type = TrainingStep.Type.REPETITION_BLOCK,
          repetitions = 3,
          stepsInRepetition = listOf(
            TrainingStep(type = TrainingStep.Type.REPETITION),
            TrainingStep(type = TrainingStep.Type.REPETITION)
          )
        )
      )
    )
    val training = Training(
      trainingSteps = listOf(
        step,
        TrainingStep(type = TrainingStep.Type.REPETITION),
      )
    )

    for (i in 1..10000) {
      training.calculateRepetitions()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_calculateTree() {
    val startTime = System.currentTimeMillis()
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(
          type = TrainingStep.Type.REPETITION_BLOCK,
          repetitions = 3,
          stepsInRepetition = listOf(
            TrainingStep(type = TrainingStep.Type.REPETITION),
            TrainingStep(type = TrainingStep.Type.REPETITION)
          )
        )
      )
    )
    val training = Training(
      trainingSteps = listOf(
        step,
        TrainingStep(type = TrainingStep.Type.REPETITION),
      )
    )

    for (i in 1..10000) {
      training.calculateTree()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_calculateTotalTime() {
    val startTime = System.currentTimeMillis()
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30,
      extraRecoverType = TrainingStep.DurationType.TIME,
      extraRecoverDuration = 60,
      stepsInRepetition = listOf(
        TrainingStep(
          id = "1",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        ),
        TrainingStep(
          id = "2",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 60,
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )
    val training = Training(
      trainingSteps = listOf(
        step,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )

    for (i in 1..10000) {
      training.calculateTotalTime()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_parseTrainingSteps() {
    val startTime = System.currentTimeMillis()
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30,
      extraRecoverType = TrainingStep.DurationType.TIME,
      extraRecoverDuration = 60,
      stepsInRepetition = listOf(
        TrainingStep(
          id = "1",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        ),
        TrainingStep(
          id = "2",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )
    val training = Training(
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        step,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )

    for (i in 1..10000) {
      training.parseTrainingSteps()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for parseTrainingSteps: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_parseTraining() {
    val startTime = System.currentTimeMillis()
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 2,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30,
      extraRecoverType = TrainingStep.DurationType.TIME,
      extraRecoverDuration = 60,
      stepsInRepetition = listOf(
        TrainingStep(
          id = "1",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        ),
        TrainingStep(
          id = "2",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 1000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )
    val training = Training(
      title = "Training",
      dueDate = dateFormat.parse("01/01/2023"),
      dueDateString = "01/01/2023",
      dueTime = mapOf("hour" to 10, "minute" to 30),
      dueTimeString = "10:30",
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        step,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )

    for (i in 1..10000) {
      training.parseTraining()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for parseTraining: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
  fun performanceTest_calculateSearchTokens() {
    val startTime = System.currentTimeMillis()
    val training = Training(
      title = "Training",
      description = "Description",
      notes = "Notes",
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 300,
          recoverType = TrainingStep.DurationType.TIME,
          recoverDuration = 30
        )
      )
    )

    for (i in 1..10000) {
      training.calculateSearchTokens()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for calculateSearchTokens: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }

  @Test
fun performanceTest_getBestResults() {
    val startTime = System.currentTimeMillis()
    val step1 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:30", "1:40", "1:20")
    )
    val step2 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:25", "1:45", "1:15")
    )
    val step3 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 90,
      results = listOf("3:30 min/km", "3:10 min/km", "3:25 min/km")
    )
    val step4 = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(step1, step2, step3)
    )

    val training = Training(
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        step4,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 90,
          results = listOf("3:30 min/mi")
        )
      )
    )

    for (i in 1..10000) {
      training.getBestResults()
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for getBestResults: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 10000
    )
  }

  @Test
  fun performanceTest_emptyResults() {
    val startTime = System.currentTimeMillis()
    val step1 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:30", "1:40", "1:20")
    )
    val step2 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 500,
      distanceUnit = "m",
      results = listOf("1:25", "1:45", "1:15")
    )
    val step3 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 90,
      results = listOf("3:30 min/km", "3:10 min/km", "3:25 min/km")
    )
    val step4 = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(step1, step2, step3)
    )

    val training = Training(
      trainingSteps = listOf(
        TrainingStep(
          type = TrainingStep.Type.WARM_UP
        ),
        step4,
        TrainingStep(
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.TIME,
          duration = 90,
          results = listOf("3:30 min/mi")
        )
      )
    )

    for (i in 1..10000) {
      emptyResults(training.trainingSteps)
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for emptyResults: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 10000
    )
  }
}