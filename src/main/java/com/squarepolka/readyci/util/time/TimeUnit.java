package com.squarepolka.readyci.util.time;

public class TimeUnit {
    public long totalTime;
    public long remainingTime;
    public long calculatedTime;

    public TimeUnit(long totalTime, long remainingTime, long calculatedTime) {
        this.totalTime = totalTime;
        this.remainingTime = remainingTime;
        this.calculatedTime = calculatedTime;
    }

    @Override
    public String toString() {
        String timeString = Long.toString(calculatedTime);
        return timeString;
    }
}
