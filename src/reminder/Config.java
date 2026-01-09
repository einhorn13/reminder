package reminder;

public class Config {
    // Medical & Productivity Recommendations:
    // "20-20-20 Rule": Every 20 minutes, take 20 seconds to look away.
    // Hourly break: Every 60 minutes, take 5 minutes to stretch.
    
    public static final int DEFAULT_SHORT_INTERVAL = 20; // minutes
    public static final int DEFAULT_LONG_INTERVAL = 60;  // minutes
    public static final int DEFAULT_SHORT_DURATION = 20; // seconds
    public static final int DEFAULT_LONG_DURATION = 5;   // minutes

    // Intervals (frequency)
    private int shortBreakInterval;
    private int longBreakInterval;
    
    // Durations (how long the break lasts)
    private int shortBreakDuration; // seconds
    private int longBreakDuration;  // minutes

    public Config(int shortBreakInterval, int longBreakInterval, int shortBreakDuration, int longBreakDuration) {
        this.shortBreakInterval = shortBreakInterval;
        this.longBreakInterval = longBreakInterval;
        this.shortBreakDuration = shortBreakDuration;
        this.longBreakDuration = longBreakDuration;
    }

    // Getters and Setters
    public int getShortBreakInterval() { return shortBreakInterval; }
    public void setShortBreakInterval(int val) { this.shortBreakInterval = val; }

    public int getLongBreakInterval() { return longBreakInterval; }
    public void setLongBreakInterval(int val) { this.longBreakInterval = val; }

    public int getShortBreakDuration() { return shortBreakDuration; }
    public void setShortBreakDuration(int val) { this.shortBreakDuration = val; }

    public int getLongBreakDuration() { return longBreakDuration; }
    public void setLongBreakDuration(int val) { this.longBreakDuration = val; }
}