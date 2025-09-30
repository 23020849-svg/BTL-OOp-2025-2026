import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.*;
import javax.swing.*;

public class GameView extends JPanel implements ActionListener {

    public static final int WIDTH = 750;
    public static final int HEIGHT = 750;
    private static final int BORDER = 3;
    private static final int FPS = 60;

    private boolean play = false;
    private boolean dragging = false;

    private Paddle paddle;
    private Ball ball;

    private final Timer timer;

    public GameView(){
        setPreferredSize(new java.awt.Dimension(WIDTH, HEIGHT));
        setDoubleBuffered(true);
        setFocusable(true);

        paddle = new Paddle(350, 650, 100, 8, 20, BORDER, WIDTH - BORDER);
        ball   = new Ball(120, 350, 20, 3);

        // KEYBINDING: tạo action inline thay vì lớp riêng
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("LEFT"), "leftAction");
        getActionMap().put("leftAction", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                paddle.moveLeft();
                play = true;
                repaint();
            }
        });

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke("RIGHT"), "rightAction");
        getActionMap().put("rightAction", new AbstractAction() {
            @Override public void actionPerformed(ActionEvent e) {
                paddle.moveRight();
                play = true;
                repaint();
            }
        });

        // Chuột: kéo paddle (tuỳ chọn, bỏ nếu chưa cần)
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                dragging = paddle.getBounds().contains(e.getPoint());
            }
            @Override public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (!dragging) return;
                paddle.setX(e.getX() - paddle.getWidth()/2); // setX đã kẹp biên
                play = true;
                repaint();
            }
        });

        timer = new Timer(1000 / FPS, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        // nền đen
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // viền xanh 3px
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, BORDER, HEIGHT);
        g.fillRect(0, 0, WIDTH, BORDER);
        g.fillRect(735, 0, BORDER, HEIGHT);
        g.fillRect(0, 714, WIDTH, BORDER);

        // vẽ paddle & bóng
        Graphics2D g2 = (Graphics2D) g;
        paddle.draw(g2);
        ball.draw(g2);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if (play) {
            ball.update();
            
            //va chạm tường
            ball.checkWallCollision(WIDTH, HEIGHT);

            // Va chạm paddle
            ball.checkCollision(paddle);

            // thua nếu chạm đáy
            if (ball.getY() + ball.getHeight() >= HEIGHT - BORDER) { //  dùng toạ độ y
                play = false;


                // Nếu Ball CHƯA có reset:
                ball.setX(120);
                ball.setY(350);
                ball.setDirectionX(-1);
                ball.setDirectionY(-1);
            }
        }
        repaint();
    }
}
