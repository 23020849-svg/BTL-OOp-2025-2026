package src; // Đặt class trong package "arkanoid"

/**
 * GameManager.java
 *
 * Quản lý toàn bộ logic game Arkanoid: paddle, ball, bricks, power-ups,
 * xử lý va chạm, điểm, mạng, cập nhật trạng thái và vẽ khung hình.
 */
import java.awt.*; // Thư viện Swing để vẽ giao diện và xử lý sự kiện
import java.awt.event.ActionEvent; // Dùng cho đồ họa 2D
import java.awt.event.ActionListener;
import java.util.ArrayList; // Giao diện cho xử lý timer
import java.util.Iterator;
import java.util.Random;
import javax.swing.*;

// Lớp GameManager kế thừa JPanel (để vẽ game) và implements ActionListener (để cập nhật mỗi frame)
public class GameManager extends JPanel implements ActionListener {

    public static final int WIDTH = 1440;  // Chiều rộng khung game
    public static final int HEIGHT = 800;  // Chiều cao khung game

    // Các thành phần chính của game
    private Paddle paddle;              // Thanh đỡ (người chơi điều khiển)
    private Ball ball;                  // Quả bóng
    private java.util.List<Brick> bricks;  // Danh sách gạch
    private java.util.List<PowerUp> powerUps; // Danh sách vật phẩm rơi
    private Renderer renderer;          // Lớp phụ để vẽ
    private Timer timer;                // Bộ đếm thời gian cho game loop (~60 FPS)
    private Random rand = new Random(); // Sinh ngẫu nhiên vật phẩm

    // Trạng thái game
    private int score = 0;    // Điểm
    private int lives = 3;    // Số mạng
    private boolean running = false;      // Game đang chạy hay không
    private boolean ballLaunched = false; // Bóng đã bắn hay chưa
    private boolean paused = false; // Tạm dừng game

    // Góc bắn
    private double launchAngle = -90; // Góc bắn mặc định (âm = hướng lên)
    private final double MIN_ANGLE = -180; // Giới hạn trái
    private final double MAX_ANGLE = 0; // Giới hạn phải
    private Sound collisionSound; // Âm thanh va chạm

