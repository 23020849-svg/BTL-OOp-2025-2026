package arkanoid.entities.powerups; // Đặt class trong package arkanoid.entities.powerups

import arkanoid.core.GameManager;
import arkanoid.entities.Ball;
import arkanoid.entities.Paddle;
import arkanoid.utils.Sound;
/**
 * ExpandPaddlePowerUp.java
 *
 * Power-up: mở rộng paddle trong khoảng thời gian.
 */


// ======= Lớp ExpandPaddlePowerUp kế thừa PowerUp =======
// Khi người chơi hứng được, paddle sẽ được kéo dài tạm thời.
public class ExpandPaddlePowerUp extends PowerUp {
    
    // Constructor: khởi tạo vị trí, kích thước, giá trị mở rộng và thời gian hiệu lực
    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 
              PowerUpConfig.EXPAND_PADDLE_WIDTH, 
              PowerUpConfig.EXPAND_PADDLE_HEIGHT);
        this.durationMillis = PowerUpConfig.EXPAND_DURATION_MS;
    }

    // ======= Khi paddle hứng được power-up =======
    @Override
    public void applyEffect(Paddle paddle, Ball ball, GameManager game, int screenWidth) {
        Sound powerUpSound = new Sound();
        powerUpSound.loadSound("/powerup.wav");
        powerUpSound.playOnce();
        // Luôn reset thời gian về 10 giây khi nhặt PowerUp (không cộng dồn)
        paddle.applyExpand(PowerUpConfig.EXPAND_EXTRA_PIXELS, 
            PowerUpConfig.EXPAND_DURATION_MS, screenWidth);
        deactivate(); // Sau khi kích hoạt → tắt power-up (không hoạt động nữa)
    }

  


   
}
