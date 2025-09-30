import javax.swing.*;

public class Main{
    public static void main(String[] args) {
        JFrame f= new JFrame();
        f.setTitle("Arkanoid");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //exit out of application
        f.setResizable(false);
        
        f.setSize(750,750);
        ImageIcon image = new ImageIcon("rsc/prj.png"); //image icon
        f.setIconImage(image.getImage());

        GameView gameview= new GameView();
       
        f.add(gameview);    
       
        
        f.setVisible(true);

          
    }

}