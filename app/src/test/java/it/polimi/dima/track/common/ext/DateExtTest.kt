package it.polimi.dima.track.common.ext

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateExtTest {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    /*
     * GET WEEK INTERVAL TESTS
     */

    @Test
    fun getWeekInterval() {
        val currentDate = dateFormat.parse("27/08/2023") // A Sunday
        val expected = "21-27 ago 2023"
        assertEquals(expected, currentDate.getWeekInterval())
    }

    @Test
    fun getWeekInterval_NullDate() {
        val expected = "default"
        assertEquals("", null.getWeekInterval())
        assertEquals(expected, null.getWeekInterval(default = expected))
    }

    @Test
    fun getWeekInterval_DifferentLocale() {
        val locale = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)
        val currentDate = dateFormat.parse("27/08/2023") // A Sunday
        val expected = "27 Aug - 2 Sep 2023"
        assertEquals(expected, currentDate.getWeekInterval())
        Locale.setDefault(locale)
    }

    @Test
    fun getWeekInterval_LeapYear() {
        val leapYearDate = dateFormat.parse("29/02/2024") // A leap year
        val expected = "26 feb - 3 mar 2024"
        assertEquals(expected, leapYearDate.getWeekInterval())
    }

    @Test
    fun getWeekInterval_EndOfMonth() {
        val endOfMonthDate = dateFormat.parse("31/08/2023") // A Tuesday
        val expected = "28 ago - 3 set 2023"
        assertEquals(expected, endOfMonthDate.getWeekInterval())
    }

    @Test
    fun getWeekInterval_EndOfYear() {
        val endOfMonthDate = dateFormat.parse("31/12/2022")
        val expected = "26 dic 2022 - 1 gen 2023"
        assertEquals(expected, endOfMonthDate.getWeekInterval())
    }

    /*
     * GET DAY TESTS
     */

    @Test
    fun getDay() {
        val currentDate = dateFormat.parse("27/08/2023")
        val expected = "27"
        assertEquals(expected, currentDate.getDay(""))
    }

    @Test
    fun getDay_NullDate() {
        val expected = "default"
        assertEquals("", null.getDay())
        assertEquals(expected, null.getDay(default = expected))
    }

    /*
     * GET DAY NAME TESTS
     */

    @Test
    fun getDayName() {
        val currentDate = dateFormat.parse("27/08/2023")
        val expected = "dom"
        assertEquals(expected, currentDate.getDayName())
    }

    @Test
    fun getDayName_NullDate() {
        val expected = "expected"
        assertEquals("", null.getDayName())
        assertEquals(expected, null.getDayName(default = expected))
    }

    @Test
    fun getDayName_DifferentLocale() {
        val locale = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)
        val currentDate = dateFormat.parse("27/08/2023")
        val expected = "Sun"
        assertEquals(expected, currentDate.getDayName())
        Locale.setDefault(locale)
    }

    /*
     * IS TODAY TESTS
     */

    @Test
    fun isToday() {
        val currentDate = Date()
        assertTrue(currentDate.isToday())
    }

    @Test
    fun isToday_NullDate() {
        assertFalse(null.isToday())
    }

    @Test
    fun isToday_notToday() {
        val currentDate = dateFormat.parse("01/01/2021")
        assertFalse(currentDate.isToday())
    }

    /*
     * IS THIS WEEK TESTS
     */

    @Test
    fun isThisWeek() {
        val currentDate = Date()
        assertTrue(currentDate.isThisWeek())
    }

    @Test
    fun isThisWeek_NullDate() {
        assertFalse(null.isThisWeek())
    }

    @Test
    fun isThisWeek_notThisWeek() {
        val currentDate = dateFormat.parse("01/01/2021")
        assertFalse(currentDate.isThisWeek())
    }

    /*
     * IS THIS MONTH TESTS
     */

    @Test
    fun isThisMonth() {
        val currentDate = Date()
        assertTrue(currentDate.isThisMonth())
    }

    @Test
    fun isThisMonth_NullDate() {
        assertFalse(null.isThisMonth())
    }

    @Test
    fun isThisMonth_notThisMonth() {
        val currentDate = dateFormat.parse("01/01/2021")
        assertFalse(currentDate.isThisMonth())
    }

    /*
     * PERFORMANCE TESTS
     */

    @Test
    fun performanceTest_getWeekInterval() {
        val startTime = System.currentTimeMillis()
        val currentDate = dateFormat.parse("27/08/2023") // A Sunday

        for (i in 1..10000) {
            currentDate.getWeekInterval()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_getDay() {
        val startTime = System.currentTimeMillis()
        val currentDate = dateFormat.parse("27/08/2023")

        for (i in 1..10000) {
            currentDate.getDay("")
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for getDay: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_getDayName() {
        val startTime = System.currentTimeMillis()
        val currentDate = dateFormat.parse("27/08/2023")

        for (i in 1..10000) {
            currentDate.getDayName()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for getDayName: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_isToday() {
        val startTime = System.currentTimeMillis()
        val currentDate = Date()

        for (i in 1..10000) {
            currentDate.isToday()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for isToday: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_isThisWeek() {
        val startTime = System.currentTimeMillis()
        val currentDate = Date()

        for (i in 1..10000) {
            currentDate.isThisWeek()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for isThisWeek: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }

    @Test
    fun performanceTest_isThisMonth() {
        val startTime = System.currentTimeMillis()
        val currentDate = Date()

        for (i in 1..10000) {
            currentDate.isThisMonth()
        }

        val endTime = System.currentTimeMillis()
        val executionTime = endTime - startTime
        println("Performance test execution time for isThisMonth: $executionTime ms")
        assertTrue("Performance test failed: execution time was $executionTime ms", executionTime < 1000)
    }
}
