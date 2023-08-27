package it.polimi.dima.track.common.ext

import org.junit.Test
import org.junit.Assert.*

class IntExtTest {

    /*
     * SECONDS TO HH:MM:SS TESTS
     */

    @Test
    fun secondsToHhMmSs() {
        val seconds = 36065 // 10 hour, 1 minute, 5 seconds
        val expected = "10:01:05"
        assertEquals(expected, seconds.secondsToHhMmSs())
    }

    @Test
    fun secondsToHhMmSs_lessThan10Hours() {
        val seconds = 3665 // 1 hour, 1 minute, 5 seconds
        val expected = "1:01:05"
        assertEquals(expected, seconds.secondsToHhMmSs())
    }

    @Test
    fun secondsToHhMmSs_OnlyMinutes() {
        val seconds = 605 // 10 minute, 5 seconds
        val expected = "10:05"
        assertEquals(expected, seconds.secondsToHhMmSs())
    }

    @Test
    fun secondsToHhMmSs_OnlyMinutes_lessThan10() {
        val seconds = 65 // 1 minute, 5 seconds
        val expected = "1:05"
        assertEquals(expected, seconds.secondsToHhMmSs())
    }

    @Test
    fun secondsToHhMmSs_OnlySeconds() {
        val seconds = 5
        val expected = "0:05"
        assertEquals(expected, seconds.secondsToHhMmSs())
    }

    @Test
    fun secondsToHhMmSs_Zero() {
        val seconds = 0
        val expected = "0:00"
        assertEquals(expected, seconds.secondsToHhMmSs())
    }

    /*
     * SECONDS TO HH:MM TESTS
     */

    @Test
    fun secondsToHhMm() {
        val seconds = 36065 // 10 hour, 1 minute, 5 seconds
        val expected = "10:01"
        assertEquals(expected, seconds.secondsToHhMm())
    }

    @Test
    fun secondsToHhMm_lessThan10Hours() {
        val seconds = 3665 // 1 hour, 1 minute, 5 seconds
        val expected = "1:01"
        assertEquals(expected, seconds.secondsToHhMm())
    }

    @Test
    fun secondsToHhMm_OnlyMinutes() {
        val seconds = 605 // 10 minute, 5 seconds
        val expected = "0:10"
        assertEquals(expected, seconds.secondsToHhMm())
    }

    @Test
    fun secondsToHhMm_OnlyMinutes_lessThan10() {
        val seconds = 65 // 1 minute, 5 seconds
        val expected = "0:01"
        assertEquals(expected, seconds.secondsToHhMm())
    }

    @Test
    fun secondsToHhMm_OnlySeconds() {
        val seconds = 5
        val expected = "0:00"
        assertEquals(expected, seconds.secondsToHhMm())
    }

    @Test
    fun secondsToHhMm_Zero() {
        val seconds = 0
        val expected = "0:00"
        assertEquals(expected, seconds.secondsToHhMm())
    }

    /*
     * TO CLOCK PATTERN TESTS
     */

    @Test
    fun toClockPattern_SingleDigit() {
        val value = 5
        val expected = "05"
        assertEquals(expected, value.toClockPattern())
    }

    @Test
    fun toClockPattern_DoubleDigit() {
        val value = 12
        val expected = "12"
        assertEquals(expected, value.toClockPattern())
    }

    /*
     * DISTANCE TO SECONDS TESTS
     */

    @Test
    fun distanceToSeconds_Meters() {
        // 5000 m in 5 min/km
        val distance = 5000
        val distanceUnit = "m"
        val expected = 1500
        assertEquals(expected, distance.distanceToSeconds(distanceUnit))
    }

    @Test
    fun distanceToSeconds_Km() {
        // 5 km in 5 min/km
        val distance = 5
        val distanceUnit = "km"
        val expected = 1500
        assertEquals(expected, distance.distanceToSeconds(distanceUnit))
    }

    @Test
    fun distanceToSeconds_Mi() {
        // 3 mi in 5 min/km (8,046 min/mi)
        val distance = 3
        val distanceUnit = "mi"
        val expected = 1448 // Approximately
        assertEquals(expected, distance.distanceToSeconds(distanceUnit))
    }

    /*
     * DISTANCE TO METERS TESTS
     */

    @Test
    fun distanceToMeters_Meters() {
        val distance = 5000
        val distanceUnit = "m"
        val expected = 5000
        assertEquals(expected, distance.distanceToMeters(distanceUnit))
    }

    @Test
    fun distanceToMeters_Km() {
        val distance = 5
        val distanceUnit = "km"
        val expected = 5000
        assertEquals(expected, distance.distanceToMeters(distanceUnit))
    }

    @Test
    fun distanceToMeters_Mi() {
        val distance = 3
        val distanceUnit = "mi"
        val expected = 4828 // Approximately
        assertEquals(expected, distance.distanceToMeters(distanceUnit))
    }

    /*
     * FORMAT TIME RECOVER TESTS
     */

    @Test
    fun formatTimeRecover_LessThan60() {
        val time = 45
        val expected = "45''"
        assertEquals(expected, time.formatTimeRecover())
    }

    @Test
    fun formatTimeRecover_MultipleOf60() {
        val time = 180
        val expected = "3'"
        assertEquals(expected, time.formatTimeRecover())
    }

    @Test
    fun formatTimeRecover_Other() {
        val time = 125
        val expected = "2:05''"
        assertEquals(expected, time.formatTimeRecover())
    }

    @Test
    fun formatTimeRecover_Hours() {
        val time = 3665 // 1 hour, 1 minute, 5 seconds
        val expected = "61:05''"
        assertEquals(expected, time.formatTimeRecover())
    }

    @Test
    fun formatTimeRecover_Zero() {
        val time = 0
        val expected = "0''"
        assertEquals(expected, time.formatTimeRecover())
    }

    /*
     * PERFORMANCE TESTS
     */

    @Test
    fun performanceTest_secondsToHhMmSs() {
        val startTime = System.currentTimeMillis()
        val seconds = 3665 // 1 hour, 1 minute, 5 seconds

        for (i in 1..10000) {
            seconds.secondsToHhMmSs()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for secondsToHhMmSs: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_secondsToHhMm() {
        val startTime = System.currentTimeMillis()
        val seconds = 3665 // 1 hour, 1 minute, 5 seconds

        for (i in 1..10000) {
            seconds.secondsToHhMm()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for secondsToHhMm: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_toClockPattern() {
        val startTime = System.currentTimeMillis()
        val value = 12

        for (i in 1..10000) {
            value.toClockPattern()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for toClockPattern: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_distanceToSeconds() {
        val startTime = System.currentTimeMillis()
        val distance = 5
        val distanceUnit = "km"

        for (i in 1..10000) {
            distance.distanceToSeconds(distanceUnit)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for distanceToSeconds: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_distanceToMeters() {
        val startTime = System.currentTimeMillis()
        val distance = 5
        val distanceUnit = "km"

        for (i in 1..10000) {
            distance.distanceToMeters(distanceUnit)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for distanceToMeters: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_formatTimeRecover() {
        val startTime = System.currentTimeMillis()
        val time = 125

        for (i in 1..10000) {
            time.formatTimeRecover()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for formatTimeRecover: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }
}
