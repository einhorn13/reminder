package reminder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.lang.Math.min;

public class ActivityMonitor implements ActionListener, MouseListener {
    private Timer timer;
    JProgressBar progressBar;
    Point lastMousePoint, currentMousePoint;
    int timerCounter, activityCounter;

    ActivityMonitor(JProgressBar setProgressBar) {
        progressBar = setProgressBar;
        timer = new Timer(1000, this);
        timer.start();
        lastMousePoint = new Point();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (MouseInfo.getPointerInfo()!=null)
            currentMousePoint = MouseInfo.getPointerInfo().getLocation();
        else
            currentMousePoint = new Point(0,0);

        timerCounter++;

        if (lastMousePoint.x != currentMousePoint.x || lastMousePoint.y != currentMousePoint.y) {
            activityCounter++;
        }

        if (timerCounter >= 60) {
            if (activityCounter>0) {
                progressBar.setValue(min(progressBar.getValue()+1, progressBar.getMaximum()));
                progressBar.setString(""+progressBar.getValue());
            }
            timerCounter = 0;
            activityCounter = 0;
        }

        lastMousePoint=currentMousePoint;
    }

    // for ProgressBar reset - press button 2
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == 2) {
            progressBar.setValue(0);
            progressBar.setString(""+progressBar.getValue());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
