import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;           
import javax.swing.JPanel;
import javax.swing.Timer;

public class GameView extends JPanel implements ActionListener{
    private boolean play= false;
    private int totalBrick=21;
    private final Timer timer;
    private int delay=8;
    private int ballposX=120;
    private int ballposY=350;
    private double ballX = -1.5;
    private double ballY = -2.5;
    private int playerX=350;
    private boolean dragging = false; // trạng thái kéo thả paddle
    private MapGenerator map;

    Action leftAction;
    Action rightAction;


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

        //keybinding

        leftAction = new LeftAction();
        rightAction = new RightAction();

        getInputMap().put(javax.swing.KeyStroke.getKeyStroke("LEFT"), "leftAction");
        getActionMap().put("leftAction", leftAction);

        getInputMap().put(javax.swing.KeyStroke.getKeyStroke("RIGHT"), "rightAction");
        getActionMap().put("rightAction", rightAction);

        timer= new Timer(1000/ FPS, this);
        //gọi hàm time
        timer.start();

        //khởi tạo bản đồ
        map=new MapGenerator(3,7);

        //mỗi tick: cập nhật logic game 
     
        /**
     * MouseMotionListener là interface bắt sự kiện di chuyển chuột
     
     */
    addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mousePressed(java.awt.event.MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        // Nếu click nằm trên paddle → cho phép kéo
        if (mouseY >= 650 && mouseY <= 650 + 8 &&
            mouseX >= playerX && mouseX <= playerX + 100) {
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent e) {
        dragging = false; // dừng kéo
    }
});

addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {
        if (dragging) {
            int mouseX = e.getX();
            // Cập nhật paddle theo vị trí chuột (căn giữa)
            playerX = mouseX - 50; // 100/2
            // Giới hạn trong khung
            if (playerX < 3) {
                playerX = 3;
            } else if (playerX > WIDTH - 3 - 100) {
                playerX = WIDTH - 3 - 100;
            }
            play=true;
            repaint();
        }
    }
});

        
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
       
        //the bricks
        map.draw((Graphics2D) g);
     
    }
    // di chuyển sang trái
    public class LeftAction extends AbstractAction{

        @Override
        public void actionPerformed(ActionEvent e) {
            // Move paddle left
            if (playerX > 3) {
                playerX -= 20;
                play=true;
                repaint();
            }
            
        }
    }
    // di chuyển sang phải
    public class RightAction extends AbstractAction{

        
        @Override
        public void actionPerformed(ActionEvent e) {
            // Move paddle right
            if (playerX < WIDTH - 3 - 100) {
                playerX += 20;
                play=true;
                repaint();
            }
            
        }
    }



    @Override
    //move the ball
    public void actionPerformed(java.awt.event.ActionEvent e){
        
        if(play){
            ballposX+=ballX;
            ballposY+=ballY;
            if(ballposX<3){
                ballX=-ballX;
            }
            if(ballposY<3){
                ballY=-ballY;
            }
            if(ballposX>710){
                ballX=-ballX;
            }
            Rectangle ballRect=new Rectangle(ballposX,ballposY,20,20);
            Rectangle paddleRect=new Rectangle(playerX,650,100,8);
            if(ballRect.intersects(paddleRect)){
                ballY=-ballY;
            }
        }
    repaint();
    }

    
}
