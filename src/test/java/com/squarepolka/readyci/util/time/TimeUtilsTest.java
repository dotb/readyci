package com.squarepolka.readyci.util.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeUtilsTest {

    @BeforeEach
    void setUp() {

    }

    @Test
    void getFormattedTaskTime(long timeDifference) {
        TimeUtils subject = new TimeUtils();
        String result = subject.getFormattedTaskTime(2000);
        assertEquals("0 days 0 hours 0 minutes 2 seconds 0 milliseconds", result, "Formatted time is correct");
    }

    @Test
    void convertMillisecondsToReadableTime(long totalTime, long timeRemaining, long timeUnit) {
        TimeUtils subject = new TimeUtils();
        TimeUnit result = subject.convertMillisecondsToReadableTime(2000, 1000, TimeUtils.oneSecond);

        assertEquals(result.calculatedTime, 1000, "Calculated time for seconds is correct");
    }
}
