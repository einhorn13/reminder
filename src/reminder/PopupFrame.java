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
    JLabel lblInfo;
    JButton btnSkip;
    Timer timer;
    private int timerCount;
    private final int WINDOW_WIDTH = 350, WINDOWS_HEIGHT = 200;

    public PopupFrame(String msgText, int popupTime) {


        timerCount = popupTime;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        timer = new Timer(1000, this);
        timer.start();

        this.setResizable(false);
        this.setUndecorated(true);

        // Create PopUp window at center of first screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-WINDOW_WIDTH)/2, (screenSize.height-WINDOWS_HEIGHT)/2, WINDOW_WIDTH, WINDOWS_HEIGHT);
        Shape shape = new RoundRectangle2D.Double(0,0,350,200,20,20);
        this.setShape(shape);

        this.getContentPane().setLayout(null);

        lblInfo = new JLabel(msgText);
        lblInfo.setBounds(40, 20, 240, 40);
        lblInfo.setFont(new Font(lblInfo.getName(), Font.PLAIN, 18));
        this.getContentPane().add(lblInfo);

        btnSkip = new JButton();
        btnSkip.setBounds(130,120, 100, 40);
        btnSkip.setText("Skip (" + timerCount+")");
        btnSkip.addActionListener(event -> this.dispose());
        this.getContentPane().add(btnSkip);

        this.addMouseMotionListener(this);

        setAlwaysOnTop(true);
        setVisible(true);
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
        btnSkip.setText("Skip (" + timerCount+")");
        if (timerCount<=0) {
            this.dispose();
            timer.stop();
        }
    }
}