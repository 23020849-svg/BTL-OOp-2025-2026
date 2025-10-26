package arkanoid.core; // Đặt class trong package arkanoid.core

/**
 * GameManager.java
 *
 * Quản lý toàn bộ logic game Arkanoid: paddle, ball, bricks, power-ups,
 * xử lý va chạm, điểm, mạng, cập nhật trạng thái và vẽ khung hình.
 */
import java.awt.Dimension; // Thư viện Swing để vẽ giao diện và xử lý sự kiện
import java.awt.Graphics; // Dùng cho đồ họa 2D
import java.awt.event.ActionEvent; // Giao diện cho xử lý timer
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import arkanoid.entities.Ball;
import arkanoid.entities.Paddle;
import arkanoid.entities.bricks.Brick;
import arkanoid.entities.powerups.ExpandPaddlePowerUp;
import arkanoid.entities.powerups.FastBallPowerUp;
import arkanoid.entities.powerups.MultiBallPowerUp;
import arkanoid.entities.powerups.PowerUp;
import arkanoid.utils.LevelLoader;
import arkanoid.utils.Sound;
import arkanoid.view.Renderer;

// Lớp GameManager kế thừa JPanel (để vẽ game) và implements ActionListener (để cập nhật mỗi frame)
public class GameManager extends JPanel {

    public static final int WIDTH = 1440; // Chiều rộng khung game
    public static final int HEIGHT = 800; // Chiều cao khung game
    private static final double BALL_SCALE = 2.5; // phóng to khi vẽ
    private static final int VISUAL_GAP = 6;      // khoảng hở giữa bóng (đã scale) và paddle


    // Các thành phần chính của game
    private Paddle paddle; // Thanh đỡ (người chơi điều khiển)
    private java.util.List<Ball> balls; // Danh sách bóng
    private java.util.List<Brick> bricks; // Danh sách gạch
    private java.util.List<PowerUp> powerUps; // Danh sách vật phẩm rơi
    private Renderer renderer; // Lớp phụ để vẽ
    private Random rand = new Random(); // Sinh ngẫu nhiên vật phẩm

    private LevelLoader levelLoader;
    private int currentLevel = 1;
    private int totalLevels = 5;

    // Trạng thái game
    private int score = 0; // Điểm
    private int lives = 3; // Số mạng
    private boolean running = false; // Game đang chạy hay không
    private boolean ballLaunched = false; // Bóng đã bắn hay chưa
    private boolean paused = false; // Tạm dừng game
    private java.util.List<PowerUp> activePowerUps = new ArrayList<>();

    // Góc bắn
    private double launchAngle = -90; // Góc bắn mặc định (âm = hướng lên)
    private final double MIN_ANGLE = -180; // Giới hạn trái
    private final double MAX_ANGLE = 0; // Giới hạn phải
    private Sound collisionSound; // Âm thanh va chạm



    /** Khởi tạo toàn bộ game */
    public GameManager() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setOpaque(false); // Đặt kích thước khung
       // setBackground(Color.BLACK); // Màu nền
        setFocusable(true); // Cho phép nhận phím
        collisionSound = new Sound();
        collisionSound.loadSound("/391658__jeckkech__collision.wav");

        initGame(); // Khởi tạo các đối tượng game
        initKeyBindings(); // Gán phím điều khiển

