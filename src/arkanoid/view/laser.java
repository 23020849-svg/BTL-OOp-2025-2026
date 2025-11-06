package arkanoid.view;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class laser extends JPanel {

    private ImageIcon gifIcon;
    private static final long serialVersionUID = 1L;

    public laser() {
        JLabel lblNewLabel = new JLabel("");
        this.gifIcon = new ImageIcon(getClass().getResource("/laser.gif"));
        lblNewLabel.setIcon(gifIcon);

        lblNewLabel.setPreferredSize(new Dimension(100,60));
        add(lblNewLabel);
    }

    public ImageIcon getGifIcon() {
        return gifIcon;
    }
    

    
}
