import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class Main{
    public static void main(String[] args) {
      
      ImageIcon image1 = new ImageIcon("hahaa.png");
      Image image = image1.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);

      JLabel label = new JLabel(); //create a label
      label.setText("Welcome to my game"); //set text of label
      label.setIcon(new ImageIcon(image));
      label.setHorizontalTextPosition(JLabel.CENTER);//set text of iamge
      label.setVerticalTextPosition(JLabel.TOP);
      label.setForeground(Color.green);
      label.setFont(new Font("MV Boli",Font.PLAIN,20));
      MyFrame myFrame = new MyFrame();
      myFrame.add(label);
      
    }

}