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
import arkanoid.entities.Paddle;
import arkanoid.entities.bricks.Brick;
import arkanoid.entities.powerups.ExpandPaddlePowerUp;
import arkanoid.entities.powerups.FastBallPowerUp;
import arkanoid.entities.powerups.MultiBallPowerUp;
import arkanoid.entities.powerups.PowerUp;
import arkanoid.utils.HighScoreManager;
import arkanoid.utils.LevelLoader;
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
        currentLevel = 1;
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
                    
                    // Spawn power-up randomly
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
        currentLevel++;
        if (currentLevel > totalLevels) {
            handleGameWon();
        } else {
            createLevel(screenWidth, screenHeight);
        }
    }

    private void handleGameWon() {
        running = false;
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "Chúc mừng! Bạn đã chiến thắng!\nĐiểm số: " + score,
                "Chiến thắng!", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        });
    }

    private void spawnRandomPowerUp(int x, int y) {
        if (powerUps == null) {
            powerUps = new ArrayList<>();
        }
        
        double chance = rand.nextDouble();
        PowerUp powerUp;
        
        if (chance < 0.33) {
            powerUp = new ExpandPaddlePowerUp(x - 50, y);
        } else if (chance < 0.66) {
            powerUp = new FastBallPowerUp(x - 50, y);
        } else {
            powerUp = new MultiBallPowerUp(x - 50, y);
        }
        
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
}