package arkanoid.entities;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Paddle.java
 *
 * Thanh đỡ do người chơi điều khiển.
 * Hỗ trợ mở rộng (power-up), giới hạn di chuyển trong biên.
 */
public class Paddle extends MovableObject {

    // Constants
    private static final double DEFAULT_SPEED = 420.0;
    private static final int MIN_WIDTH = 40;
    private static final int MAX_WIDTH = 300;
    
    // Movement properties
    private double speed;
    private boolean movingLeft;
    private boolean movingRight;
    
    // Expansion properties
    private int defaultWidth;
    private long expandEndTime;
    
    // Laser properties
    private boolean laserActive;
    private long laserEndTime;
    private long lastLaserFireTime;
    private long laserFireRate;
    private List<LaserBeam> lasers;
    
    // Visual properties
    private Color color;

    /**
     * Constructor
     * @param x vị trí X ban đầu
     * @param y vị trí Y ban đầu
     * @param width chiều rộng ban đầu
     * @param height chiều cao
     * @param color màu sắc
     */
    public Paddle(int x, int y, int width, int height, Color color) {
        super(x, y, width, height);
        this.defaultWidth = width;
        this.color = color != null ? color : Color.BLUE;
        this.speed = DEFAULT_SPEED;
        this.movingLeft = false;
        this.movingRight = false;
        this.expandEndTime = 0;

        // Initialize laser system
        this.laserActive = false;
        this.laserEndTime = 0;
        this.lastLaserFireTime = 0;
        this.laserFireRate = 500;
        this.lasers = new ArrayList<>();
    }

    /**
     * Update paddle position and state
     * @param dt delta time in seconds
     * @param screenWidth screen width for boundary checking
     */
    public void update(double dt, int screenWidth) {
        if (dt <= 0 || screenWidth <= 0) return;
        
        // Update position based on movement flags
        updatePosition(dt, screenWidth);
        
        // Check and update expansion state
        updateExpansionState();
        
        // Update laser state
        updateLaserState(dt);
    }

    /**
     * Legacy update method (for compatibility)
     */
    @Override
    public void update(double dt) {
        // Intentionally empty - use update(double, int) instead
    }

    /**
     * Update paddle position based on movement flags
     */
    private void updatePosition(double dt, int screenWidth) {
        double movement = 0;
        
        if (movingLeft) {
            movement -= speed * dt;
        }
        if (movingRight) {
            movement += speed * dt;
        }
        
        // Apply movement
        x += movement;
        
        // Clamp position to screen boundaries
        clampToScreen(screenWidth);
    }

    /**
     * Keep paddle within screen boundaries
     */
    private void clampToScreen(int screenWidth) {
        if (x < 0) {
            x = 0;
        }
        if (x + width > screenWidth) {
            x = screenWidth - width;
        }
    }

    /**
     * Check and update expansion state
     */
    private void updateExpansionState() {
        if (expandEndTime > 0 && System.currentTimeMillis() >= expandEndTime) {
            resetWidth();
        }
    }

    /**
     * Reset paddle to default width
     */
    private void resetWidth() {
        width = defaultWidth;
        expandEndTime = 0;
    }

    // ======= LASER SYSTEM =======