        renderer = new Renderer(); // Tạo renderer để vẽ
    }

    /** Reset game */
    public void initGame() {
        paddle = new Paddle(WIDTH / 2 - 40, HEIGHT - 40,120, 12); // Tạo paddle ở giữa dưới
        balls = new ArrayList<>();
        balls.add(new Ball(WIDTH / 2 - 8, HEIGHT - 60, 8, 3, -3));
        levelLoader = new LevelLoader();
        currentLevel = 1; // Luôn bắt đầu từ level 1
        createLevel(); // Gọi createLevel để tải màn hình chơi
        powerUps = new ArrayList<>();  // Danh sách power-up
        running = true;                // Bắt đầu game
        ballLaunched = false;          // Chưa bắn bóng
        score = 0;                     // Reset điểm
        lives = 3;                     // Reset mạng
        paused = false;                // Reset pause
        activePowerUps.clear();        // Clear active power-ups
    }

    /** Tạo bố cục gạch */
    private void createLevel() {
        bricks = levelLoader.loadLevel(currentLevel);

        // Reset lại trạng thái bóng cho màn mới
        ballLaunched = false;
        paddle.setX(WIDTH / 2 - paddle.getWidth() / 2); // Đưa paddle về giữa

        // Xóa bóng cũ (nếu có) và tạo bóng mới
        balls.clear();
        balls.add(new Ball(WIDTH / 2 -8, HEIGHT - 60, 8, 3, -3));
        alignBallToPaddle();
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
                    if (launchAngle > MAX_ANGLE)
                        launchAngle = MAX_ANGLE;
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
                    if (launchAngle < MIN_ANGLE)
                        launchAngle = MIN_ANGLE;
                }
            }
        });

        // Giữ trái
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        getActionMap().put("left_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingLeft(true); // Paddle sang trái
                if (!ballLaunched)
                    alignBallToPaddle(); // Nếu chưa bắn, bóng di chuyển theo paddle
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
                if (!ballLaunched)
                    alignBallToPaddle(); // Bóng di chuyển cùng paddle
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
                    Ball ballToLaunch = balls.get(0);
                    ballToLaunch.setDx(speed * Math.cos(rad));
                    ballToLaunch.setDy(speed * Math.sin(rad)); // âm vì hướng lên
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

        // Phím ESC để PAUSE / Resume
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
            }
        });
    }

    /** Đặt bóng lên paddle (khi chưa bắn) */
    private void alignBallToPaddle() {
        if (balls.size() == 1) {
            Ball ball = balls.get(0);

    // Tâm paddle
    int px = (int) paddle.getX();
    int py = (int) paddle.getY();
    int pw = paddle.getWidth();
    int ph = paddle.getHeight();
    int pcx = px + pw / 2;
    int ptop = py;

    // Kích thước logic của bóng
    int bw = ball.getWidth();
    int bh = ball.getHeight();
    int rLogic = Math.min(bw, bh) / 2;

    // Bán kính dùng để VẼ (2.5×)
    int rDraw = (int) Math.round(rLogic * BALL_SCALE);

    // ---- Canh tâm X đúng giữa paddle (không phụ thuộc scale) ----
    double ballX = pcx - bw / 2.0;

    // ---- Canh Y để đáy BÓNG VẼ cách mặt trên paddle VISUAL_GAP px ----
    // cy_draw = ptop - VISUAL_GAP - rDraw
    // ballY  = cy_draw - bh/2  (vì setY dùng góc trên-trái theo kích thước logic bh)
    double cy_draw = ptop - VISUAL_GAP - rDraw;
    double ballY = cy_draw + 15 - bh / 2.0;

    ball.setX(ballX);
    ball.setY(ballY);
        }
    }

    public void updateGame(double dt) {
        if (!running || paused) return;

        paddle.update(dt); // Truyền dt

        // Cập nhật và kiểm tra va chạm power-up
        Iterator<PowerUp> pit = powerUps.iterator();
        while (pit.hasNext()) {
            PowerUp p = pit.next();
            p.update(dt); // Power-up rơi xuống
            if (!p.isActive()) {
                pit.remove(); // Xóa nếu hết tác dụng
            }
            // Va chạm paddle → kích hoạt hiệu ứng
            else if (p.getBounds().intersects(paddle.getBounds())) {
                // Áp dụng hiệu ứng trực tiếp lên Paddle và Ball
                // Dùng quả bóng đầu tiên trong danh sách
                p.applyEffect(paddle, balls.isEmpty() ? null : balls.get(0), this);
                p.start();
                activePowerUps.add(p);
                p.deactivate();// Vô hiệu hóa vật phẩm rơi
            }
        }
        
        // Xóa các power-up đã hết thời gian
        activePowerUps.removeIf(active -> active.getRemainingTime() <= 0);

        // Nếu chưa bắn bóng, căn chỉnh quả bóng duy nhất theo paddle
        if (!ballLaunched) {
            alignBallToPaddle();
        }

        // Dùng Iterator để duyệt và xóa bóng một cách an toàn
        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball currentBall = ballIterator.next();
            if (ballLaunched) {
                currentBall.update(dt);
            }

            // Va chạm bóng và paddle
            if (currentBall.getBounds().intersects(paddle.getBounds())) {
                collisionSound.playOnce(); // Phát âm thanh va chạm
                int paddleCenter = (int)paddle.getX() + paddle.getWidth() / 2;
                int ballCenter = (int)currentBall.getX() + currentBall.getWidth() / 2;
                int diff = ballCenter - paddleCenter; // Lệch giữa tâm bóng và paddle
                double factor = diff / (double) (paddle.getWidth() / 2);
                double speed = Math.sqrt(currentBall.getDx() * currentBall.getDx() + currentBall.getDy() * currentBall.getDy());
                double newDx = speed * factor * 1.2; // Góc lệch dựa trên vị trí va chạm
                double newDy = -Math.abs(speed * (1 - Math.abs(factor))); // Luôn bật lên
                if (Math.abs(newDy) < 2) newDy = -2; // Giữ tốc độ tối thiểu
                currentBall.setDx(newDx);
                currentBall.setDy(newDy);
                currentBall.setY(paddle.getY() - currentBall.getHeight() - 1); // Đặt bóng lên trên paddle
            }

            // Va chạm bóng và gạch
            Iterator<Brick> brickIt = bricks.iterator();
            while (brickIt.hasNext()) {
                Brick brick = brickIt.next();
                if (brick.isDestroyed()) continue;

                if (currentBall.getBounds().intersects(brick.getBounds())) {
                    collisionSound.playOnce(); // Phát âm thanh va chạm
                    currentBall.bounceOff(brick); // Bật bóng trước để điều chỉnh vị trí

                    brick.takeHit(); // Trừ máu gạch
                    if (brick.isDestroyed()) {
                        score += 100;

                        // Xác suất rơi power-up tùy theo từng loại gạch
                        if (rand.nextDouble() < brick.getPowerUpDropChance()) {
                            spawnRandomPowerUp((int)(brick.getX() + brick.getWidth() / 2), (int)brick.getY() + brick.getHeight());
                        }
                    }

                    // Ngăn bóng va thêm các gạch khác trong cùng frame
                    break;
                }
            }
        }

        bricks.removeIf(Brick::isDestroyed); // Xóa gạch vỡ
        // Xóa bóng rơi ra ngoài
        balls.removeIf(b -> b.getY() > HEIGHT);

        // Kiểm tra nếu bóng rơi xuống dưới màn hình
        if (ballLaunched && balls.isEmpty()) {
            lives--; // Mất một mạng
            if (lives <= 0) { // Hết mạng → Game Over
                running = false;
                SwingUtilities.invokeLater(() -> {
                    int resp = JOptionPane.showConfirmDialog(this,
                            "Game Over! Score: " + score + "\nPlay again?",
                            "Game Over", JOptionPane.YES_NO_OPTION);
                    if (resp == JOptionPane.YES_OPTION)
                        initGame(); // Chơi lại
                    else
                        System.exit(0); // Thoát game
                });
            } else {
                // Hồi sinh một quả bóng mới
                ballLaunched = false;
                balls.add(new Ball(WIDTH / 2 - 8, HEIGHT - 60, 8, 3, -3));
                alignBallToPaddle();
            }
        }

        // Khi không còn gạch → sang level mới
        if (bricks.isEmpty()) {
           currentLevel++; // Tăng cấp độ
           if (currentLevel > totalLevels) {
              // Xử lý khi người chơi đã thắng toàn bộ game
              running = false;
               SwingUtilities.invokeLater(() -> {
                  JOptionPane.showMessageDialog(this, "Chúc mừng! Bạn đã chiến thắng!\nScore: " + score);
                  System.exit(0);
               });
               return;
           } else {
               // Tải level tiếp theo
               createLevel();
           }
        }

        repaint(); // Vẽ lại frame
    }

    /** Sinh power-up ngẫu nhiên */
    private void spawnRandomPowerUp(int x, int y) {
        double chance = rand.nextDouble();
        if (chance < 0.33) {
            powerUps.add(new ExpandPaddlePowerUp(x - 10, y));
        } else if (chance < 0.66) {
            powerUps.add(new FastBallPowerUp(x - 10, y));
        } else {
            powerUps.add(new MultiBallPowerUp(x - 10, y));
        }
    }

    /** Vẽ frame */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Vẽ tất cả qua Renderer (kể cả overlay)
        renderer.draw(g, paddle, balls, bricks, powerUps, score, lives,
                ballLaunched, launchAngle, paused, activePowerUps);
    }

    // Getter (nếu cần)
    public Paddle getPaddle() {
        return paddle;
    }

    public java.util.List<Ball> getBalls() {
        return balls;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getLives() {
        return lives;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public boolean isBallLaunched() {
        return ballLaunched;
    }
    
    public void launchBall() {
        if (!ballLaunched && !balls.isEmpty()) {
            collisionSound.playOnce();
            ballLaunched = true;
            double speed = 6.0;
            double rad = Math.toRadians(launchAngle);
            Ball ball = balls.get(0); // Chỉ phóng quả bóng đầu tiên
            ball.setDx(speed * Math.cos(rad));
            ball.setDy(speed * Math.sin(rad));
        }
    }
    
    public void adjustLaunchAngle(double delta) {
        if (!ballLaunched) {
            launchAngle += delta;
            if (launchAngle > MAX_ANGLE)
                launchAngle = MAX_ANGLE;
            if (launchAngle < MIN_ANGLE)
                launchAngle = MIN_ANGLE;
        }
    }

    /** Kích hoạt hiệu ứng Multi-Ball */
    public void activateMultiBall() {
        if (balls.isEmpty()) return; // Không có bóng để nhân đôi

        // Lấy quả bóng đầu tiên làm gốc
        Ball originalBall = balls.get(0);
        double x = originalBall.getX();
        double y = originalBall.getY();
        double speed = Math.sqrt(originalBall.getDx() * originalBall.getDx() + originalBall.getDy() * originalBall.getDy());

        // Tạo 1 quả bóng mới với góc lệch
        Ball ball2 = new Ball((int)x, (int)y, 8, 0, 0);
        ball2.setDx(speed * Math.cos(Math.toRadians(90))); // Hướng 90 độ
        ball2.setDy(-speed * Math.sin(Math.toRadians(90)));

        balls.add(ball2);
    }
}