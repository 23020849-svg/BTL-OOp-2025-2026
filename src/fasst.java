import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import java.awt.Dimension; 

public class fasst extends JPanel {
    private ImageIcon gifIcon; // Biến instance
    private static final long serialVersionUID = 1L;

    
    public fasst() {
        JLabel lblNewLabel = new JLabel("");

        
        this.gifIcon = new ImageIcon("C:\\Users\\Admin\\Desktop\\BTL\\rsc\\fast.gif");

       
        
        lblNewLabel.setPreferredSize(new Dimension(100, 60));

        add(lblNewLabel);
    }

    
    public ImageIcon getGifIcon() {
        return gifIcon;
    }
}