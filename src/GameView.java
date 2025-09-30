import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameView extends JPanel implements ActionListener{
    private boolean play= false;
    private int totalBrick=21;
    private final Timer timer;
    private int delay=8;
    private int ballposX=120;
    private int ballposY=350;
    private int ballX = -1;
    private int ballY = -2;
    private int playerX=350;
    
    // kích thước & fps;
    public static final int WIDTH = 750;
    public static final int HEIGHT = 750;
    private static final int FPS = 60;
        // biến demo để thấy khung hình đổi theo thời gian
    private long startNanoTime = System.nanoTime();;
    private int frames = 0;
    private double approxFPS = 0;

    public GameView(){
        //vẽ mượt hơn 
        setDoubleBuffered(true);
        setFocusable(true); //nhận phím

        timer= new Timer(1000/ FPS, this);
        //gọi hàm time
        timer.start();

        //mỗi tick: cập nhật logic game 
     
        
        
    }
       private void updateGame(){
            //sau này cập nhật sau
            }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        //black canvas
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
         //borders
         g.setColor(Color.GREEN);
            g.fillRect(0, 0, 3, 750);
            g.fillRect(0, 0, 750, 3);
            g.fillRect(getWidth()-3, 0, 3, 750);
            g.fillRect(0,getHeight()-3, getWidth(), 3);
        //the paddle
        g.setColor(Color.PINK);
        g.fillRect(playerX, 650, 100, 8);        

        //the ball
        g.setColor(Color.RED);
        g.fillOval(ballposX, ballposY, 20, 20);
       
     
    }
     @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();   // cập nhật trạng thái
        repaint();      // yêu cầu vẽ lại
    }

}
