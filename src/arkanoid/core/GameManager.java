package arkanoid.core;

/**
 * GameManager.java
 *
 * Quản lý toàn bộ logic game Arkanoid: paddle, ball, bricks, power-ups,
 * xử lý va chạm, điểm, mạng, cập nhật trạng thái và vẽ khung hình.
 */
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import arkanoid.entities.Ball;
import arkanoid.entities.Paddle;
import arkanoid.entities.bricks.Brick;
import arkanoid.entities.bricks.NormalBrick;
import arkanoid.entities.bricks.StrongBrick;
import arkanoid.entities.bricks.UnbreakableBrick;
import arkanoid.entities.powerups.ExpandPaddlePowerUp;
import arkanoid.entities.powerups.FastBallPowerUp;
import arkanoid.entities.powerups.MultiBallPowerUp;
import arkanoid.entities.powerups.PowerUp;
import arkanoid.utils.GameState;
import arkanoid.utils.HighScoreManager;
import arkanoid.utils.LevelLoader;
import arkanoid.utils.SaveManager;
import arkanoid.utils.Sound;
import arkanoid.view.LeaderboardDialog;  
import arkanoid.view.Renderer;

public class GameManager extends JPanel {

    public static final int WIDTH = 1440; //Chiều rộng khung game
    public static final int HEIGHT = 800; // Chiều cao khung game
    private static final double BALL_SCALE = 2.5;
    private static final int VISUAL_GAP = 6;// khoảng hở giữa bóng (đã scale) và paddle

    private Paddle paddle; //Thanh đỡ (người chơi điều khiển)
    private List<Ball> balls;
    private List<Brick> bricks;
    private List<PowerUp> powerUps;
    private Renderer renderer;
    private Random rand = new Random();  // Sinh ngẫu nhiên vật phẩm


    private LevelLoader levelLoader;  
    private int currentLevel = 1;
    private int totalLevels = 5;

    private int score = 0;
    private int lives = 3;
    private boolean running = false;
    private boolean ballLaunched = false;
    private boolean paused = false;
    private boolean isFirstLife = true;
    private List<PowerUp> activePowerUps = new ArrayList<>();

    private double launchAngle = -90; // Góc bắn mặc định (âm = hướng lên)
    private final double MIN_ANGLE = -180; // Giới hạn trái
    private final double MAX_ANGLE = 0; // Giới hạn phải
    private Sound collisionSound;
    private Sound losingSound;

    public GameManager() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setOpaque(false);
        setFocusable(true);
        
        collisionSound = new Sound();
        collisionSound.loadSound("/391658__jeckkech__collision.wav");
        losingSound = new Sound();
        losingSound.loadSound("/losing_sound.wav");

