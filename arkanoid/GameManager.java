import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameManager extends JPanel implements ActionListener, KeyListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private Paddle paddle;
    private Ball ball;
    private List<Brick> bricks;
    private List<PowerUp> powerUps;
    private int score;
    private int lives;
    private String gameState;
    private Timer timer;
    private Renderer renderer; // biến thành instance variable

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public GameManager() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        renderer = new Renderer();

        // Tạo bóng và paddle
        ball = new Ball(WIDTH / 2, HEIGHT / 2, 15, 10, 2);
        paddle = new Paddle(WIDTH / 2 - 60, HEIGHT - 50, 120, 10);

        // Tạo gạch
        bricks = new ArrayList<>();

        int rows = 5; // số hàng gạch
        int cols = 10; // số cột gạch
        int brickWidth = 70;
        int brickHeight = 20;
        int padding = 5;
        int offsetX = 25;
        int offsetY = 50;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = offsetX + col * (brickWidth + padding);
                int y = offsetY + row * (brickHeight + padding);
                bricks.add(new Brick(x, y, brickWidth, brickHeight, 1, "Normal"));
            }
        }

        // Khởi tạo trạng thái game
        gameState = "Playing";

        // Tạo timer để update game
        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    public void startGame() {
    }

    public void updateGame() {
        if (!gameState.equals("Playing")) {
            return;
        }

        // Cập nhật vị trí paddle theo phím
        if (leftPressed)
            paddle.moveLeft();
        if (rightPressed)
            paddle.moveRight();

        paddle.update();
        ball.update();

        checkCollision();
    }

    public void handleInput(String input) {
        // Không dùng ở đây nữa, xử lý bằng KeyListener
    }

    public void checkCollision() {

    }

    public void gameOver() {
        //gameState = "GAME_OVER";
        //timer.stop();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.draw(g, paddle, ball, bricks, powerUps, score, lives);
    }

    // ------------------ Key Listener ------------------
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT)
            leftPressed = true;
        if (key == KeyEvent.VK_RIGHT)
            rightPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT)
            leftPressed = false;
        if (key == KeyEvent.VK_RIGHT)
            rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // ------------------ Main Method ------------------
    public static void main(String[] args) {
        JFrame frame = new JFrame("Arkanoid Game");
        GameManager game = new GameManager();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
