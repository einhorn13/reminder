package reminder;

import java.awt.*;

public class ActivityMonitor {
    private Point lastMousePoint;
    private long lastActivityTime;

    public ActivityMonitor() {
        lastMousePoint = new Point(0, 0);
        lastActivityTime = System.currentTimeMillis();
    }

    /**
     * Checks current mouse position and updates activity timestamp if moved.
     */
    public void checkActivity() {
        Point currentMousePoint;
        try {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            currentMousePoint = (pointerInfo != null) ? pointerInfo.getLocation() : new Point(0, 0);
        } catch (HeadlessException e) {
            currentMousePoint = new Point(0, 0);
        }

        if (lastMousePoint.x != currentMousePoint.x || lastMousePoint.y != currentMousePoint.y) {
            lastActivityTime = System.currentTimeMillis();
            lastMousePoint = currentMousePoint;
        }
    }

    public long getLastActivityTime() {
        return lastActivityTime;
    }
}