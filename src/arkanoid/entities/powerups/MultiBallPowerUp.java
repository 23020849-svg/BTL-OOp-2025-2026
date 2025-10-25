package arkanoid.entities.powerups;

import arkanoid.entities.Paddle;
import arkanoid.entities.Ball;
import arkanoid.core.GameManager;

/**
 * MultiBallPowerUp.java
 *
 * Power-up: Tách bóng hiện tại thành nhiều bóng.
 */
public class MultiBallPowerUp extends PowerUp {

    public MultiBallPowerUp(int x, int y) {
        // Gọi constructor lớp cha PowerUp (tọa độ + kích thước)
        super(x, y, 20, 12);
        // Vật phẩm này không có thời gian hiệu lực, nó chỉ kích hoạt một lần
        this.durationMillis = 0;
    }

    @Override
    public void applyEffect(Paddle paddle, Ball ball, GameManager game) {
        // Gọi phương thức trong GameManager để tạo thêm bóng
        game.activateMultiBall();
        deactivate(); // Tắt vật phẩm ngay sau khi kích hoạt
    }
}