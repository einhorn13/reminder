package reminder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.RoundRectangle2D;

public class PopupFrame extends JFrame implements MouseMotionListener, ActionListener {
    private boolean isDragged = false;
    private Point dragPoint;
    private JLabel lblInfo;
    private JButton btnSkip;
    private Timer timer;
    private int timerCount;
    private final int WINDOW_WIDTH = 350, WINDOW_HEIGHT = 200;

    public PopupFrame(String msgText, int popupTime) {
        timerCount = popupTime;
        
        // Use DISPOSE_ON_CLOSE so Alt+F4 works. 
        // We override dispose() to ensure the timer stops.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        timer = new Timer(1000, this);
        timer.start();

        setResizable(false);
        setUndecorated(true);
        
        setupLocation();

        Shape shape = new RoundRectangle2D.Double(0,0,350,200,20,20);
        setShape(shape);

        getContentPane().setLayout(null);

        lblInfo = new JLabel(msgText);
        lblInfo.setBounds(40, 20, 240, 40);
        lblInfo.setFont(new Font(lblInfo.getName(), Font.PLAIN, 18));
        getContentPane().add(lblInfo);

        btnSkip = new JButton();
        btnSkip.setBounds(130,120, 100, 40);
        updateSkipButtonText();
        btnSkip.addActionListener(event -> dispose());
        getContentPane().add(btnSkip);

        addMouseMotionListener(this);

        setAlwaysOnTop(true);
        setVisible(true);
    }

    private void updateSkipButtonText() {
        btnSkip.setText("Skip (" + timerCount + ")");
    }

    private void setupLocation() {
        GraphicsConfiguration targetConfig = null;
        try {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            if (pointerInfo != null) {
                GraphicsDevice device = pointerInfo.getDevice();
                targetConfig = device.getDefaultConfiguration();
            }
        } catch (Exception e) {
            // Fallback to default
        }

        Rectangle bounds;
        if (targetConfig != null) {
            bounds = targetConfig.getBounds();
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            bounds = new Rectangle(0, 0, screenSize.width, screenSize.height);
        }

        int x = bounds.x + (bounds.width - WINDOW_WIDTH) / 2;
        int y = bounds.y + (bounds.height - WINDOW_HEIGHT) / 2;
        
        setBounds(x, y, WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    @Override
    public void dispose() {
        // Ensure timer is stopped to prevent resource leaks
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }
        super.dispose();
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
        timerCount--;
        updateSkipButtonText();
        if (timerCount <= 0) {
            this.dispose();
        }
    }
}