package arkanoid.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int VERSION = 2;
    public int version = VERSION;

    // ===== GAME INFO =====
    public int currentLevel;        
    public int levelIndex;          // Giữ lại để tương thích (alias của currentLevel)
    public int score;
    public int lives;
    public long levelStartTime;

    // ===== PADDLE =====
    public double paddleX;
    public double paddleY;
    public int paddleWidth;         
    public int paddleDefaultWidth;  
    public boolean isPaddleExpanded;
    public long paddleExpandRemainMs;
    public boolean isPaddleLaserActive;  
    public long paddleLaserRemainMs;     

    // ===== BALLS =====
    public List<BallState> balls;

    // ===== BRICKS =====
    public List<BrickState> bricks;

    // ===== POWER-UPS =====
    public List<PowerUpState> powerUps; 
    // ===== LAUNCH STATE =====
    public boolean ballLaunched;
    public double launchAngle;

    // ===== CUSTOMIZATION =====
    public String ballImagePath;
    public int paddleColorRGB;
    public int ballColorRGB;

    // ===== METADATA =====
    public long saveTimestamp;

    public GameState() {
        balls = new ArrayList<>();
        bricks = new ArrayList<>();
        powerUps = new ArrayList<>();
        saveTimestamp = System.currentTimeMillis();
    }

    // ===== BALL STATE =====
    public static class BallState implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public double x, y;
        public double vx, vy;
        public int radius;
        public double speedMultiplier;
        public long fastRemainMs;
        public String imagePath;
        public int colorRGB;

        public BallState() {}
        
        public BallState(double x, double y, double vx, double vy, int radius) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.radius = radius;
            this.speedMultiplier = 1.0;
            this.fastRemainMs = 0;
        }
    }

    // ===== BRICK STATE =====
    public static class BrickState implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public int x, y, width, height;
        public int hitPoints;
        public String type;
        
        public BrickState() {}
        
        public BrickState(int x, int y, int w, int h, int hp, String type) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.hitPoints = hp;
            this.type = type;
        }
    }

    // ===== POWER-UP STATE =====
    public static class PowerUpState implements Serializable {
        private static final long serialVersionUID = 1L;
        
        public double x, y;
        public String type;
        
        public PowerUpState() {}
        
        public PowerUpState(double x, double y, String type) {
            this.x = x;
            this.y = y;
            this.type = type;
        }
    }

    // ===== VALIDATION =====
    public boolean isValid() {
        return currentLevel > 0 && 
               lives > 0 && 
               balls != null && 
               bricks != null;
    }

    // ===== FORMATTED TIME =====
    public String getFormattedSaveTime() {
        long now = System.currentTimeMillis();
        long diff = now - saveTimestamp;
        
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) return days + " ngày trước";
        if (hours > 0) return hours + " giờ trước";
        if (minutes > 0) return minutes + " phút trước";
        return "Vừa xong";
    }
}