package arkanoid.core;

import java.awt.Color;
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
import arkanoid.entities.LaserBeam;
import arkanoid.entities.Paddle;
import arkanoid.entities.bricks.Brick;
import arkanoid.entities.bricks.NormalBrick;
import arkanoid.entities.bricks.StrongBrick;
import arkanoid.entities.bricks.UnbreakableBrick;
import arkanoid.entities.powerups.PowerUp;
import arkanoid.entities.powerups.PowerUpFactory;
import arkanoid.utils.GameState;
import arkanoid.utils.HighScoreManager;
import arkanoid.utils.LevelLoader;
import arkanoid.utils.ProgressManager;
import arkanoid.utils.SaveManager;
import arkanoid.utils.Sound;
import arkanoid.view.LeaderboardDialog;
import arkanoid.view.Renderer;

public class GameManager extends JPanel {

    private static final double BALL_SCALE = 2.5;
    private static final int VISUAL_GAP = 6;
    private static final double MIN_BALL_SPEED = 122.0;
    private static final double MAX_BALL_SPEED = 720.0;
    private static final int DEFAULT_WIDTH = 1440;
    private static final int DEFAULT_HEIGHT = 800;
    
    private String ballImagePath = "/balls/ball_red.png"; 

    private Paddle paddle;
    private List<Ball> balls;
    private List<Brick> bricks;
    private List<PowerUp> powerUps;
    private List<PowerUp> activePowerUps;
    private Renderer renderer;
    private Random rand;
    private LevelLoader levelLoader;
    private Sound collisionSound;
    private Sound losingSound;

    private int currentLevel;
    private int totalLevels;
    private int score;
    private int lives;
    private int currentScreenWidth;
    private int currentScreenHeight;
   
    
    private boolean running;
    private boolean ballLaunched;
    private boolean paused;
    private boolean isFirstLife;
    
    private Color paddleColor;
    private Color ballColor;
    
    private double launchAngle;
    private final double MIN_ANGLE = -180;
    private final double MAX_ANGLE = 0;

    private ProgressManager progressManager;
    private long levelStartTime;


    public GameManager() {
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setOpaque(false);
        setFocusable(true);

        initializeComponents();
        initGame();
        initKeyBindings();
    }


    
    private void initializeComponents() {
        rand = new Random();
        renderer = new Renderer();
        activePowerUps = new ArrayList<>();
        
        collisionSound = new Sound();
        collisionSound.loadSound("/391658__jeckkech__collision.wav");
        losingSound = new Sound();
        losingSound.loadSound("/losing_sound.wav");
        
        paddleColor = Color.BLUE;
        ballColor = Color.RED;
        launchAngle = -90;
        totalLevels = 5;

        progressManager = ProgressManager.getInstance();
    }

