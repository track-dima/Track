package it.polimi.dima.track.common.ext

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StringExtTest {

    /*
     * IS VALID EMAIL TESTS
     */

    @Test
    fun isValidEmail_Valid() {
        val email = "test@example.com"
        assertTrue(email.isValidEmail())
    }

    @Test
    fun isValidEmail_Invalid() {
        val email = "invalid-email"
        assertFalse(email.isValidEmail())
    }

    @Test
    fun isValidEmail_Blank() {
        val email = ""
        assertFalse(email.isValidEmail())
    }

    /*
     * IS VALID PASSWORD TESTS
     */

    @Test
    fun isValidPassword_Valid() {
        val password = "P@ssw0rd"
        assertTrue(password.isValidPassword())
    }

    @Test
    fun isValidPassword_Invalid() {
        val password = "weak"
        assertFalse(password.isValidPassword())
    }

    @Test
    fun isValidPassword_Blank() {
        val password = ""
        assertFalse(password.isValidPassword())
    }

    /*
     * PASSWORD MATCHES TESTS
     */

    @Test
    fun passwordMatches_Match() {
        val password = "P@ssw0rd"
        val repeated = "P@ssw0rd"
        assertTrue(password.passwordMatches(repeated))
    }

    @Test
    fun passwordMatches_NoMatch() {
        val password = "P@ssw0rd"
        val repeated = "DifferentPassword"
        assertFalse(password.passwordMatches(repeated))
    }

    /*
     * REMOVE LEADING ZEROS TESTS
     */

    @Test
    fun removeLeadingZeros_HHMMSS_LessThan10HH() {
        val time = "01:02:03"
        val expected = "1:02:03"
        assertEquals(expected, time.removeLeadingZeros())
    }

    @Test
    fun removeLeadingZeros_HHMMSS_noHH() {
        val time = "00:12:03"
        val expected = "12:03"
        assertEquals(expected, time.removeLeadingZeros())
    }

    @Test
    fun removeLeadingZeros_HHMMSS_noHH_LessThan10MM() {
        val time = "00:02:03"
        val expected = "2:03"
        assertEquals(expected, time.removeLeadingZeros())
    }

    @Test
    fun removeLeadingZeros_HHMMSS_Zero() {
        val time = "00:00:00"
        val expected = "0:00"
        assertEquals(expected, time.removeLeadingZeros())
    }

    @Test
    fun removeLeadingZeros_MMSSCC_LessThan10MM() {
        val time = "02:12.34"
        val expected = "2:12.34"
        assertEquals(expected, time.removeLeadingZeros())
    }

    @Test
    fun removeLeadingZeros_MMSSCC_noMM() {
        val time = "00:12.34"
        val expected = "12.34"
        assertEquals(expected, time.removeLeadingZeros())
    }

    @Test
    fun removeLeadingZeros_MMSSCC_noMM_LessThan10SS() {
        val time = "00:02.34"
        val expected = "2.34"
        assertEquals(expected, time.removeLeadingZeros())
    }

    @Test
    fun removeLeadingZeros_MMSSCC_noMM_noSS() {
        val time = "00:00.34"
        val expected = "0.34"
        assertEquals(expected, time.removeLeadingZeros())
    }

    @Test
    fun removeLeadingZeros_MMSSCC_Zero() {
        val time = "00:00.00"
        val expected = "0.00"
        assertEquals(expected, time.removeLeadingZeros())
    }

    /*
     * TIME IS PACE TESTS
     */

    @Test
    fun timeIsPace_PaceUnit_KM() {
        val time = "4:30 min/km"
        assertTrue(time.timeIsPace())
    }

    @Test
    fun timeIsPace_PaceUnit_MI() {
        val time = "8:00 min/mi"
        assertTrue(time.timeIsPace())
    }

    @Test
    fun timeIsPace_NoPaceUnit() {
        val time = "1:30:00"
        assertFalse(time.timeIsPace())
    }

    /*
     * PACE TO METERS TESTS
     */

    @Test
    fun paceToMeters_PaceUnit_KM() {
        val pace = "5:00 min/km"
        val time = 180
        val expected = "600 m"
        assertEquals(expected, pace.paceToMeters(time))
    }

    @Test
    fun paceToMeters_PaceUnit_MI() {
        val pace = "8:00 min/mi"
        val time = 240
        val expected = "804 m"
        assertEquals(expected, pace.paceToMeters(time))
    }

    /*
     * TIME TO PACE TESTS
     */

    @Test
    fun timeToPace_DistanceUnit_M() {
        val time = "25:00"
        val distance = 5000
        val distanceUnit = "m"
        val expected = "5:00 min/km"
        assertEquals(expected, time.timeToPace(distance, distanceUnit))
    }

    @Test
    fun timeToPace_DistanceUnit_KM() {
        val time = "30:00"
        val distance = 10
        val distanceUnit = "km"
        val expected = "3:00 min/km"
        assertEquals(expected, time.timeToPace(distance, distanceUnit))
    }

    @Test
    fun timeToPace_DistanceUnit_MI() {
        val time = "25:00"
        val distance = 3
        val distanceUnit = "mi"
        val expected = "8:20 min/mi"
        assertEquals(expected, time.timeToPace(distance, distanceUnit))
    }

    @Test
    fun timeToPace_DistanceUnit_Invalid() {
        val time = "25:00"
        val distance = 3
        val distanceUnit = "invalid"
        val expected = ""
        assertEquals(expected, time.timeToPace(distance, distanceUnit))
    }

    /*
     * TIME IS ZERO TESTS
     */

    @Test
    fun timeIsZero_HHMMSS_Zero() {
        val time = "0:00:00"
        assertTrue(time.timeIsZero())
    }

    @Test
    fun timeIsZero_MMSS_Zero() {
        val time = "00:00"
        assertTrue(time.timeIsZero())
    }

    @Test
    fun timeIsZero_HHMMSS_NonZero() {
        val time = "0:00:01"
        assertFalse(time.timeIsZero())
    }

    @Test
    fun timeIsZero_MMSS_NonZero() {
        val time = "01:00"
        assertFalse(time.timeIsZero())
    }

    @Test
    fun timeIsZero_MMSSCC_Zero() {
        val time = "0:00.00"
        assertTrue(time.timeIsZero())
    }

    @Test
    fun timeIsZero_MMSSCC_NonZero() {
        val time = "0:00.01"
        assertFalse(time.timeIsZero())
    }

    @Test
    fun timeIsZero_pace_KM_Zero() {
        val time = "0:00 min/km"
        assertTrue(time.timeIsZero())
    }

    @Test
    fun timeIsZero_pace_KM_NonZero() {
        val time = "0:01 min/km"
        assertFalse(time.timeIsZero())
    }

    @Test
    fun timeIsZero_pace_MI_Zero() {
        val time = "0:00 min/mi"
        assertTrue(time.timeIsZero())
    }

    @Test
    fun timeIsZero_pace_MI_NonZero() {
        val time = "0:01 min/mi"
        assertFalse(time.timeIsZero())
    }

    /*
     * TIME TO SECONDS TESTS
     */

    @Test
    fun timeToSeconds_HHMMSS() {
        val time = "02:30:15"
        val expected = 9015
        assertEquals(expected, time.timeToSeconds())
    }

    @Test
    fun timeToSeconds_MMSS() {
        val time = "10:15"
        val expected = 615
        assertEquals(expected, time.timeToSeconds())
    }

    @Test
    fun timeToSeconds_MMSSCC() {
        val time = "10:15.25"
        val expected = 615
        assertEquals(expected, time.timeToSeconds())
    }

    @Test
    fun timeToSeconds_Empty() {
        val time = ""
        val expected = 0
        assertEquals(expected, time.timeToSeconds())
    }

    /*
     * EXTRACT CENTS TESTS
     */

    @Test
    fun extractCents_CentsPresent() {
        val time = "10:15.25"
        val expected = 25
        assertEquals(expected, time.extractCents())
    }

    @Test
    fun extractCents_NoCents() {
        val time = "10:15"
        val expected = 0
        assertEquals(expected, time.extractCents())
    }

    /*
     * PACE TO SECONDS TESTS
     */

    @Test
    fun paceToSeconds_MinutesAndSeconds() {
        val pace = "5:30 min/km"
        val expected = 330
        assertEquals(expected, pace.paceToSeconds())
    }

    @Test
    fun paceToSeconds_MinutesOnly() {
        val pace = "8:00 min/mi"
        val expected = 480
        assertEquals(expected, pace.paceToSeconds())
    }

    /*
     * EXTRACT PACE UNIT TESTS
     */

    @Test
    fun extractPaceUnit_MinPerKm() {
        val pace = "5:30 min/km"
        val expected = "min/km"
        assertEquals(expected, pace.extractPaceUnit())
    }

    @Test
    fun extractPaceUnit_MinPerMi() {
        val pace = "8:00 min/mi"
        val expected = "min/mi"
        assertEquals(expected, pace.extractPaceUnit())
    }

    /*
     * TIME WORSE THAN TESTS
     */

    @Test
    fun timeWorseThan_Worse() {
        val time1 = "2:00"
        val time2 = "1:30"
        assertTrue(time1.timeWorseThan(time2))
    }

    @Test
    fun timeWorseThan_Better() {
        val time1 = "1:30"
        val time2 = "2:00"
        assertFalse(time1.timeWorseThan(time2))
    }

    @Test
    fun timeWorseThan_Equal() {
        val time1 = "2:00"
        val time2 = "2:00"
        assertFalse(time1.timeWorseThan(time2))
    }

    /*
     * TIME BETTER THAN TESTS
     */

    @Test
    fun timeBetterThan_Worse() {
        val time1 = "2:00"
        val time2 = "1:30"
        assertFalse(time1.timeBetterThan(time2))
    }

    @Test
    fun timeBetterThan_Better() {
        val time1 = "1:30"
        val time2 = "2:00"
        assertTrue(time1.timeBetterThan(time2))
    }

    @Test
    fun timeBetterThan_Equal() {
        val time1 = "2:00"
        val time2 = "2:00"
        assertFalse(time1.timeBetterThan(time2))
    }

    /*
     * PACE WORSE THAN TESTS
     */

    @Test
    fun paceWorseThan_Worse() {
        val pace1 = "5:30 min/km"
        val pace2 = "5:00 min/km"
        assertTrue(pace1.paceWorseThan(pace2))
    }

    @Test
    fun paceWorseThan_Better() {
        val pace1 = "5:00 min/km"
        val pace2 = "5:30 min/km"
        assertFalse(pace1.paceWorseThan(pace2))
    }

    @Test
    fun paceWorseThan_Equal() {
        val pace1 = "5:30 min/km"
        val pace2 = "5:30 min/km"
        assertFalse(pace1.paceWorseThan(pace2))
    }

    @Test
    fun paceWorseThan_DifferentUnits() {
        val pace1 = "5:30 min/km"
        val pace2 = "8:00 min/mi"
        assertTrue(pace1.paceWorseThan(pace2))
    }

    /*
     * PACE BETTER THAN TESTS
     */

    @Test
    fun paceBetterThan_Worse() {
        val pace1 = "5:30 min/km"
        val pace2 = "5:00 min/km"
        assertFalse(pace1.paceBetterThan(pace2))
    }

    @Test
    fun paceBetterThan_Better() {
        val pace1 = "5:00 min/km"
        val pace2 = "5:30 min/km"
        assertTrue(pace1.paceBetterThan(pace2))
    }

    @Test
    fun paceBetterThan_Equal() {
        val pace1 = "5:30 min/km"
        val pace2 = "5:30 min/km"
        assertFalse(pace1.paceBetterThan(pace2))
    }

    @Test
    fun paceBetterThan_DifferentUnits() {
        val pace1 = "4:30 min/km"
        val pace2 = "8:00 min/mi"
        assertTrue(pace1.paceBetterThan(pace2))
    }

    /*
     * PERFORMANCE TESTS
     */

    @Test
    fun performanceTest_extractCents() {
        val startTime = System.currentTimeMillis()
        val time = "00:12.34"

        for (i in 1..10000) {
            time.extractCents()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for extractCents: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_timeIsZero() {
        val startTime = System.currentTimeMillis()
        val time = "00:12:34"

        for (i in 1..10000) {
            time.timeIsZero()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for timeIsZero: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_timeToPace() {
        val startTime = System.currentTimeMillis()
        val time = "00:10:00"
        val distance = 5000
        val distanceUnit = "m"

        for (i in 1..10000) {
            time.timeToPace(distance, distanceUnit)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for timeToPace: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_paceToMeters() {
        val startTime = System.currentTimeMillis()
        val pace = "5:00 min/km"
        val time = 600

        for (i in 1..10000) {
            pace.paceToMeters(time)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for paceToMeters: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_timeIsPace() {
        val startTime = System.currentTimeMillis()
        val time = "5:00 min/km"

        for (i in 1..10000) {
            time.timeIsPace()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for timeIsPace: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_removeLeadingZeros() {
        val startTime = System.currentTimeMillis()
        val time = "00:12:34"

        for (i in 1..10000) {
            time.removeLeadingZeros()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for removeLeadingZeros: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_passwordMatches() {
        val startTime = System.currentTimeMillis()
        val password = "Pass123"
        val repeatedPassword = "Pass123"

        for (i in 1..10000) {
            password.passwordMatches(repeatedPassword)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for passwordMatches: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_isValidPassword() {
        val startTime = System.currentTimeMillis()
        val password = "Pass123"

        for (i in 1..10000) {
            password.isValidPassword()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for isValidPassword: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_isValidEmail() {
        val startTime = System.currentTimeMillis()
        val email = "test@example.com"

        for (i in 1..10000) {
            email.isValidEmail()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for isValidEmail: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_timeToSeconds() {
        val startTime = System.currentTimeMillis()
        val time = "02:30:15"

        for (i in 1..10000) {
            time.timeToSeconds()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for timeToSeconds: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_paceToSeconds() {
        val startTime = System.currentTimeMillis()
        val pace = "5:30 min/km"

        for (i in 1..10000) {
            pace.paceToSeconds()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for paceToSeconds: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_extractPaceUnit() {
        val startTime = System.currentTimeMillis()
        val pace = "5:30 min/km"

        for (i in 1..10000) {
            pace.extractPaceUnit()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for extractPaceUnit: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_timeWorseThan() {
        val startTime = System.currentTimeMillis()
        val time1 = "2:00"
        val time2 = "1:30"

        for (i in 1..10000) {
            time1.timeWorseThan(time2)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for timeWorseThan: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_timeBetterThan() {
        val startTime = System.currentTimeMillis()
        val time1 = "2:00"
        val time2 = "1:30"

        for (i in 1..10000) {
            time1.timeBetterThan(time2)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for timeBetterThan: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_paceWorseThan() {
        val startTime = System.currentTimeMillis()
        val pace1 = "5:30 min/km"
        val pace2 = "5:00 min/km"

        for (i in 1..10000) {
            pace1.paceWorseThan(pace2)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for paceWorseThan: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_paceBetterThan() {
        val startTime = System.currentTimeMillis()
        val pace1 = "5:30 min/km"
        val pace2 = "5:00 min/km"

        for (i in 1..10000) {
            pace1.paceBetterThan(pace2)
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for paceBetterThan: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }
}
