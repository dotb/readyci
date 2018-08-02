package com.squarepolka.readyci.util.time;


public class TimeUtils {
    public static final long oneMilliSecond = 1;
    public static final long oneSecond = 1000;
    public static final long oneMinute = oneSecond * 60;
    public static final long oneHour = oneMinute * 60;
    public static final long oneDay = oneHour * 24;

    public String getFormattedTaskTime(long timeDifference) {

        TimeUnit days = convertMillisecondsToReadableTime(timeDifference, timeDifference, oneDay);
        TimeUnit hours = convertMillisecondsToReadableTime(timeDifference, days.remainingTime, oneHour);
        TimeUnit minutes = convertMillisecondsToReadableTime(timeDifference, hours.remainingTime, oneMinute);
        TimeUnit seconds = convertMillisecondsToReadableTime(timeDifference, minutes.remainingTime, oneSecond);
        TimeUnit milliseconds = convertMillisecondsToReadableTime(timeDifference, seconds.remainingTime, oneMilliSecond);

        return String.format("%s days %s hours %s minutes %s seconds %s milliseconds", days, hours, minutes, seconds, milliseconds);
    }

    public TimeUnit convertMillisecondsToReadableTime(long totalTime, long timeRemaining, long timeUnit) {
        long calculatedTime = timeRemaining / timeUnit;
        long newRemainingTime = totalTime - (calculatedTime * timeUnit);
        TimeUnit readableTime = new TimeUnit(totalTime, newRemainingTime, calculatedTime);
        return readableTime;
    }

}
