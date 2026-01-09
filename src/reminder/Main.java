package reminder;

import javax.swing.*;
import java.net.URL;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Ignore
        }

        SwingUtilities.invokeLater(() -> {
            Config config = ConfigManager.loadConfig();
            ReminderService service = new ReminderService(config);
            AppFrame frame = new AppFrame(service);
            
            // Try loading icon from classpath first, then filesystem
            URL iconUrl = Main.class.getResource("/icon.png");
            if (iconUrl != null) {
                frame.setIconImage(new ImageIcon(iconUrl).getImage());
            } else {
                frame.setIconImage(new ImageIcon("assets/icon.png").getImage());
            }

            frame.setVisible(true);
            service.start();
        });
    }
}