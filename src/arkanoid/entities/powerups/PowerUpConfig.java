package arkanoid.entities.powerups;

/**
 * PowerUpConfig - Cấu hình tập trung cho tất cả power-ups
 * Áp dụng Single Responsibility Principle
 */
public class PowerUpConfig {
    
    // ========== EXPAND PADDLE CONFIG ==========
    public static final int EXPAND_PADDLE_WIDTH = 100;
    public static final int EXPAND_PADDLE_HEIGHT = 60;
    public static final int EXPAND_EXTRA_PIXELS = 80;
    public static final long EXPAND_DURATION_MS = 10_000; // 10 giây
    
    // ========== FAST BALL CONFIG ==========
    public static final int FAST_BALL_WIDTH = 100;
    public static final int FAST_BALL_HEIGHT = 60;
    public static final double FAST_BALL_MULTIPLIER = 1.6;
    public static final long FAST_BALL_DURATION_MS = 10_000; // 10 giây
    
    // ========== MULTI BALL CONFIG ==========
    public static final int MULTI_BALL_WIDTH = 100;
    public static final int MULTI_BALL_HEIGHT = 60;
    public static final int MULTI_BALL_COUNT = 2; // Số bóng thêm
    public static final double MULTI_BALL_ANGLE_OFFSET = 30.0; // Độ
    
    // ========== LASER CONFIG ==========
    public static final int LASER_WIDTH = 100;
    public static final int LASER_HEIGHT = 60;
    public static final long LASER_DURATION_MS = 10_000; // 10 giây
    public static final long LASER_FIRE_RATE_MS = 500; // 0.5 giây
    
    // ========== COMMON CONFIG ==========
    public static final double POWER_UP_FALL_SPEED = 120.0; // Tốc độ rơi
    public static final String POWER_UP_SOUND_PATH = "/powerup.wav";
    
    // ========== DROP RATES ==========
    public static final double EXPAND_PADDLE_DROP_WEIGHT = 0.30; // 30%
    public static final double FAST_BALL_DROP_WEIGHT = 0.25;     // 25%
    public static final double MULTI_BALL_DROP_WEIGHT = 0.20;    // 20%
    public static final double LASER_DROP_WEIGHT = 0.25;         // 25%
    
    // ========== RESOURCE PATHS ==========
    public static final String EXPAND_PADDLE_GIF = "/extra.gif";
    public static final String FAST_BALL_GIF = "/fast.gif";
    public static final String MULTI_BALL_GIF = "/extraball.gif";
    public static final String LASER_GIF = "/laser.gif";
    
    // Private constructor - utility class
    private PowerUpConfig() {
        throw new AssertionError("Cannot instantiate PowerUpConfig");
    }
    
    /**
     * Kiểm tra cấu hình có hợp lệ không
     */
    public static boolean validateConfig() {
        // Kiểm tra tổng tỷ lệ drop rate
        double totalDropRate = EXPAND_PADDLE_DROP_WEIGHT + 
                               FAST_BALL_DROP_WEIGHT + 
                               MULTI_BALL_DROP_WEIGHT + 
                               LASER_DROP_WEIGHT;
        
        if (Math.abs(totalDropRate - 1.0) > 0.001) {
            System.err.println("Warning: Total drop rate is not 1.0: " + totalDropRate);
            return false;
        }
        
        return true;
    }
    
   
    public static void printConfig() {
        System.out.println("========== POWER-UP CONFIGURATION ==========");
        System.out.println("Expand Paddle: " + EXPAND_EXTRA_PIXELS + "px, " + 
                          EXPAND_DURATION_MS + "ms");
        System.out.println("Fast Ball: x" + FAST_BALL_MULTIPLIER + ", " + 
                          FAST_BALL_DURATION_MS + "ms");
        System.out.println("Multi Ball: +" + MULTI_BALL_COUNT + " balls");
        System.out.println("Laser: " + LASER_DURATION_MS + "ms, fire rate " + 
                          LASER_FIRE_RATE_MS + "ms");
        System.out.println("Drop Rates: Expand=" + (EXPAND_PADDLE_DROP_WEIGHT*100) + 
                          "%, Fast=" + (FAST_BALL_DROP_WEIGHT*100) + 
                          "%, Multi=" + (MULTI_BALL_DROP_WEIGHT*100) + 
                          "%, Laser=" + (LASER_DROP_WEIGHT*100) + "%");
        System.out.println("==========================================");
    }
}