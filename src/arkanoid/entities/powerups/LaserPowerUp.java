package arkanoid.entities.powerups;

import arkanoid.core.GameManager;
import arkanoid.entities.Ball;
import arkanoid.entities.Paddle;

/**
 * LaserPowerUp - Power-up cho phép paddle bắn laser
 * Khi thu thập, paddle sẽ tự động bắn laser từ 2 góc để phá gạch
 */
public class LaserPowerUp extends PowerUp {
    
    private static final long LASER_DURATION = 10000; // 10 giây
    private static final long LASER_FIRE_RATE = 500;  // Bắn mỗi 0.5 giây

    /**
     * Constructor
     * @param x vị trí x ban đầu
     * @param y vị trí y ban đầu
     */
    public LaserPowerUp(int x, int y) {
        super(x, y, 24,24);
        this.durationMillis = LASER_DURATION;
    }

    /**
     * Áp dụng hiệu ứng laser cho paddle
     * @param paddle Paddle nhận power-up
     * @param ball Ball (không sử dụng cho laser)
     * @param gameManager Game manager
     * @param screenWidth Chiều rộng màn hình (không sử dụng cho laser)
     */
    @Override 
    public void applyEffect(Paddle paddle, Ball ball, GameManager gameManager, int screenWidth) {
        if (paddle == null || gameManager == null) {
            return;
        }

        // Kích hoạt chế độ laser cho paddle
        paddle.activateLaser(LASER_DURATION, LASER_FIRE_RATE);

        System.out.println("Laser Power-Up activated! Duration: " + (LASER_DURATION / 1000) + "s, Fire rate: " + LASER_FIRE_RATE + "ms");
    }

    /**
     * Lấy tên của power-up
     */

    
    public String getName() {
        return "Laser";
    }

    /**
     * Lấy thời lượng của power-up
     */
    public long getDuration() {
        return LASER_DURATION;
    }

    /**
     * Lấy tốc độ bắn của laser
     */
    public long getFireRate() {
        return LASER_FIRE_RATE;
    }

    @Override
    public String toString() {
        return String.format("LaserPowerUp[x=%.1f, y=%.1f, active=%b, duration=%dms]",
                getX(), getY(), isActive(), LASER_DURATION);
    }
}