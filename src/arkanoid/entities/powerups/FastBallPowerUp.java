package arkanoid.entities.powerups; // Đặt class trong package arkanoid

import arkanoid.core.GameManager;
import arkanoid.entities.Ball;
import arkanoid.entities.Paddle;
import arkanoid.utils.Sound;

/**
 * FastBallPowerUp.java
 *
 * Power-up: tăng tốc bóng trong thời gian ngắn.
 */
// Khi người chơi hứng được, bóng sẽ di chuyển nhanh hơn tạm thời.
public class FastBallPowerUp extends PowerUp { // Hệ số nhân tốc độ (ví dụ 1.6 = nhanh hơn 60%)
    // Hệ số nhân tốc độ (ví dụ 1.6 = nhanh hơn 60%)
    // ======= Constructor =======
    public FastBallPowerUp(int x, int y) {
       super(x, y, 
              PowerUpConfig.FAST_BALL_WIDTH, 
              PowerUpConfig.FAST_BALL_HEIGHT);
        this.durationMillis = PowerUpConfig.FAST_BALL_DURATION_MS;
    }

    // ======= Phương thức render: vẽ power-up lên màn hình =======
  
    // ======= Khi người chơi hứng được power-up =======
    @Override
    public void applyEffect(Paddle paddle, Ball ball, GameManager game, int screenWidth) {
         Sound powerUpSound = new Sound();
        powerUpSound.loadSound("/powerup.wav");
        powerUpSound.playOnce();

        if (game != null && game.getBalls() != null) {
            for (Ball currentBall : game.getBalls()) {
                if (currentBall != null) {
                    currentBall.setSpeedMultiplier( PowerUpConfig.FAST_BALL_MULTIPLIER, 
                        PowerUpConfig.FAST_BALL_DURATION_MS);
                }
            }
        }

        deactivate(); // Sau khi kích hoạt, tắt power-up (không thể kích hoạt lại)
    }
}
