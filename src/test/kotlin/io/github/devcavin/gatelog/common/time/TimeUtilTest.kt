package io.github.devcavin.gatelog.common.time

import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.OffsetDateTime
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimeUtilTest {
    val clock: Clock = Clock.systemDefaultZone()
    val timeUtil = TimeUtil(clock)

    @Test
    fun `checkin time yesterday is overnight`() {
        val checkinTime = OffsetDateTime.parse("2026-09-26T20:00:00Z")
        val now = OffsetDateTime.parse("2026-09-27T00:00:00Z")

        val result = timeUtil.isOvernight(checkinTime, now)

        assertTrue(result)
    }

    @Test
    fun `checkin time today is not overnight`() {
        val checkinTime = OffsetDateTime.parse("2026-09-26T20:00:00Z")
        val now = OffsetDateTime.parse("2026-09-26T23:59:59Z")

        val result = timeUtil.isOvernight(checkinTime, now)

        assertFalse(result)
    }

    @Test
    fun `checkin time previous day is overnight`() {
        val checkinTime = OffsetDateTime.parse("2026-09-24T23:59:59Z")
        val now = OffsetDateTime.parse("2026-09-25T00:00:00Z")

        val result = timeUtil.isOvernight(checkinTime, now)
        assertTrue(result)
    }
}