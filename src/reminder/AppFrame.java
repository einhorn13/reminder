package reminder;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class AppFrame extends JFrame implements ReminderListener {
    private final String VERSION = "0.5";
    
    // Labels
    private JLabel lblShortInt, lblLongInt, lblShortDur, lblLongDur, lblStatus;
    // Sliders
    private JSlider sldShortInt, sldLongInt, sldShortDur, sldLongDur;
    
    private JProgressBar progressBar;
    private Point dragPoint;
    private ReminderService reminderService;
    private TrayHelper trayHelper;
    
    private boolean isLoading = false;

    public AppFrame(ReminderService service) {
        this.reminderService = service;
        this.reminderService.setListener(this);
        this.trayHelper = new TrayHelper(this);

        initUI();
        loadSettingsToUI();
    }

    private void initUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setSize(450, 500); 
        setLocationRelativeTo(null);
        setShape(new RoundRectangle2D.Double(0, 0, 450, 500, 40, 40));

        // Use custom BackgroundPanel
        BackgroundPanel mainPanel = new BackgroundPanel(new GridBagLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Logic to load background image
        loadBackground(mainPanel);
        
        setContentPane(mainPanel);

        // Drag functionality
        MouseAdapter dragListener = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { dragPoint = e.getPoint(); }
            public void mouseDragged(MouseEvent e) {
                if (dragPoint != null) {
                    Point curr = getLocation();
                    setLocation(curr.x + e.getX() - dragPoint.x, curr.y + e.getY() - dragPoint.y);
                }
            }
        };
        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // --- Header ---
        JLabel lblTitle = new JLabel("Reminder Ver. " + VERSION, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        mainPanel.add(lblTitle, gbc); // No need for manual gridx/y if we add sequentially with correct GBC, but sticking to explicit

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        mainPanel.add(lblTitle, gbc);

        lblStatus = new JLabel("Initializing...", SwingConstants.CENTER);
        lblStatus.setForeground(Color.GRAY); // Might need adjustment if background is dark
        gbc.gridy = 1;
        mainPanel.add(lblStatus, gbc);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        gbc.gridy = 2;
        mainPanel.add(progressBar, gbc);

        // --- Frequency Settings ---
        addSectionHeader(mainPanel, gbc, 3, "Frequency (How often)");

        lblShortInt = new JLabel();
        sldShortInt = new JSlider(5, 60);
        addSliderRow(mainPanel, gbc, 4, "Short break every (min):", lblShortInt, sldShortInt);

        lblLongInt = new JLabel();
        sldLongInt = new JSlider(30, 180);
        addSliderRow(mainPanel, gbc, 6, "Long break every (min):", lblLongInt, sldLongInt);

        // --- Duration Settings ---
        addSectionHeader(mainPanel, gbc, 8, "Duration (How long)");

        lblShortDur = new JLabel();
        sldShortDur = new JSlider(10, 120); // 10s to 120s
        addSliderRow(mainPanel, gbc, 9, "Short break length (sec):", lblShortDur, sldShortDur);

        lblLongDur = new JLabel();
        sldLongDur = new JSlider(1, 30); // 1m to 30m
        addSliderRow(mainPanel, gbc, 11, "Long break length (min):", lblLongDur, sldLongDur);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false); // Make transparent for background
        
        JButton btnHide = new JButton("Hide");
        btnHide.addActionListener(e -> trayHelper.minimizeToTray());
        
        JButton btnReset = new JButton("Reset");
        btnReset.addActionListener(e -> {
            reminderService.resetToDefaults();
            loadSettingsToUI();
        });

        JButton btnExit = new JButton("Exit");
        btnExit.addActionListener(e -> System.exit(0));

        buttonPanel.add(btnHide);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnExit);

        gbc.gridy = 13;
        mainPanel.add(buttonPanel, gbc);
    }

    private void loadBackground(BackgroundPanel panel) {
        try {
            File bgFile = new File("assets/background.jpg");
            if (bgFile.exists()) {
                Image img = ImageIO.read(bgFile);
                panel.setBackgroundImage(img);
            } else {
                // Fallback color (System default or custom)
                panel.setBackground(UIManager.getColor("Panel.background"));
            }
        } catch (Exception e) {
            System.err.println("Failed to load background: " + e.getMessage());
        }
    }

    private void addSectionHeader(JPanel panel, GridBagConstraints gbc, int row, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(new Color(0, 102, 204));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(label, gbc);
        gbc.insets = new Insets(5, 5, 5, 5);
    }

    private void addSliderRow(JPanel panel, GridBagConstraints gbc, int row, String text, JLabel valLabel, JSlider slider) {
        gbc.gridwidth = 1; gbc.gridy = row; gbc.gridx = 0;
        
        JLabel titleLabel = new JLabel(text);
        panel.add(titleLabel, gbc);
        
        gbc.gridx = 1;
        panel.add(valLabel, gbc);

        slider.setMajorTickSpacing((slider.getMaximum() - slider.getMinimum()) / 5);
        slider.setPaintTicks(true);
        slider.setOpaque(false); // Make slider transparent
        
        slider.addChangeListener(e -> {
            valLabel.setText(String.valueOf(slider.getValue()));
            updateServiceConfig();
        });
        
        gbc.gridx = 0; gbc.gridy = row + 1; gbc.gridwidth = 2;
        panel.add(slider, gbc);
    }
    
    private void updateServiceConfig() {
        if (isLoading) return;
        
        reminderService.updateConfig(
            sldShortInt.getValue(),
            sldLongInt.getValue(),
            sldShortDur.getValue(),
            sldLongDur.getValue()
        );
    }

    private void loadSettingsToUI() {
        isLoading = true;
        try {
            Config config = reminderService.getConfig();
            
            sldShortInt.setValue(config.getShortBreakInterval());
            lblShortInt.setText(String.valueOf(config.getShortBreakInterval()));
            
            sldLongInt.setValue(config.getLongBreakInterval());
            lblLongInt.setText(String.valueOf(config.getLongBreakInterval()));
            
            sldShortDur.setValue(config.getShortBreakDuration());
            lblShortDur.setText(String.valueOf(config.getShortBreakDuration()));
            
            sldLongDur.setValue(config.getLongBreakDuration());
            lblLongDur.setText(String.valueOf(config.getLongBreakDuration()));
        } finally {
            isLoading = false;
        }
    }

    @Override
    public void onStateUpdate(int workSeconds, int targetSeconds, String statusText) {
        progressBar.setMaximum(targetSeconds);
        progressBar.setValue(workSeconds);
        progressBar.setString(workSeconds / 60 + " / " + targetSeconds / 60 + " min");
        lblStatus.setText(statusText);
    }

    @Override
    public void onBreakTriggered(String message, int durationSeconds) {
        SwingUtilities.invokeLater(() -> new PopupFrame(message, durationSeconds));
    }
}