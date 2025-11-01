package arkanoid.view;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel; 

public class fast extends JPanel {
    private ImageIcon gifIcon; // Biến instance
    private static final long serialVersionUID = 1L;

    
    public fast() {
        JLabel lblNewLabel = new JLabel("");

        
        this.gifIcon = new ImageIcon(getClass().getResource("/fast.gif"));
        lblNewLabel.setIcon(gifIcon);

       
        
        lblNewLabel.setPreferredSize(new Dimension(100, 60));

        add(lblNewLabel);
    }

    
    public ImageIcon getGifIcon() {
        return gifIcon;
    }
}