    public void initGame() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0) w = DEFAULT_WIDTH;
        if (h == 0) h = DEFAULT_HEIGHT;

        this.currentScreenWidth = w;
        this.currentScreenHeight = h;

        // Initialize paddle
        paddle = new Paddle(w / 2 - 60, h - 40, 120, 12, paddleColor);
        
        // Initialize balls list
        balls = new ArrayList<>();
        Ball initialBall = createBall(w / 2 - 8, h - 60);
        balls.add(initialBall);

        // Initialize bricks
        levelLoader = new LevelLoader();
        if (currentLevel == 0) {
        currentLevel = 1;
    }
        createLevel(w, h);

        // Initialize power-ups
        powerUps = new ArrayList<>();
        activePowerUps.clear();
        
        // Reset game state
        score = 0;
        lives = 3;
        running = true;
        ballLaunched = false;
        paused = false;
        isFirstLife = true;

        levelStartTime = System.currentTimeMillis();
    }

    public void startFromLevel(int level) {
    if (level < 1 || level > totalLevels) {
        level = 1;
    }
    
    // Kiểm tra level đã unlock chưa
    if (!progressManager.isLevelUnlocked(level)) {
        System.out.println("Level " + level + " chưa mở khóa!");
        level = 1;
    }
    
    this.currentLevel = level;
    initGame();
}

    private Ball createBall(int x, int y) {
        Ball ball = new Ball(x, y, 8, 3, -3, ballColor);
        ball.setBallImagePath(ballImagePath);
        return ball;
    }

    public void createLevel(int screenWidth, int screenHeight) {
        if (levelLoader == null) {
            levelLoader = new LevelLoader();
        }
        
        bricks = levelLoader.loadLevel(currentLevel, screenWidth);
        ballLaunched = false;

        this.currentScreenWidth = screenWidth;
        this.currentScreenHeight = screenHeight;

        // Reset paddle position
        if (paddle != null) {
            paddle.setX(screenWidth / 2 - paddle.getWidth() / 2);
            paddle.setY(screenHeight - 40);
        }
        
        // Reset balls
        if (balls == null) {
            balls = new ArrayList<>();
        } else {
            balls.clear();
        }
        
        Ball newBall = createBall(screenWidth / 2 - 8, screenHeight - 60);
        balls.add(newBall);
        
        alignBallToPaddle();
    }

    public void rescaleGame(int newWidth, int newHeight) {
        if (currentScreenWidth == 0 || currentScreenHeight == 0) return;
        if (currentScreenWidth == newWidth && currentScreenHeight == newHeight) return;

        double scaleX = (double) newWidth / currentScreenWidth;
        double scaleY = (double) newHeight / currentScreenHeight;

        // Rescale paddle
        if (paddle != null) {
            paddle.rescale(scaleX, scaleY);
        }
        
        // Rescale balls
        if (balls != null) {
            for (Ball b : balls) {
                if (b != null) {
                    b.rescale(scaleX, scaleY);
                }
            }
        }
        
        // Rescale bricks
        if (bricks != null) {
            for (Brick b : bricks) {
                if (b != null) {
                    b.rescale(scaleX, scaleY);
                }
            }
        }
        
        // Rescale power-ups
        if (powerUps != null) {
            for (PowerUp p : powerUps) {
                if (p != null) {
                    p.rescale(scaleX, scaleY);
                }
            }
        }

        this.currentScreenWidth = newWidth;
        this.currentScreenHeight = newHeight;
    }

    private void initKeyBindings() {
        // Angle adjustment right (key 6)
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("6"), "angle_right");
        getActionMap().put("angle_right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustLaunchAngle(5);
            }
        });

        // Angle adjustment left (key 4)
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"), "angle_left");
        getActionMap().put("angle_left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adjustLaunchAngle(-5);
            }
        });

        // Move left
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        getActionMap().put("left_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (paddle != null) {
                    paddle.setMovingLeft(true);
                    if (!ballLaunched) {
                        alignBallToPaddle();
                    }
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        getActionMap().put("left_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (paddle != null) {
                    paddle.setMovingLeft(false);
                }
            }
        });

        // Move right
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed RIGHT"), "right_pressed");
        getActionMap().put("right_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (paddle != null) {
                    paddle.setMovingRight(true);
                    if (!ballLaunched) {
                        alignBallToPaddle();
                    }
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        getActionMap().put("right_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (paddle != null) {
                    paddle.setMovingRight(false);
                }
            }
        });

        // Launch ball (SPACE)
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
        getActionMap().put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                launchBall();
            }
        });

        // Restart game (R)
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initGame();
            }
        });

        // Pause/Resume (ESCAPE)
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "pause");
        getActionMap().put("pause", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = !paused;
            }
        });
    }

    public void alignBallToPaddle() {
        if (balls == null || balls.isEmpty() || paddle == null) return;
        if (balls.size() != 1) return;
        
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

    public void updateGame(double dt, int screenWidth, int screenHeight) {
        if (!running || paused) return;

        // Update paddle
        if (paddle != null) {
            paddle.update(dt, screenWidth);
        }

        // Update and collect power-ups
        updatePowerUps(dt, screenHeight);

        // Update active power-ups timers
        if (activePowerUps != null) {
            activePowerUps.removeIf(active -> active.getRemainingTime() <= 0);
        }

        // Align ball to paddle if not launched
        if (!ballLaunched) {
            alignBallToPaddle();
        }

        // Update balls and handle collisions
        updateBalls(dt, screenWidth, screenHeight);

        // Remove destroyed bricks
        if (bricks != null) {
            bricks.removeIf(Brick::isDestroyed);
        }

        // Check if all balls are lost
        if (ballLaunched && (balls == null || balls.isEmpty())) {
            handleBallLost(screenWidth, screenHeight);
        }

        // Check if level is complete
        if (bricks != null && bricks.isEmpty()) {
            handleLevelComplete(screenWidth, screenHeight);
        }
    }

    private void updatePowerUps(double dt, int screenHeight) {
        if (powerUps == null) return;
        
        Iterator<PowerUp> pit = powerUps.iterator();
        while (pit.hasNext()) {
            PowerUp p = pit.next();
            if (p == null) {
                pit.remove();
                continue;
            }
            
            p.update(dt, screenHeight);

            if (paddle != null && p.getBounds().intersects(paddle.getBounds())) {
                Ball firstBall = (balls != null && !balls.isEmpty()) ? balls.get(0) : null;
                p.applyEffect(paddle, firstBall, this, currentScreenWidth);
                p.start();
                if (activePowerUps != null) {
                    activePowerUps.add(p);
                }
                pit.remove();
            } else if (!p.isActive() || p.getY() > screenHeight) {
                pit.remove();
            }
        }
    }

    private void updateBalls(double dt, int screenWidth, int screenHeight) {
        if (balls == null) return;
        
        Iterator<Ball> ballIterator = balls.iterator();
        while (ballIterator.hasNext()) {
            Ball currentBall = ballIterator.next();
            if (currentBall == null) {
                ballIterator.remove();
                continue;
            }

            // Update ball position
            if (ballLaunched) {
                currentBall.update(dt, screenWidth, screenHeight);
            }

            // Check paddle collision
            handlePaddleCollision(currentBall);

            // Check brick collisions
            handleBrickCollisions(currentBall);

            // Remove balls that fell off screen
            if (currentBall.getY() > screenHeight) {
                ballIterator.remove();
            }
        }
        
        // Handle laser collisions
        handleLaserCollisions();
    }

    /**
     * Xử lý va chạm giữa laser và gạch
     */
    private void handleLaserCollisions() {
        if (paddle == null || bricks == null) return;
        
        List<LaserBeam> lasers = paddle.getLasers();
        if (lasers == null || lasers.isEmpty()) return;

        for (LaserBeam laser : lasers) {
            if (!laser.isActive()) continue;

            Iterator<Brick> brickIt = bricks.iterator();
            while (brickIt.hasNext()) {
                Brick brick = brickIt.next();
                if (brick == null || brick.isDestroyed()) continue;

                if (laser.checkCollision(brick.getBounds())) {
                    if (collisionSound != null) {
                        collisionSound.playOnce();
                    }

                    brick.takeHit();
                    laser.deactivate();

                    if (brick.isDestroyed()) {
                        score += 100;

                        // Spawn power-up randomly using Factory Pattern
                        if (rand != null && rand.nextDouble() < brick.getPowerUpDropChance()) {
                            spawnRandomPowerUp((int) (brick.getX() + brick.getWidth() / 2),
                                             (int) brick.getY() + brick.getHeight());
                        }
                    }
                    break; // Laser chỉ phá 1 gạch rồi biến mất
                }
            }
        }
    }

    private void handlePaddleCollision(Ball currentBall) {
        if (paddle == null || currentBall == null) return;
        
        if (currentBall.getBounds().intersects(paddle.getBounds())) {
            // Only bounce if ball is moving downward
            if (currentBall.getDy() > 0) {
                if (collisionSound != null) {
                    collisionSound.playOnce();
                }

                double targetSpeed = currentBall.getBaseSpeed() * currentBall.getSpeedMultiplier();
                targetSpeed = Math.max(MIN_BALL_SPEED, Math.min(MAX_BALL_SPEED, targetSpeed));
                
                int paddleCenter = (int) paddle.getX() + paddle.getWidth() / 2;
                int ballCenter = (int) currentBall.getX() + currentBall.getWidth() / 2;
                int diff = ballCenter - paddleCenter;
                double factor = diff / (double) (paddle.getWidth() / 2);

                // Limit factor to prevent extreme angles
                final double MAX_FACTOR = 0.85;
                factor = Math.max(-MAX_FACTOR, Math.min(MAX_FACTOR, factor));

                double maxAngleOffset = 75.0;
                double bounceAngleRad = Math.toRadians(90 - (factor * maxAngleOffset));
                
                double newDx = targetSpeed * Math.cos(bounceAngleRad);
                double newDy = -targetSpeed * Math.sin(bounceAngleRad);
                
                currentBall.setDx(newDx);
                currentBall.setDy(newDy);
                currentBall.setY(paddle.getY() - currentBall.getHeight() - 1);
            }
        }
    }

    private void handleBrickCollisions(Ball currentBall) {
        if (bricks == null || currentBall == null) return;
        
        Iterator<Brick> brickIt = bricks.iterator();
        while (brickIt.hasNext()) {
            Brick brick = brickIt.next();
            if (brick == null || brick.isDestroyed()) continue;

            if (currentBall.getBounds().intersects(brick.getBounds())) {
                if (collisionSound != null) {
                    collisionSound.playOnce();
                }
                
                currentBall.bounceOff(brick);
                brick.takeHit();

                if (brick.isDestroyed()) {
                    score += 100;
                    
                    // Spawn power-up randomly using Factory Pattern
                    if (rand != null && rand.nextDouble() < brick.getPowerUpDropChance()) {
                        spawnRandomPowerUp((int) (brick.getX() + brick.getWidth() / 2),
                                         (int) brick.getY() + brick.getHeight());
                    }
                }
                break;
            }
        }
    }

    private void handleBallLost(int screenWidth, int screenHeight) {
        lives--;
        if (lives <= 0) {
            onGameOver();
        } else {
            resetBallAndPaddle(screenWidth, screenHeight);
        }
    }

    private void resetBallAndPaddle(int screenWidth, int screenHeight) {
        ballLaunched = false;
        isFirstLife = false;
        
        if (paddle != null) {
            paddle.setX(screenWidth / 2 - paddle.getWidth() / 2);
            paddle.setY(screenHeight - 40);
        }
        
        if (balls == null) {
            balls = new ArrayList<>();
        } else {
            balls.clear();
        }
        
        Ball newBall = createBall(screenWidth / 2 - 8, screenHeight - 60);
        balls.add(newBall);
        
        alignBallToPaddle();
    }

    private void handleLevelComplete(int screenWidth, int screenHeight) {
        long levelTime = System.currentTimeMillis() - levelStartTime;
        progressManager.completeLevel(currentLevel, score, levelTime);
        
        currentLevel++;
        if (currentLevel > totalLevels) {
            handleGameWon();
        } else {
            levelStartTime = System.currentTimeMillis();
            createLevel(screenWidth, screenHeight);
            saveGame();
        }
    }

    private void handleGameWon() {
        running = false;
        long totalTime = progressManager.getProgress().getTotalPlayTime();
        int totalScore = progressManager.getProgress().getTotalScore();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "Chúc mừng! Bạn đã chiến thắng!\nĐiểm số: " + score,
                "Chiến thắng!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }

    /**
     * Spawn power-up sử dụng Factory Method Pattern
     */
    private void spawnRandomPowerUp(int x, int y) {
        if (powerUps == null) {
        powerUps = new ArrayList<>();
            }
    
            // SỬ DỤNG FACTORY METHOD PATTERN
        PowerUp powerUp = PowerUpFactory.createWeightedRandomPowerUp(x - 50, y);
        powerUps.add(powerUp);
    }

    public void activateMultiBall() {
        if (balls == null || balls.isEmpty()) return;
        
        Ball originalBall = balls.get(0);
        double x = originalBall.getX();
        double y = originalBall.getY();
        double speed;

        if (!ballLaunched) {
            speed = originalBall.getBaseSpeed() * originalBall.getSpeedMultiplier();
            
            double rad = Math.toRadians(launchAngle);
            originalBall.setDx(speed * Math.cos(rad));
            originalBall.setDy(speed * Math.sin(rad));
        
            // Create ball going left
            Ball ball2 = createBall((int) x, (int) y);
            double rad2 = Math.toRadians(launchAngle + 30);
            ball2.setDx(speed * Math.cos(rad2));
            ball2.setDy(speed * Math.sin(rad2));
            balls.add(ball2);
            
            // Create ball going right
            Ball ball3 = createBall((int) x, (int) y);
            double rad3 = Math.toRadians(launchAngle - 30);
            ball3.setDx(speed * Math.cos(rad3));
            ball3.setDy(speed * Math.sin(rad3));
            balls.add(ball3);

            ballLaunched = true;
            isFirstLife = false;
        } else {
            speed = Math.sqrt(originalBall.getDx() * originalBall.getDx() +
                            originalBall.getDy() * originalBall.getDy());

            Ball ball2 = createBall((int) x, (int) y);
            ball2.setDx(-speed * Math.cos(Math.toRadians(30)));
            ball2.setDy(-speed * Math.sin(Math.toRadians(30)));
            balls.add(ball2);

            Ball ball3 = createBall((int) x, (int) y);
            ball3.setDx(speed * Math.cos(Math.toRadians(30)));
            ball3.setDy(speed * Math.sin(Math.toRadians(30)));
            balls.add(ball3);
        }
    }

    private void onGameOver() {
        running = false;
        
        progressManager.failLevel(currentLevel);
        deleteSavedGame();

        if (losingSound != null) {
            losingSound.playOnce();
        }

        SwingUtilities.invokeLater(() -> {
            String name = JOptionPane.showInputDialog(this,
                "Game Over!\nĐiểm của bạn: " + score + "\n\nNhập tên để lưu điểm:",
                "Game Over", JOptionPane.PLAIN_MESSAGE);

            if (name == null || name.trim().isEmpty()) {
                name = "Player";
            }

            try {
                HighScoreManager hsm = new HighScoreManager();
                hsm.addScore(name.trim(), score);
                LeaderboardDialog.showTop(SwingUtilities.getWindowAncestor(this), 10);
            } catch (Exception ex) {
                System.err.println("Lỗi khi lưu điểm: " + ex.getMessage());
                ex.printStackTrace();
            }

            int response = JOptionPane.showConfirmDialog(this, 
                "Chơi lại?", 
                "Game Over", 
                JOptionPane.YES_NO_OPTION);
                
            if (response == JOptionPane.YES_OPTION) {
                currentLevel = 1;
                initGame();
            } else {
                System.exit(0);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        // Intentionally empty - rendering handled by paintComponent(Graphics, int, int)
    }

    public void paintComponent(Graphics g, int screenWidth, int screenHeight) {
        if (renderer != null) {
            renderer.draw(g, paddle, paddleColor, balls, ballColor, bricks, powerUps, 
                         score, lives, ballLaunched, launchAngle, paused, 
                         activePowerUps, isFirstLife);
        }
    }

    public void launchBall() {
        if (!ballLaunched && balls != null && !balls.isEmpty()) {
            if (collisionSound != null) {
                collisionSound.playOnce();
            }
            
            ballLaunched = true;
            isFirstLife = false;
            
            double speed = 360.0;
            double rad = Math.toRadians(launchAngle);
            Ball ball = balls.get(0);
            ball.setDx(speed * Math.cos(rad));
            ball.setDy(speed * Math.sin(rad));
        }
    }

    public void adjustLaunchAngle(double delta) {
        if (!ballLaunched) {
            launchAngle += delta;
            launchAngle = Math.max(MIN_ANGLE, Math.min(MAX_ANGLE, launchAngle));
        }
    }

    // Setters
    public void setBallImagePath(String imagePath) {
        this.ballImagePath = imagePath;
        if (balls != null) {
            for (Ball ball : balls) {
                if (ball != null) {
                    ball.setBallImagePath(imagePath);
                }
            }
        }
    }

    public void setPaddleColor(Color color) {
        this.paddleColor = color;
        if (paddle != null) {
            paddle.setColor(color);
        }
    }

    public void setBallColor(Color color) {
        this.ballColor = color;
        if (balls != null) {
            for (Ball ball : balls) {
                if (ball != null) {
                    ball.setBallColor(color);
                }
            }
        }
    }

    // Getters
    public Paddle getPaddle() { 
        return paddle; 
    }
    
    public List<Ball> getBalls() { 
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
    
    public boolean isFirstLife() { 
        return isFirstLife; 
    }
    
    public boolean isGameOver() { 
        return !running; 
    }
    
    public boolean isPaused() {
        return paused;
    }

    //them helper methof
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    //set level
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }   

    public boolean saveGame() {


    if (!running) {
        System.out.println("Cannot save when game is not running");
        return false;
    }
    
    GameState state = new GameState();
    
    // Game info
    state.currentLevel = this.currentLevel;
    state.score = this.score;
    state.lives = this.lives;
    state.levelStartTime = this.levelStartTime;
    
    // Paddle
    if (paddle != null) {
        state.paddleX = paddle.getX();
        state.paddleY = paddle.getY();
        state.paddleWidth = paddle.getWidth();
        state.paddleDefaultWidth = paddle.getDefaultWidth();
        state.isPaddleExpanded = paddle.isExpanded();
        state.paddleExpandRemainMs = paddle.getExpandRemainMs();
        state.isPaddleLaserActive = paddle.isLaserActive();
        state.paddleLaserRemainMs = paddle.isLaserActive() ? 
            paddle.getLaserRemainingTime() * 1000L : 0;
    }
    
    // Balls
    state.balls = new ArrayList<>();
    if (balls != null) {
        for (Ball ball : balls) {
            if (ball != null) {
                GameState.BallState bs = new GameState.BallState(
                    ball.getX(), ball.getY(),
                    ball.getdx(), ball.getdy(),
                    ball.getRadius()
                );
                bs.speedMultiplier = ball.getSpeedMultiplier();
                bs.fastRemainMs = ball.getFastEndTime() > 0 ? 
                    (ball.getFastEndTime() - System.currentTimeMillis()) : 0;
                bs.imagePath = ball.getBallImagePath();
                bs.colorRGB = ball.getBallColor().getRGB();
                state.balls.add(bs);
            }
        }
    }
    
    // Bricks
    state.bricks = new ArrayList<>();
    if (bricks != null) {
        for (Brick brick : bricks) {
            if (brick != null && !brick.isDestroyed()) {
                String type = "normal";
                if (brick instanceof UnbreakableBrick) type = "unbreakable";
                else if (brick instanceof StrongBrick) type = "strong";
                
                GameState.BrickState bs = new GameState.BrickState(
                    (int)brick.getX(), (int)brick.getY(),
                    brick.getWidth(), brick.getHeight(),
                    brick.getHitPoints(), type
                );
                state.bricks.add(bs);
            }
        }
    }
    
    // Power-ups (đang rơi)
    state.powerUps = new ArrayList<>();
    if (powerUps != null) {
        for (PowerUp p : powerUps) {
            if (p != null && p.isActive()) {
                // SỬ DỤNG FACTORY METHOD PATTERN
                String type = PowerUpFactory.getTypeName(p);
                
                GameState.PowerUpState ps = new GameState.PowerUpState(
                    p.getX(), p.getY(), type
                );
                state.powerUps.add(ps);
            }
        }
    }
    
    // Launch state
    state.ballLaunched = this.ballLaunched;
    state.launchAngle = this.launchAngle;
    
    // Customization
    state.ballImagePath = this.ballImagePath;
    state.paddleColorRGB = this.paddleColor.getRGB();
    state.ballColorRGB = this.ballColor.getRGB();
    
    // Save to file
    return SaveManager.getInstance().save(state);
}

// ============== LOAD GAME ==============

/**
 * Tải trạng thái game đã lưu
 */
public boolean loadGame() {
    GameState state = SaveManager.getInstance().load();
    if (state == null) {
        System.out.println("No saved game to load");
        return false;
    }
    
    // Restore game info
    this.currentLevel = state.currentLevel;
    this.score = state.score;
    this.lives = state.lives;
    this.levelStartTime = state.levelStartTime;
    
    // Restore customization
    this.ballImagePath = state.ballImagePath;
    this.paddleColor = new Color(state.paddleColorRGB);
    this.ballColor = new Color(state.ballColorRGB);
    
    int w = getWidth();
    int h = getHeight();
    if (w == 0) w = DEFAULT_WIDTH;
    if (h == 0) h = DEFAULT_HEIGHT;
    
    // Restore paddle
    paddle = new Paddle(
        (int)state.paddleX, (int)state.paddleY,
        state.paddleWidth, 12, paddleColor);
    paddle.setDefaultWidth(state.paddleDefaultWidth);
    
    if (state.isPaddleExpanded && state.paddleExpandRemainMs > 0) {
        paddle.applyExpand(
            state.paddleWidth - state.paddleDefaultWidth,
            state.paddleExpandRemainMs,
            w
        );
    }
    
    if (state.isPaddleLaserActive && state.paddleLaserRemainMs > 0) {
        paddle.activateLaser(state.paddleLaserRemainMs, 500);
    }
    
    // Restore balls
    balls = new ArrayList<>();
    for (GameState.BallState bs : state.balls) {
        Ball ball = new Ball(
            (int)bs.x, (int)bs.y, bs.radius,
            bs.vx, bs.vy, new Color(bs.colorRGB)
        );
        ball.setBallImagePath(bs.imagePath);
        
        if (bs.speedMultiplier != 1.0 && bs.fastRemainMs > 0) {
            ball.setSpeedMultiplier(bs.speedMultiplier, bs.fastRemainMs);
        }
        
        balls.add(ball);
    }
    
    // Restore bricks
    bricks = new ArrayList<>();
    for (GameState.BrickState bs : state.bricks) {
        Brick brick = null;
        
        brick = switch (bs.type) {
            case "unbreakable" -> new UnbreakableBrick(bs.x, bs.y, bs.width, bs.height);
            case "strong" -> new StrongBrick(bs.x, bs.y, bs.width, bs.height, bs.hitPoints);
            default -> new NormalBrick(bs.x, bs.y, bs.width, bs.height);
        };
        
        if (brick != null) {
            bricks.add(brick);
        }
    }
    
    // Restore power-ups
    powerUps = new ArrayList<>();
    for (GameState.PowerUpState ps : state.powerUps) {
        
         PowerUp powerUp = PowerUpFactory.createPowerUpByName(
            ps.type, 
            (int)ps.x, 
            (int)ps.y
        );
        
        if (powerUp != null) {
            powerUps.add(powerUp);
        }
    }
    
    // Restore launch state
    this.ballLaunched = state.ballLaunched;
    this.launchAngle = state.launchAngle;
    
    // Reset other states
    activePowerUps = new ArrayList<>();
    running = true;
    paused = false;
    isFirstLife = false;
    
    this.currentScreenWidth = w;
    this.currentScreenHeight = h;
    
    System.out.println("Game loaded successfully!");
    return true;

     
}

/**
 * Kiểm tra có save game không
 */
public boolean hasSavedGame() {
    return SaveManager.getInstance().hasSavedGame();
}

/**
 * Xóa save game
 */
public void deleteSavedGame() {
    SaveManager.getInstance().deleteSave();
}




}