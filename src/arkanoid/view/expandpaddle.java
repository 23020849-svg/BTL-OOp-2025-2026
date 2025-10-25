package arkanoid.view;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel; 

public class expandpaddle extends JPanel {
    private ImageIcon gif; 
    private static final long serialVersionUID = 1L;

    
    public  expandpaddle() {
        JLabel lblNewLabel = new JLabel("");

        
        this.gif = new ImageIcon("C:\\Users\\Admin\\Desktop\\BTL\\rsc\\extra.gif");

      
        lblNewLabel.setPreferredSize(new Dimension(100, 60));

        add(lblNewLabel);
    }

    
    public ImageIcon getGifIcon() {
        return gif;
    }
}