    /**
     * Update laser state - check expiration and auto-fire
     */
    private void updateLaserState(double dt) {
        // Check if laser duration expired
        if (laserActive && System.currentTimeMillis() >= laserEndTime) {
            deactivateLaser();
        }
        
        // Auto fire lasers
        if (laserActive) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastLaserFireTime >= laserFireRate) {
                fireLaser();
                lastLaserFireTime = currentTime;
            }
        }
        
        // Update all laser beams
        if (lasers != null) {
            lasers.removeIf(laser -> !laser.isActive());
            for (LaserBeam laser : lasers) {
                if (laser != null) {
                    laser.update(dt);
                }
            }
        }
    }

    /**
     * Activate laser power-up
     * @param durationMillis how long the laser stays active
     * @param fireRateMillis time between each laser shot
     */
    public void activateLaser(long durationMillis, long fireRateMillis) {
        if (durationMillis <= 0 || fireRateMillis <= 0) return;

        this.laserActive = true;
        this.laserEndTime = System.currentTimeMillis() + durationMillis;
        this.laserFireRate = fireRateMillis;
        this.lastLaserFireTime = 0; // Fire immediately
    }

    /**
     * Deactivate laser
     */
    public void deactivateLaser() {
        this.laserActive = false;
        this.laserEndTime = 0;
    }

    /**
     * Fire laser beams from both corners of paddle
     */
    private void fireLaser() {
        if (lasers == null) {
            lasers = new ArrayList<>();
        }
        
        // Fire from left corner
        double leftX = x + 5;
        double leftY = y;
        lasers.add(new LaserBeam(leftX, leftY));
        
        // Fire from right corner
        double rightX = x + width - 9;
        double rightY = y;
        lasers.add(new LaserBeam(rightX, rightY));
    }

    /**
     * Get list of active laser beams
     */
    public List<LaserBeam> getLasers() {
        return lasers;
    }

    /**
     * Check if laser is currently active
     */
    public boolean isLaserActive() {
        return laserActive;
    }

    /**
     * Get remaining laser time in seconds
     */
    public int getLaserRemainingTime() {
        if (!laserActive || laserEndTime == 0) {
            return 0;
        }
        long remainingMillis = laserEndTime - System.currentTimeMillis();
        if (remainingMillis <= 0) {
            return 0;
        }
        return (int) Math.ceil(remainingMillis / 1000.0);
    }

    // ======= EXPANSION METHODS =======

    /**
     * Apply expansion power-up
     * @param extraPixels additional pixels to add to default width
     * @param durationMillis duration of the effect in milliseconds
     * @param screenWidth screen width for boundary adjustment
     */
    public void applyExpand(int extraPixels, long durationMillis, int screenWidth) {
        if (extraPixels <= 0 || durationMillis <= 0) return;
        
        // Calculate new width
        int newWidth = defaultWidth + extraPixels;
        
        // Clamp to reasonable limits
        newWidth = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, newWidth));
        
        // Apply new width
        width = newWidth;
        
        // Adjust position if paddle goes out of bounds
        if (x + width > screenWidth) {
            x = Math.max(0, screenWidth - width);
        }
        
        // Set expiration time
        expandEndTime = System.currentTimeMillis() + durationMillis;
    }

    /**
     * Cancel expansion immediately
     */
    public void cancelExpansion() {
        resetWidth();
    }

    /**
     * Rescale paddle (for screen resize)
     */
    @Override
    public void rescale(double scaleX, double scaleY) {
        if (scaleX <= 0 || scaleY <= 0) return;
        
        // Rescale position and dimensions
        super.rescale(scaleX, scaleY);
        
        // Rescale default width
        defaultWidth = (int) Math.round(defaultWidth * scaleX);
        
        // Ensure default width stays within limits
        defaultWidth = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, defaultWidth));
    }

    // ======= Movement Control Methods =======
    
    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public void stopMoving() {
        this.movingLeft = false;
        this.movingRight = false;
        this.dx = 0;
    }

    /**
     * Legacy movement methods (for compatibility)
     */
    public void moveLeft() {
        this.movingLeft = true;
        this.movingRight = false;
    }

    public void moveRight() {
        this.movingRight = true;
        this.movingLeft = false;
    }

    // ======= Position and Size Setters =======
    
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setWidth(int width) {
        if (width > 0) {
            this.width = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, width));
        }
    }

    // ======= Expansion State Getters =======
    
    /**
     * Get remaining expansion time in milliseconds
     */
    public long getExpandRemainMs() {
        if (expandEndTime == 0) {
            return 0;
        }
        return Math.max(0, expandEndTime - System.currentTimeMillis());
    }

    /**
     * Get remaining expansion time in seconds (rounded up)
     */
    public int getExpandRemainingTime() {
        long remainingMillis = getExpandRemainMs();
        if (remainingMillis <= 0) {
            return 0;
        }
        return (int) Math.ceil(remainingMillis / 1000.0);
    }

    /**
     * Check if paddle is currently expanded
     */
    public boolean isExpanded() {
        return width > defaultWidth && expandEndTime > 0;
    }

    // ======= Property Getters =======
    
    public double getSpeed() {
        return speed;
    }

    public int getDefaultWidth() {
        return defaultWidth;
    }

    public Color getColor() {
        return color;
    }

    public boolean isMovingLeft() {
        return movingLeft;
    }

    public boolean isMovingRight() {
        return movingRight;
    }

    // ======= Property Setters =======
    
    public void setSpeed(double speed) {
        if (speed > 0) {
            this.speed = speed;
        }
    }

    public void setColor(Color color) {
        if (color != null) {
            this.color = color;
        }
    }

    public void setDefaultWidth(int defaultWidth) {
        if (defaultWidth > 0) {
            this.defaultWidth = Math.max(MIN_WIDTH, Math.min(MAX_WIDTH, defaultWidth));
        }
    }

    // ======= Utility Methods =======
    
    /**
     * Reset paddle to initial state
     */
    public void reset(int x, int y) {
        this.x = x;
        this.y = y;
        this.width = defaultWidth;
        this.movingLeft = false;
        this.movingRight = false;
        this.expandEndTime = 0;
        this.dx = 0;
        this.dy = 0;

        // Reset laser
        deactivateLaser();
        if (lasers != null) {
            lasers.clear();
        }
    }

    /**
     * Get expansion progress (0.0 to 1.0)
     */
    public double getExpansionProgress(long totalDuration) {
        if (!isExpanded() || totalDuration <= 0) {
            return 0.0;
        }
        long remaining = getExpandRemainMs();
        return Math.max(0.0, Math.min(1.0, remaining / (double) totalDuration));
    }

    @Override
    public String toString() {
        return String.format("Paddle[x=%.1f, y=%.1f, w=%d, h=%d, speed=%.1f, expanded=%b, laser=%b]",
                x, y, width, height, speed, isExpanded(), laserActive);
    }
}