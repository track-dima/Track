package it.polimi.dima.track.common.ext

import it.polimi.dima.track.model.TrainingStep
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert
import org.junit.Test

class TrainingStepExtTest {

  /*
   * CALCULATE TREE TESTS
   */

  @Test
  fun calculateTree_OnlyRepetitionBlock() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      stepsInRepetition = emptyList()
    )
    val (normalSteps, repetitionBlocks) = step.calculateTree()
    assertEquals(0, normalSteps)
    assertEquals(1, repetitionBlocks)
  }

  @Test
  fun calculateTree_NormalStep() {
    val step = TrainingStep(type = TrainingStep.Type.EXERCISES)
    val (normalSteps, repetitionBlocks) = step.calculateTree()
    assertEquals(1, normalSteps)
    assertEquals(0, repetitionBlocks)
  }

  @Test
  fun calculateTree_RepetitionBlockWithNormalSteps() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION)
      )
    )
    val (normalSteps, repetitionBlocks) = step.calculateTree()
    assertEquals(3, normalSteps)
    assertEquals(1, repetitionBlocks)
  }

  @Test
  fun calculateTree_RepetitionBlockWithRepetitionBlocks() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(
          type = TrainingStep.Type.REPETITION_BLOCK,
          stepsInRepetition = listOf(
            TrainingStep(type = TrainingStep.Type.REPETITION),
            TrainingStep(type = TrainingStep.Type.REPETITION)
          )
        )
      )
    )
    val (normalSteps, repetitionBlocks) = step.calculateTree()
    assertEquals(4, normalSteps)
    assertEquals(2, repetitionBlocks)
  }

  /*
   * CALCULATE REPETITIONS TESTS
   */

  @Test
  fun calculateRepetitions_OnlyRepetition() {
    val step = TrainingStep(type = TrainingStep.Type.REPETITION)
    assertEquals(1, step.calculateRepetitions())
  }

  @Test
  fun calculateRepetitions_OnlyNoRepetitionStep() {
    val step = TrainingStep(type = TrainingStep.Type.EXERCISES)
    assertEquals(0, step.calculateRepetitions())
  }

  @Test
  fun calculateRepetitions_OnlyRepetitionBlock() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      stepsInRepetition = emptyList()
    )
    assertEquals(0, step.calculateRepetitions())
  }

  @Test
  fun calculateRepetitions_RepetitionBlockWithNormalSteps() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION)
      )
    )
    assertEquals(9, step.calculateRepetitions())
  }

  @Test
  fun calculateRepetitions_RepetitionBlockWithRepetitionBlocks() {
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
    assertEquals(16, step.calculateRepetitions())
  }

  /*
   * CALCULATE TOTAL TIME TESTS
   */

  @Test
  fun calculateTotalTime_OnlyRepetition_DurationTime_RecoverTime_NotLastInBlock() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 60,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30
    )
    assertEquals(90, step.calculateTotalTime(false))
  }

  @Test
  fun calculateTotalTime_OnlyRepetition_DurationTime_RecoverTime_LastInBlock() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 60,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30
    )
    assertEquals(60, step.calculateTotalTime(true))
  }

  @Test
  fun calculateTotalTime_OnlyRepetition_DurationTime_RecoverDistance() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 60,
      recoverType = TrainingStep.DurationType.DISTANCE,
      recoverDistance = 1000,
      recoverDistanceUnit = "m"
    )
    assertEquals(360, step.calculateTotalTime(false))
  }

  @Test
  fun calculateTotalTime_OnlyRepetition_DurationDistance_RecoverTime() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 1000,
      distanceUnit = "m",
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30
    )
    assertEquals(330, step.calculateTotalTime(false))
  }

  @Test
  fun calculateTotalTime_OnlyRepetition_DurationDistance_RecoverDistance() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 1000,
      distanceUnit = "m",
      recoverType = TrainingStep.DurationType.DISTANCE,
      recoverDistance = 1000,
      recoverDistanceUnit = "m"
    )
    assertEquals(600, step.calculateTotalTime(false))
  }

  @Test
  fun calculateTotalTIme_OnlyNoRepetitionStep() {
    val step = TrainingStep(type = TrainingStep.Type.EXERCISES)
    assertEquals(0, step.calculateTotalTime(false))
  }

  @Test
  fun calculateTotalTime_OnlyRepetitionBlock() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      stepsInRepetition = emptyList()
    )
    assertEquals(0, step.calculateTotalTime(false))
  }

  @Test
  fun calculateTotalTime_RepetitionBlockWithRepetitions() {
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
    assertEquals(1890, step.calculateTotalTime(false))
  }

  /*
   * PARSE TO STRING TESTS
   */

  @Test
  fun parseToString_OnlyRepetition() {
    val dot = "•"
    val level = 1
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 60,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30
    )
    val expected = "1:00 with 30'' recovery"
    assertEquals(expected, step.parseToString(level, dot, false))
  }

  @Test
  fun parseToString_OnlyRepetition_LastStep() {
    val dot = "•"
    val level = 1
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 60,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30
    )
    val expected = "1:00"
    assertEquals(expected, step.parseToString(level, dot, true))
  }

  @Test
  fun parseToString_RepetitionBlockWithRepetitions() {
    val dot = "•"
    val level = 1
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
    val expected = "2 sets of:\n" +
        "\t\t\t$dot 1000m with 1000m recovery\n" +
        "\t\t\t$dot 1000m with 30'' recovery\n" +
        "\t\t\t$dot 1' recovery after the sets"
    assertEquals(expected, step.parseToString(level, dot, true))
  }

  /*
   * CALCULATE SEARCH TOKENS TESTS
   */

  @Test
  fun calculateSearchTokens_OnlyRepetition_DurationTime() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 60,
      recoverType = TrainingStep.DurationType.TIME,
      recoverDuration = 30
    )
    val expected = listOf("1:00")
    assertEquals(expected, step.calculateSearchTokens())
  }

  @Test
  fun calculateSearchTokens_OnlyRepetition_DurationDistance() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 1000,
      distanceUnit = "m",
      recoverType = TrainingStep.DurationType.DISTANCE,
      recoverDistance = 1000,
      recoverDistanceUnit = "m"
    )
    val expected = listOf("1000m")
    assertEquals(expected, step.calculateSearchTokens())
  }

  @Test
  fun calculateSearchTokens_OnlyRepetitionBlock() {
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      stepsInRepetition = emptyList()
    )
    val expected = emptyList<String>()
    assertEquals(expected, step.calculateSearchTokens())
  }

  @Test
  fun calculateSearchTokens_RepetitionBlockWithRepetitions() {
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
          distance = 2000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        ),
        TrainingStep(
          id = "2",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 2000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )
    val expected = listOf("1000m", "2000m")
    assertEquals(expected, step.calculateSearchTokens())
  }

  /*
   * GET BEST RESULTS TESTS
   */

  @Test
  fun getBestResults_RepetitionBlockWithSteps_timeResults() {
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
      durationType = TrainingStep.DurationType.DISTANCE,
      distance = 400,
      distanceUnit = "m",
      results = listOf("1:22", "1:42", "1:12")
    )
    val step4 = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(step1, step2, step3)
    )

    val (bestTimeResults, bestPaceResults) = step4.getBestResults()

    assertEquals("1:15", bestTimeResults[500])
    assertEquals("1:12", bestTimeResults[400])

    assertTrue(bestPaceResults.isEmpty())
  }

  @Test
  fun getBestResults_RepetitionBlockWithSteps_paceResults() {
    val step1 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 60,
      results = listOf("3:10 min/km", "3:05 min/km", "3:30 min/km")
    )
    val step2 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 60,
      results = listOf("3:00 min/km", "3:15 min/km", "3:20 min/km")
    )
    val step3 = TrainingStep(
      type = TrainingStep.Type.REPETITION,
      durationType = TrainingStep.DurationType.TIME,
      duration = 90,
      results = listOf("3:30 min/mi", "3:10 min/km", "3:25 min/km")
    )
    val step4 = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
      stepsInRepetition = listOf(step1, step2, step3)
    )

    val (bestTimeResults, bestPaceResults) = step4.getBestResults()

    assertEquals("3:00 min/km", bestPaceResults[60])
    assertEquals("3:30 min/mi", bestPaceResults[90])

    assertTrue(bestTimeResults.isEmpty())
  }

  /*
   * UPDATE BEST TIME RESULT TESTS
   */

  @Test
  fun updateBestTimeResults_OnlyNewResults() {
    val bestResults = mutableMapOf<Int, String>()
    val newBestResults = mapOf(500 to "1:30", 400 to "1:20")
    val expected = mapOf(500 to "1:30", 400 to "1:20")
    assertEquals(expected, updateBestTimeResults(bestResults, newBestResults))
  }

  @Test
  fun updateBestTimeResults_OnlyOldResults() {
    val bestResults = mutableMapOf(500 to "1:30", 400 to "1:20")
    val newBestResults = mapOf<Int, String>()
    val expected = mapOf(500 to "1:30", 400 to "1:20")
    assertEquals(expected, updateBestTimeResults(bestResults, newBestResults))
  }

  @Test
  fun updateBestTimeResults_NewResultsBetterThanOldResults() {
    val bestResults = mutableMapOf(500 to "1:30", 400 to "1:20")
    val newBestResults = mapOf(500 to "1:25", 400 to "1:15")
    val expected = mapOf(500 to "1:25", 400 to "1:15")
    assertEquals(expected, updateBestTimeResults(bestResults, newBestResults))
  }

  @Test
  fun updateBestTimeResults_NewResultsWorseThanOldResults() {
    val bestResults = mutableMapOf(500 to "1:25", 400 to "1:15")
    val newBestResults = mapOf(500 to "1:30", 400 to "1:20")
    val expected = mapOf(500 to "1:25", 400 to "1:15")
    assertEquals(expected, updateBestTimeResults(bestResults, newBestResults))
  }

  @Test
  fun updateBestTimeResults_NewResultsBetterThanSomeOldResults() {
    val bestResults = mutableMapOf(500 to "1:30", 400 to "1:20")
    val newBestResults = mapOf(500 to "1:25", 300 to "1:15")
    val expected = mapOf(500 to "1:25", 400 to "1:20", 300 to "1:15")
    assertEquals(expected, updateBestTimeResults(bestResults, newBestResults))
  }

  /*
   * UPDATE BEST PACE RESULT TESTS
   */

  @Test
  fun updateBestPaceResults_OnlyNewResults() {
    val bestResults = mutableMapOf<Int, String>()
    val newBestResults = mapOf(60 to "3:10 min/km", 90 to "3:30 min/mi")
    val expected = mapOf(60 to "3:10 min/km", 90 to "3:30 min/mi")
    assertEquals(expected, updateBestPaceResults(bestResults, newBestResults))
  }

  @Test
  fun updateBestPaceResults_OnlyOldResults() {
    val bestResults = mutableMapOf(60 to "3:10 min/km", 90 to "3:30 min/mi")
    val newBestResults = mapOf<Int, String>()
    val expected = mapOf(60 to "3:10 min/km", 90 to "3:30 min/mi")
    assertEquals(expected, updateBestPaceResults(bestResults, newBestResults))
  }

  @Test
  fun updateBestPaceResults_NewResultsBetterThanOldResults() {
    val bestResults = mutableMapOf(60 to "3:10 min/km", 90 to "3:30 min/mi")
    val newBestResults = mapOf(60 to "3:20 min/mi", 90 to "3:20 min/mi")
    val expected = mapOf(60 to "3:20 min/mi", 90 to "3:20 min/mi")
    assertEquals(expected, updateBestPaceResults(bestResults, newBestResults))
  }

  @Test
  fun updateBestPaceResults_NewResultsWorseThanOldResults() {
    val bestResults = mutableMapOf(60 to "3:00 min/km", 90 to "3:20 min/mi")
    val newBestResults = mapOf(60 to "3:10 min/km", 90 to "3:30 min/mi")
    val expected = mapOf(60 to "3:00 min/km", 90 to "3:20 min/mi")
    assertEquals(expected, updateBestPaceResults(bestResults, newBestResults))
  }

  @Test
  fun updateBestPaceResults_NewResultsBetterThanSomeOldResults() {
    val bestResults = mutableMapOf(60 to "3:10 min/km", 90 to "3:30 min/mi")
    val newBestResults = mapOf(60 to "3:00 min/km", 120 to "3:20 min/mi")
    val expected = mapOf(60 to "3:00 min/km", 90 to "3:30 min/mi", 120 to "3:20 min/mi")
    assertEquals(expected, updateBestPaceResults(bestResults, newBestResults))
  }

  /*
   * PERFORMANCE TESTS
   */

  @Test
  fun performanceTest_calculateTree() {
    val startTime = System.currentTimeMillis()
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(
          type = TrainingStep.Type.REPETITION_BLOCK,
          stepsInRepetition = listOf(
            TrainingStep(type = TrainingStep.Type.REPETITION),
            TrainingStep(type = TrainingStep.Type.REPETITION)
          )
        )
      )
    )

    for (i in 1..10000) {
      step.calculateTree()
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
      stepsInRepetition = listOf(
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(type = TrainingStep.Type.REPETITION),
        TrainingStep(
          type = TrainingStep.Type.REPETITION_BLOCK,
          stepsInRepetition = listOf(
            TrainingStep(type = TrainingStep.Type.REPETITION),
            TrainingStep(type = TrainingStep.Type.REPETITION)
          )
        )
      )
    )

    for (i in 1..10000) {
      step.calculateRepetitions()
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
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 2000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )

    for (i in 1..10000) {
      step.calculateTotalTime(false)
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
  fun performanceTest_parseToString() {
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
          distance = 2000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )

    for (i in 1..10000) {
      step.parseToString(1, "•", false)
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
  fun performanceTest_calculateSearchTokens() {
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
          distance = 2000,
          distanceUnit = "m",
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )

    for (i in 1..10000) {
      step.calculateSearchTokens()
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
  fun performanceTest_getBestResults() {
    val startTime = System.currentTimeMillis()
    val step = TrainingStep(
      type = TrainingStep.Type.REPETITION_BLOCK,
      repetitions = 3,
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
          results = listOf("1:30", "1:40", "1:20"),
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        ),
        TrainingStep(
          id = "2",
          type = TrainingStep.Type.REPETITION,
          durationType = TrainingStep.DurationType.DISTANCE,
          distance = 2000,
          distanceUnit = "m",
          results = listOf("1:25", "1:45", "1:15"),
          recoverType = TrainingStep.DurationType.DISTANCE,
          recoverDistance = 1000,
          recoverDistanceUnit = "m"
        )
      )
    )

    for (i in 1..10000) {
      step.getBestResults()
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
  fun performanceTest_updateBestTimeResults() {
    val startTime = System.currentTimeMillis()
    val bestResults = mutableMapOf(500 to "1:30", 400 to "1:20")
    val newBestResults = mapOf(500 to "1:25", 300 to "1:15")

    for (i in 1..10000) {
      updateBestTimeResults(bestResults, newBestResults)
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
  fun performanceTest_updateBestPaceResults() {
    val startTime = System.currentTimeMillis()
    val bestResults = mutableMapOf(60 to "3:10 min/km", 90 to "3:30 min/mi")
    val newBestResults = mapOf(60 to "3:00 min/km", 120 to "3:20 min/mi")

    for (i in 1..10000) {
      updateBestPaceResults(bestResults, newBestResults)
    }

    val endTime = System.currentTimeMillis()
    val executionTime = endTime - startTime
    println("Performance test execution time for extractCents: $executionTime ms")
    Assert.assertTrue(
      "Performance test failed: execution time was $executionTime ms",
      executionTime < 1000
    )
  }
}