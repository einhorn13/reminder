package reminder;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(LayoutManager layout) {
        super(layout);
        setOpaque(true); // Allows painting background color if image is null
    }

    public void setBackgroundImage(Image image) {
        this.backgroundImage = image;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        // 1. Paint solid color (fallback or base)
        super.paintComponent(g);

        // 2. Paint image if exists
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            // Enable better scaling quality
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}