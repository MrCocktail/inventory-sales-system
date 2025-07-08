import javax.swing.*;
import java.awt.*;

public class BackgroundDesktopPane extends JDesktopPane {
    private Image backgroundImage;

    public BackgroundDesktopPane(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}
