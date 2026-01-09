package reminder;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class TrayHelper {
    private TrayIcon trayIcon;
    private SystemTray systemTray;
    private JFrame mainFrame;

    public TrayHelper(JFrame frame) {
        this.mainFrame = frame;
        if (SystemTray.isSupported()) {
            systemTray = SystemTray.getSystemTray();
            createTrayIcon();
        }
    }

    private void createTrayIcon() {
        // Load icon safely
        Image image = null;
        URL iconUrl = getClass().getResource("/icon.png");
        if (iconUrl != null) {
            image = new ImageIcon(iconUrl).getImage();
        } else {
            // Fallback if resource not found (e.g. running from IDE without resources root set)
            image = new ImageIcon("assets/icon.png").getImage();
        }

        PopupMenu popup = new PopupMenu();
        MenuItem openItem = new MenuItem("Open");
        openItem.addActionListener(e -> showFrame());
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        
        popup.add(openItem);
        popup.addSeparator();
        popup.add(exitItem);

        trayIcon = new TrayIcon(image, "Reminder App", popup);
        trayIcon.setImageAutoSize(true);
        trayIcon.addActionListener(e -> showFrame());
    }

    public void minimizeToTray() {
        if (SystemTray.isSupported() && trayIcon != null) {
            try {
                systemTray.add(trayIcon);
                mainFrame.setVisible(false);
            } catch (AWTException e) {
                e.printStackTrace();
            }
        } else {
            mainFrame.setState(Frame.ICONIFIED);
        }
    }

    private void showFrame() {
        mainFrame.setVisible(true);
        mainFrame.setState(Frame.NORMAL);
        if (trayIcon != null) systemTray.remove(trayIcon);
    }
}