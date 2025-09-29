import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class MyFrame extends JFrame{
    MyFrame(){

        this.setTitle("Arkanoid");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit out of application
        this.setResizable(false);
        this.setSize(500,500);
        this.setVisible(true); //make this visible

        ImageIcon image = new ImageIcon("prj.png"); //image icon
        this.setIconImage(image.getImage());
        this.getContentPane().setBackground(Color.white); //change color of background
    }
}