    /** Khởi tạo toàn bộ game */
    public GameManager() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT)); // Đặt kích thước khung
        setBackground(Color.BLACK);                     // Màu nền
        setFocusable(true);                             // Cho phép nhận phím
        collisionSound = new Sound();
        collisionSound.loadSound("rsc/391658__jeckkech__collision.wav");

        initGame();         // Khởi tạo các đối tượng game
        initKeyBindings();  // Gán phím điều khiển

        renderer = new Renderer(); // Tạo renderer để vẽ
        timer = new Timer(16, this); // Cập nhật game mỗi 16ms (~60 FPS)
        timer.start();               // Bắt đầu vòng lặp game
        
    }

    /** Reset game */
    private void initGame() {
        paddle = new Paddle(WIDTH / 2 - 40, HEIGHT - 40, 80, 12); // Tạo paddle ở giữa dưới
        ball = new Ball(WIDTH / 2 - 8, HEIGHT - 60, 8, 3, -3);    // Tạo bóng trên paddle
        bricks = new ArrayList<>();    // Danh sách gạch
        powerUps = new ArrayList<>();  // Danh sách power-up
        createLevel();                 // Sinh level
        running = true;                // Bắt đầu game
        ballLaunched = false;          // Chưa bắn bóng
        score = 0;                     // Reset điểm
        lives = 3;                     // Reset mạng
    }

    /** Tạo bố cục gạch */
    private void createLevel() {
        bricks.clear(); // Xóa gạch cũ
        int rows = 5;   // 5 hàng gạch
        int cols = 10;  // 10 cột gạch
        int brickW = (WIDTH - 50) / cols; // Tính chiều rộng mỗi gạch
        int brickH = 20;                  // Chiều cao mỗi gạch
        int startX = 25;                  // Lề trái
        int startY = 60;                  // Lề trên
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = startX + c * brickW;
                int y = startY + r * (brickH + 6);

                // Hàng trên cùng (r = 0) sẽ là gạch 3 máu
                if (r == 0) {
                    bricks.add(new StrongBrick(x, y, brickW - 4, brickH, 3)); // <-- Gạch 3 máu
                }
                // Hàng số hai (r = 1) có một vài gạch 2 máu
                else if (r == 1 && c % 3 == 0) {
                    bricks.add(new StrongBrick(x, y, brickW - 4, brickH, 2));
                } // Các hàng còn lại là gạch thường
                else {
                    bricks.add(new NormalBrick(x, y, brickW - 4, brickH));
                }
            }
        }
    }

    /** Gán phím điều khiển */
    private void initKeyBindings() {
        // Tăng góc (bắn lệch sang phải)
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("6"), "angle_right");
        getActionMap().put("angle_right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ballLaunched) {
                    launchAngle += 5;
                    if (launchAngle > MAX_ANGLE) launchAngle = MAX_ANGLE;
                }
            }
        });

        // Giảm góc (bắn lệch sang trái)
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"), "angle_left");
        getActionMap().put("angle_left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ballLaunched) {
                    launchAngle -= 5;
                    if (launchAngle < MIN_ANGLE) launchAngle = MIN_ANGLE;
                }
            }
        });

        // Giữ trái
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        getActionMap().put("left_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingLeft(true); // Paddle sang trái
                if (!ballLaunched) alignBallToPaddle(); // Nếu chưa bắn, bóng di chuyển theo paddle
            }
        });

        // Nhả trái
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        getActionMap().put("left_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingLeft(false);
            }
        });

        // Giữ phải
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed RIGHT"), "right_pressed");
        getActionMap().put("right_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingRight(true); // Paddle sang phải
                if (!ballLaunched) alignBallToPaddle(); // Bóng di chuyển cùng paddle
            }
        });

        // Nhả phải
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        getActionMap().put("right_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingRight(false);
            }
        });

        // Phím SPACE để bắn bóng
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
        getActionMap().put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ballLaunched) {
                    collisionSound.playOnce();
                    ballLaunched = true; // Bắt đầu bắn bóng
                    double speed = 6.0; // tốc độ khởi đầu
                    double rad = Math.toRadians(launchAngle);
                    ball.setDx(speed * Math.cos(rad));
                    ball.setDy(speed * Math.sin(rad)); // âm vì hướng lên
                }
            }
        });

        // Phím R để khởi động lại
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initGame(); // Reset lại toàn bộ
            }
        });

        // Phím P để PAUSE / Resume
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "pause");
        getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
            }
        });
    }

    /** Đặt bóng lên paddle (khi chưa bắn) */
    private void alignBallToPaddle() {
        // Căn giữa bóng trên paddle
        ball.setX(paddle.getX() + paddle.getWidth() / 2 - ball.getWidth() / 2);
        ball.setY(paddle.getY() - ball.getHeight() - 2);
    }

    /** Hàm loop cập nhật game */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) return; // Nếu game chưa chạy thì bỏ qua

        // Khi game đang bị pause, không cập nhật logic
        if (paused) {
            repaint();
            return;
        }

        if (!ballLaunched) alignBallToPaddle(); // Nếu chưa bắn, bóng đi theo paddle
        else ball.update();
        paddle.update();                        // Cập nhật paddle

        // Cập nhật và kiểm tra va chạm power-up
        Iterator<PowerUp> pit = powerUps.iterator();
        while (pit.hasNext()) {
            PowerUp p = pit.next();
            p.update(); // Power-up rơi xuống
            if (!p.isActive()) {
                pit.remove(); // Xóa nếu hết tác dụng
                continue;
            }
            // Va chạm paddle → kích hoạt hiệu ứng
            if (p.getBounds().intersects(paddle.getBounds())) {
                p.applyEffect(paddle, ball, this);
                pit.remove();
            }
        }

        // Va chạm giữa bóng và paddle
        if (ball.getBounds().intersects(paddle.getBounds())) {
            collisionSound.playOnce(); // Phát âm thanh va chạm
            int paddleCenter = paddle.getX() + paddle.getWidth() / 2;
            int ballCenter = ball.getX() + ball.getWidth() / 2;
            int diff = ballCenter - paddleCenter; // Lệch giữa tâm bóng và paddle
            double factor = diff / (double) (paddle.getWidth() / 2);
            double speed = Math.sqrt(ball.getDx() * ball.getDx() + ball.getDy() * ball.getDy());
            double newDx = speed * factor * 1.2;                  // Góc lệch dựa trên vị trí va chạm
            double newDy = -Math.abs(speed * (1 - Math.abs(factor))); // Luôn bật lên
            if (Math.abs(newDy) < 2) newDy = -2; // Giữ tốc độ tối thiểu
            ball.setDx(newDx);
            ball.setDy(newDy);
            ball.setY(paddle.getY() - ball.getHeight() - 1); // Đặt bóng lên trên paddle
        }

        // Kiểm tra va chạm bóng - gạch
        Iterator<Brick> it = bricks.iterator();
        while (it.hasNext()) {
            Brick brick = it.next();
            if (brick.isDestroyed()) continue;

            if (ball.getBounds().intersects(brick.getBounds())) {
                ball.bounceOff(brick); // Bật bóng trước để điều chỉnh vị trí

                brick.takeHit(); // Trừ máu gạch
                if (brick.isDestroyed()) {
                    score += 100;

                    // Xác suất rơi power-up tùy theo từng loại gạch
                    if (rand.nextDouble() < brick.getPowerUpDropChance()) {
                        spawnRandomPowerUp(brick.getX() + brick.getWidth() / 2, brick.getY() + brick.getHeight());
                    }

                    // Ngăn bóng va thêm các gạch khác trong cùng frame
                    break;
                }
            }
        }

        bricks.removeIf(Brick::isDestroyed); // Xóa gạch vỡ

        // Kiểm tra nếu bóng rơi xuống dưới màn hình
        if (ball.getY() > HEIGHT) {
            lives--; // Mất một mạng
            if (lives <= 0) { // Hết mạng → Game Over
                running = false;
                SwingUtilities.invokeLater(() -> {
                    int resp = JOptionPane.showConfirmDialog(this,
                            "Game Over! Score: " + score + "\nPlay again?",
                            "Game Over", JOptionPane.YES_NO_OPTION);
                    if (resp == JOptionPane.YES_OPTION) initGame(); // Chơi lại
                    else System.exit(0);                            // Thoát game
                });
            } else {
                // Còn mạng → reset bóng
                ballLaunched = false;
                ball.setDx(3);
                ball.setDy(-3);
                alignBallToPaddle();
            }
        }

        // Khi không còn gạch → sang level mới
        if (bricks.isEmpty()) {
            createLevel(); // Sinh level mới
            ball.setDx(ball.getDx() * 1.1); // Tăng tốc bóng
            ball.setDy(ball.getDy() * 1.1);
            ballLaunched = false;
            alignBallToPaddle();
        }

        repaint(); // Vẽ lại frame
    }

    /** Sinh power-up ngẫu nhiên */
    private void spawnRandomPowerUp(int x, int y) {
        if (rand.nextBoolean())
            powerUps.add(new ExpandPaddlePowerUp(x - 10, y)); // Power-up mở rộng paddle
        else
            powerUps.add(new FastBallPowerUp(x - 10, y));     // Power-up tăng tốc bóng
    }

    /** Vẽ frame */
      @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ tất cả qua Renderer (kể cả overlay)
        renderer.draw(g, paddle, ball, bricks, powerUps, score, lives,
                      ballLaunched, launchAngle, paused);
    }

    // Getter (nếu cần)
    public Paddle getPaddle() { return paddle; }
    public Ball getBall() { return ball; }
}