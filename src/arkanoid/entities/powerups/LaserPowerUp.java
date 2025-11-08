package arkanoid.entities.powerups;

import arkanoid.core.GameManager;
import arkanoid.entities.Ball;
import arkanoid.entities.Paddle;
import arkanoid.utils.Sound;

/**
 * LaserPowerUp - Power-up cho phép paddle bắn laser
 * Khi thu thập, paddle sẽ tự động bắn laser từ 2 góc để phá gạch
 */
public class LaserPowerUp extends PowerUp {
    

    /**
     * Constructor
     * @param x vị trí x ban đầu
     * @param y vị trí y ban đầu
     */
    public LaserPowerUp(int x, int y) {
        super(x, y, 
              PowerUpConfig.LASER_WIDTH, 
              PowerUpConfig.LASER_HEIGHT);
        this.durationMillis = PowerUpConfig.LASER_DURATION_MS;
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

        Sound powerUpSound = new Sound();
         powerUpSound.loadSound("/powerup.wav");
        powerUpSound.playOnce();

        // Kích hoạt chế độ laser cho paddle
        paddle.activateLaser(PowerUpConfig.LASER_DURATION_MS, 
            PowerUpConfig.LASER_FIRE_RATE_MS);

       System.out.println("Laser Power-Up activated! Duration: " + 
                          (PowerUpConfig.LASER_DURATION_MS / 1000) + "s, Fire rate: " + 
                          PowerUpConfig.LASER_FIRE_RATE_MS + "ms");
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
        return PowerUpConfig.LASER_DURATION_MS;
    }

    /**
     * Lấy tốc độ bắn của laser
     */
    public long getFireRate() {
        return PowerUpConfig.LASER_FIRE_RATE_MS ;
    }

    @Override
    public String toString() {
        return String.format("LaserPowerUp[x=%.1f, y=%.1f, active=%b, duration=%dms]",
                getX(), getY(), isActive(), PowerUpConfig.LASER_DURATION_MS);
    }

    
}