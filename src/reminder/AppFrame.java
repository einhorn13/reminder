package reminder;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.BorderUIResource;

public class AppFrame extends JFrame implements MouseMotionListener, ActionListener {
    final int DEFAULT_SHORT_BREAK = 10;      // Minutes
    final int DEFAULT_LONG_BREAK = 60;       // Minutes
    final String VERSION = "0.4";
    PopupFrame popupFrame;

    private JLabel lblNewLabel;
    private JLabel lblShortBreak, lblShortBreakValue;
    private JSlider shortBreakSlider, longBreakSlider;
    private JLabel lblLongBreak, lblLongBreakValue;
    private JLabel lblVersion;
    private JProgressBar progressBar;
    private boolean isDragged = false;
    private Point dragPoint;
    private Timer timerSec;
    private long timerCount;
    private ActivityMonitor activityMonitor;

    public AppFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setResizable(false);
        this.setUndecorated(true);
        setBounds(800, 400, 450, 300);
        Shape shape = new RoundRectangle2D.Double(0,0,450,300,40,40);
        this.setShape(shape);


        getContentPane().setLayout(null);
        this.addMouseMotionListener(this);

        lblNewLabel = new JLabel("Reminder");
        lblNewLabel.setBounds(40, 20, 360, 20);
        lblNewLabel.setBorder(new BorderUIResource.BevelBorderUIResource(BevelBorder.RAISED));
        getContentPane().add(lblNewLabel);

        lblVersion = new JLabel("Ver. "+VERSION);
        lblVersion.setForeground(Color.LIGHT_GRAY);
        lblVersion.setBounds(350, 20, 60, 20);
        getContentPane().add(lblVersion);

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(480);
        progressBar.setValue(0);
        progressBar.setBounds(40,5,360,15);
        progressBar.setString(""+progressBar.getValue());
        progressBar.setStringPainted(true);
        getContentPane().add(progressBar);
        activityMonitor = new ActivityMonitor(progressBar);
        progressBar.addMouseListener(activityMonitor);

        JButton btnOk = new JButton("Exit");
        btnOk.addActionListener(event -> System.exit(0));

        btnOk.setBounds(250, 240, 160, 40);
        getContentPane().add(btnOk);

        JButton btnHide = new JButton("Hide");
        btnHide.addActionListener(event -> setState(JFrame.ICONIFIED));

        btnHide.setBounds(40, 240, 160, 40);
        getContentPane().add(btnHide);

        shortBreakSlider = new JSlider(JSlider.HORIZONTAL, 2, 60, DEFAULT_SHORT_BREAK);
        shortBreakSlider.addChangeListener(event -> {
            lblShortBreakValue.setText(""+((JSlider)event.getSource()).getValue());
        } );

        shortBreakSlider.setBounds(50,80,300,60);
        shortBreakSlider.setMajorTickSpacing(29);
        shortBreakSlider.setMinorTickSpacing(1);
        shortBreakSlider.setPaintTicks(true);
        shortBreakSlider.setPaintLabels(true);
        getContentPane().add(shortBreakSlider);

        lblShortBreak = new JLabel("Short break reminder time:");
        lblShortBreak.setBounds(40, 60, 315, 14);
        getContentPane().add(lblShortBreak);

        lblShortBreakValue = new JLabel("" + DEFAULT_SHORT_BREAK);
        lblShortBreakValue.setBounds(380, 90, 60, 14);
        getContentPane().add(lblShortBreakValue);

        longBreakSlider = new JSlider(JSlider.HORIZONTAL,30, 180, 55);
        longBreakSlider.addChangeListener(event -> {
            lblLongBreakValue.setText(""+((JSlider)event.getSource()).getValue());
        } );
        longBreakSlider.setBounds(50,170,300,DEFAULT_LONG_BREAK);
        longBreakSlider.setMajorTickSpacing(30);
        longBreakSlider.setMinorTickSpacing(5);
        longBreakSlider.setPaintTicks(true);
        longBreakSlider.setPaintLabels(true);
        getContentPane().add(longBreakSlider);

        lblLongBreak = new JLabel("Long break reminder time:");
        lblLongBreak.setBounds(40, 150, 315, 14);
        getContentPane().add(lblLongBreak);

        lblLongBreakValue = new JLabel("" + DEFAULT_LONG_BREAK);
        lblLongBreakValue.setBounds(380, 180, 60, 14);
        getContentPane().add(lblLongBreakValue);

        timerSec = new Timer(1000, this);
        timerSec.start();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (!isDragged) {
            dragPoint = e.getPoint();
        } else {
            this.setBounds(e.getXOnScreen()-dragPoint.x, e.getYOnScreen()-dragPoint.y, this.getWidth(), this.getHeight());
        }
        isDragged = true;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        isDragged = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        timerCount++;
        if (timerCount%(longBreakSlider.getValue()*60)==0) {
            popupFrame = new PopupFrame("Take a long break.", 5*60);
        } else if (timerCount%(shortBreakSlider.getValue()*60)==0) {
            popupFrame = new PopupFrame("Take a short break.", 10);
        }
    }
}