        initGame();
        initKeyBindings();
        renderer = new Renderer();
    }

    public void initGame() {
        paddle = new Paddle(WIDTH / 2 - 40, HEIGHT - 40, 120, 12);
        balls = new ArrayList<>();
        balls.add(new Ball(WIDTH / 2 - 8, HEIGHT - 60, 8, 3, -3));
        
        levelLoader = new LevelLoader();  
        currentLevel = 1;
        createLevel();
        
        powerUps = new ArrayList<>();
        running = true;
        ballLaunched = false;
        isFirstLife = true;
        score = 0;
        lives = 3;
        paused = false;
        activePowerUps.clear();
    }

    private void createLevel() {
        bricks = levelLoader.loadLevel(currentLevel);  
        ballLaunched = false;
        paddle.setX(WIDTH / 2 - paddle.getWidth() / 2);
        balls.clear();
        balls.add(new Ball(WIDTH / 2 - 8, HEIGHT - 60, 8, 3, -3));
        alignBallToPaddle();
    }

    private void initKeyBindings() {
        // ... (giữ nguyên code cũ)
        
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

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        getActionMap().put("left_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingLeft(true);
                if (!ballLaunched) alignBallToPaddle();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        getActionMap().put("left_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingLeft(false);
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed RIGHT"), "right_pressed");
        getActionMap().put("right_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingRight(true);
                if (!ballLaunched) alignBallToPaddle();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        getActionMap().put("right_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paddle.setMovingRight(false);
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
        getActionMap().put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ballLaunched) {
                    collisionSound.playOnce();
                    ballLaunched = true;
                    double speed = 6.0;
                    double rad = Math.toRadians(launchAngle);
                    Ball ballToLaunch = balls.get(0);
                    ballToLaunch.setDx(speed * Math.cos(rad));
                    ballToLaunch.setDy(speed * Math.sin(rad));
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initGame();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
            }
        });
    }

    public void alignBallToPaddle() {
        if (balls.size() == 1) {
            Ball ball = balls.get(0);
            int px = (int) paddle.getX();
            int py = (int) paddle.getY();
            int pw = paddle.getWidth();
            int pcx = px + pw / 2;
            int ptop = py;

            int bw = ball.getWidth();
            int bh = ball.getHeight();
            int rLogic = Math.min(bw, bh) / 2;
            int rDraw = (int) Math.round(rLogic * BALL_SCALE);

            double ballX = pcx - bw / 2.0;
            double cy_draw = ptop - VISUAL_GAP - rDraw;
            double ballY = cy_draw + 15 - bh / 2.0;

            ball.setX(ballX);
            ball.setY(ballY);
        }
    }

    public void updateGame(double dt) {
        if (!running || paused) return;

        paddle.update(dt);

        Iterator<PowerUp> pit = powerUps.iterator();
        while (pit.hasNext()) {
            PowerUp p = pit.next();
            p.update(dt);
            
            if (p.getBounds().intersects(paddle.getBounds())) {
                p.applyEffect(paddle, balls.isEmpty() ? null : balls.get(0), this);
                p.start();
                activePowerUps.add(p);
                pit.remove();
            } else if (!p.isActive() || p.getY() > HEIGHT) {
                pit.remove();
            }
        }
        
        activePowerUps.removeIf(active -> active.getRemainingTime() <= 0);

        if (!ballLaunched) {
            alignBallToPaddle();
        }

        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball currentBall = ballIterator.next();
            if (ballLaunched) {
                currentBall.update(dt);
            }

            if (currentBall.getBounds().intersects(paddle.getBounds())) {
                collisionSound.playOnce();
                int paddleCenter = (int)paddle.getX() + paddle.getWidth() / 2;
                int ballCenter = (int)currentBall.getX() + currentBall.getWidth() / 2;
                int diff = ballCenter - paddleCenter;
                double factor = diff / (double) (paddle.getWidth() / 2);
                double speed = Math.sqrt(currentBall.getDx() * currentBall.getDx() + currentBall.getDy() * currentBall.getDy());
                double newDx = speed * factor * 1.2;
                double newDy = -Math.abs(speed * (1 - Math.abs(factor)));
                if (Math.abs(newDy) < 2) newDy = -2;
                currentBall.setDx(newDx);
                currentBall.setDy(newDy);
                currentBall.setY(paddle.getY() - currentBall.getHeight() - 1);
            }

            Iterator<Brick> brickIt = bricks.iterator();
            while (brickIt.hasNext()) {
                Brick brick = brickIt.next();
                if (brick.isDestroyed()) continue;

                if (currentBall.getBounds().intersects(brick.getBounds())) {
                    collisionSound.playOnce();
                    currentBall.bounceOff(brick);
                    brick.takeHit();
                    
                    if (brick.isDestroyed()) {
                        score += 100;
                        if (rand.nextDouble() < brick.getPowerUpDropChance()) {
                            spawnRandomPowerUp((int)(brick.getX() + brick.getWidth() / 2), (int)brick.getY() + brick.getHeight());
                        }
                    }
                    break;
                }
            }
        }

        bricks.removeIf(Brick::isDestroyed);
        balls.removeIf(b -> b.getY() > HEIGHT);

        if (ballLaunched && balls.isEmpty()) {
            lives--;
            if (lives <= 0) {
                onGameOver();  
            } else {
                ballLaunched = false;
                isFirstLife = false;
                balls.add(new Ball(WIDTH / 2 - 8, HEIGHT - 60, 8, 3, -3));
                alignBallToPaddle();
            }
        }

        if (bricks.isEmpty()) {
           currentLevel++;
           if (currentLevel > totalLevels) {
              running = false;
               SwingUtilities.invokeLater(() -> {
                  JOptionPane.showMessageDialog(this, "Chúc mừng! Bạn đã chiến thắng!\nScore: " + score);
                  System.exit(0);
               });
               return;
           } else {
               createLevel();
           }
        }

        repaint();
    }

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.draw(g, paddle, balls, bricks, powerUps, score, lives,
                ballLaunched, launchAngle, paused, activePowerUps, isFirstLife);
    }

    // Getters
    public Paddle getPaddle() { return paddle; }
    public List<Ball> getBalls() { return balls; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public boolean isRunning() { return running; }
    public boolean isBallLaunched() { return ballLaunched; }
    public boolean isFirstLife() { return isFirstLife; }
    
    public void launchBall() {
        if (!ballLaunched && !balls.isEmpty()) {
            collisionSound.playOnce();
            ballLaunched = true;
            isFirstLife = false;
            double speed = 6.0;
            double rad = Math.toRadians(launchAngle);
            Ball ball = balls.get(0);
            ball.setDx(speed * Math.cos(rad));
            ball.setDy(speed * Math.sin(rad));
        }
    }
    
    public void adjustLaunchAngle(double delta) {
        if (!ballLaunched) {
            launchAngle += delta;
            if (launchAngle > MAX_ANGLE) launchAngle = MAX_ANGLE;
            if (launchAngle < MIN_ANGLE) launchAngle = MIN_ANGLE;
        }
    }

    public void activateMultiBall() {
        if (balls.isEmpty()) return;
        Ball originalBall = balls.get(0);
        double x = originalBall.getX();
        double y = originalBall.getY();
        double speed = Math.sqrt(originalBall.getDx() * originalBall.getDx() + originalBall.getDy() * originalBall.getDy());
        Ball ball2 = new Ball((int)x, (int)y, 8, 0, 0);
        ball2.setDx(speed * Math.cos(Math.toRadians(90)));
        ball2.setDy(-speed * Math.sin(Math.toRadians(90)));
        balls.add(ball2);
    }

    
    private GameState toGameState() { 
        GameState s = new GameState();
        s.levelIndex = this.currentLevel;  
        s.score = this.score;
        s.lives = this.lives;

        s.paddleX = paddle.getX();
        s.paddleY = paddle.getY();
        s.paddleWidth = paddle.getWidth();

        List<GameState.BallState> bl = new ArrayList<>();
        for(Ball b : this.balls) {
            GameState.BallState bs = new GameState.BallState();
            bs.x = b.getX();
            bs.y = b.getY();
            bs.vx = b.getDx();
            bs.vy = b.getDy();
            bs.speedMultiplier = b.getSpeedMultiplier();
            bs.fastRemainMs = b.getFastRemainingTime();
            bs.radius = b.getRadius();
            bl.add(bs);
        }
        s.balls = bl;

        List<GameState.BrickState> br = new ArrayList<>();
        for(Brick b : this.bricks) {
            GameState.BrickState bs = new GameState.BrickState();
            bs.x = (int)b.getX();
            bs.y = (int)b.getY();
            bs.w = b.getWidth();
            bs.h = b.getHeight();
            bs.hp = b.getHitPoints();
            
          
            if (b instanceof UnbreakableBrick) {
                bs.type = "UNBREAKABLE";
            } else if (b instanceof StrongBrick) {
                if (b.getHitPoints() == 3) bs.type = "STRONG3";
                else bs.type = "STRONG2";
            } else {
                bs.type = "NORMAL";
            }
            
            br.add(bs);
        }
        s.bricks = br;
        
        s.isPaddleExpanded = paddle.isExpanded();
        s.paddleExpandRemainMs = paddle.getExpandRemainMs();
        return s;
    }

   
    private void applyGameState(GameState s) {
        this.currentLevel = s.levelIndex; 
        this.score = s.score;
        this.lives = s.lives;

        paddle.setPosition(s.paddleX, s.paddleY);
        paddle.setWidth((int)s.paddleWidth);

        bricks.clear();
        for (GameState.BrickState bs : s.bricks) {
            Brick b = createBrickByType(bs.type, (int)bs.x, (int)bs.y, bs.w, bs.h);
            b.setHp(bs.hp);
            this.bricks.add(b);
        }
        
        balls.clear();
        if(s.version >= GameState.VERSION && s.balls != null && !s.balls.isEmpty()) {
            for (GameState.BallState bs : s.balls) {
                Ball b = new Ball((int) bs.x, (int) bs.y, bs.radius, bs.vx, bs.vy);
                if (bs.speedMultiplier > 1.0 && bs.fastRemainMs > 0) {
                    b.setSpeedMultiplier(bs.speedMultiplier, bs.fastRemainMs);
                }
                balls.add(b);
            }
            ballLaunched = false;
        }
        
        if (s.isPaddleExpanded && s.paddleExpandRemainMs > 0) {
            paddle.applyExpand(40, s.paddleExpandRemainMs);
        }
        
        repaint();
    }

    /**
     *  Tạo Brick theo loại - CÓ THỂ DÙNG LẠI trong createLevel()
     */
    private Brick createBrickByType(String type, int x, int y, int width, int height) {
        switch (type) {
            case "NORMAL":
                return new NormalBrick(x, y, width, height);
            case "STRONG2":
                return new StrongBrick(x, y, width, height, 2);
            case "STRONG3":
                return new StrongBrick(x, y, width, height, 3);
            case "UNBREAKABLE":
                return new UnbreakableBrick(x, y, width, height);
            default:
                return new NormalBrick(x, y, width, height);
        }
    }

    public void saveGame() {
        try {
            SaveManager.save(toGameState());  //  
            JOptionPane.showMessageDialog(this, "Đã lưu game!");
        } catch (Exception ex) {
           JOptionPane.showMessageDialog(this, "Lưu thất bại: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
               
    public void loadGame() {
        try {
            GameState s = SaveManager.load();
            applyGameState(s);
            JOptionPane.showMessageDialog(this, "Đã tải game!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể tải: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     *  Xử lý khi game over - lưu điểm và hiển thị bảng xếp hạng
     * CHỈ GIỮ LẠI METHOD NÀY, XÓA METHOD TRÙNG KHÁC
     */
    private void onGameOver() {
        running = false;
        losingSound.playOnce();
        
        SwingUtilities.invokeLater(() -> {
            String name = JOptionPane.showInputDialog(
                this, 
                "Game Over!\nĐiểm của bạn: " + score + "\n\nNhập tên để lưu điểm:", 
                "Game Over", 
                JOptionPane.PLAIN_MESSAGE
            );
            
            if (name == null || name.trim().isEmpty()) {
                name = "Player";
            }
            
            try {
                HighScoreManager hsm = new HighScoreManager();
                hsm.addScore(name.trim(), score);
                LeaderboardDialog.showTop(SwingUtilities.getWindowAncestor(this), 10);
            } catch (Exception ex) {
                System.err.println("Lỗi khi lưu điểm: " + ex.getMessage());
            }
            
            int response = JOptionPane.showConfirmDialog(
                this,
                "Chơi lại?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
            );
            
            if (response == JOptionPane.YES_OPTION) {
                initGame();
            } else {
                System.exit(0);
            }
        });
    }
}