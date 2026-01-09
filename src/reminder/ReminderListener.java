package reminder;

public interface ReminderListener {
    /**
     * Updates the UI with current status.
     * @param workSeconds Current accumulated work time in seconds.
     * @param targetSeconds Target time for the next break.
     * @param statusText Text description of current state (e.g. "Working", "Idle").
     */
    void onStateUpdate(int workSeconds, int targetSeconds, String statusText);
    
    void onBreakTriggered(String message, int durationSeconds);
}