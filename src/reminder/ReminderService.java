package reminder;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ReminderService implements ActionListener {
    private Config config;
    private Timer timer;
    private ActivityMonitor activityMonitor;
    private ReminderListener listener;

    private int currentWorkSeconds = 0;
    private boolean isIdle = false;

    private static final int IDLE_THRESHOLD_SEC = 60; 
    private static final int RESET_THRESHOLD_SEC = 5 * 60;

    public ReminderService(Config config) {
        this.config = config;
        this.activityMonitor = new ActivityMonitor();
        this.timer = new Timer(1000, this);
    }

    public void setListener(ReminderListener listener) {
        this.listener = listener;
    }

    public void start() {
        if (!timer.isRunning()) timer.start();
    }

    public void stop() {
        if (timer.isRunning()) timer.stop();
    }

    public void updateConfig(int shortInt, int longInt, int shortDur, int longDur) {
        config.setShortBreakInterval(shortInt);
        config.setLongBreakInterval(longInt);
        config.setShortBreakDuration(shortDur);
        config.setLongBreakDuration(longDur);
        ConfigManager.saveConfig(config);
    }

    public void resetToDefaults() {
        updateConfig(
            Config.DEFAULT_SHORT_INTERVAL, Config.DEFAULT_LONG_INTERVAL,
            Config.DEFAULT_SHORT_DURATION, Config.DEFAULT_LONG_DURATION
        );
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        activityMonitor.checkActivity();
        
        long now = System.currentTimeMillis();
        long lastActive = activityMonitor.getLastActivityTime();
        long secondsSinceActive = (now - lastActive) / 1000;

        String status;

        if (secondsSinceActive > RESET_THRESHOLD_SEC) {
            currentWorkSeconds = 0;
            isIdle = true;
            status = "Welcome back! Timer reset.";
        } else if (secondsSinceActive > IDLE_THRESHOLD_SEC) {
            isIdle = true;
            status = "Idle (Paused)";
        } else {
            isIdle = false;
            // Prevent overflow, though unlikely
            if (currentWorkSeconds < Integer.MAX_VALUE) {
                currentWorkSeconds++;
            }
            status = "Working...";
            checkBreaks();
        }

        notifyListener(status);
    }

    private void checkBreaks() {
        int shortBreakIntervalSec = config.getShortBreakInterval() * 60;
        int longBreakIntervalSec = config.getLongBreakInterval() * 60;

        // Check Long Break
        if (currentWorkSeconds >= longBreakIntervalSec) {
            int durationSec = config.getLongBreakDuration() * 60;
            triggerBreak("Time for a LONG break!", durationSec);
            currentWorkSeconds = 0; 
        } 
        // Check Short Break
        else if (currentWorkSeconds > 0 && currentWorkSeconds % shortBreakIntervalSec == 0) {
            triggerBreak("Time for a short break.", config.getShortBreakDuration());
        }
    }

    private void triggerBreak(String msg, int duration) {
        if (listener != null) {
            listener.onBreakTriggered(msg, duration);
        }
    }

    private void notifyListener(String status) {
        if (listener != null) {
            int shortBreakIntervalSec = config.getShortBreakInterval() * 60;
            int progressTarget = shortBreakIntervalSec; 
            
            // Calculate display value
            int displayValue = currentWorkSeconds % shortBreakIntervalSec;
            
            // Edge case: when a long break is approaching, we might want to show that,
            // but for simplicity, we stick to the short break cycle for the progress bar.
            
            listener.onStateUpdate(displayValue, progressTarget, status);
        }
    }
}