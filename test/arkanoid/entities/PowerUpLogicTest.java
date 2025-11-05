package arkanoid.entities;

import arkanoid.core.GameManager;
import arkanoid.entities.powerups.ExpandPaddlePowerUp;
import arkanoid.entities.powerups.FastBallPowerUp;
import arkanoid.entities.powerups.MultiBallPowerUp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm tra Logic của các Power-up")
public class PowerUpLogicTest {

    private Paddle paddle;
    private Ball ball;
    private GameManager mockGameManager; // Chúng ta sẽ tạo một GameManager "giả"
    private final int screenWidth = 800;
    
    // Một class "giả" (mock) đơn giản của GameManager để test MultiBall
    private class MockGameManager extends GameManager {
        public int activateMultiBallCallCount = 0;
        
        public MockGameManager() {
            super();
            // Thêm bóng vào danh sách để tránh NullPointerException
            getBalls().add(ball);
        }

        @Override
        public void activateMultiBall() {
            this.activateMultiBallCallCount++;
        }
    }

    @BeforeEach
    void setUp() {
        paddle = new Paddle(100, 100, 120, 20, Color.BLUE);
        ball = new Ball(50, 50, 8, 300, 300, Color.RED);
        mockGameManager = new MockGameManager();
    }

    @Test
    @DisplayName("ExpandPaddlePowerUp làm tăng chiều rộng Paddle")
    public void testExpandPaddleEffect() {
        ExpandPaddlePowerUp powerUp = new ExpandPaddlePowerUp(0, 0);
        int initialWidth = paddle.getWidth();
        
        powerUp.applyEffect(paddle, ball, mockGameManager, screenWidth);
        
        assertTrue(paddle.getWidth() > initialWidth, "Chiều rộng Paddle phải tăng lên");
        assertTrue(paddle.isExpanded(), "Paddle phải ở trạng thái isExpanded");
        assertFalse(powerUp.isActive(), "PowerUp phải bị hủy (deactivate) sau khi áp dụng");
    }

    @Test
    @DisplayName("FastBallPowerUp tăng tốc độ Ball")
    public void testFastBallEffect() {
        FastBallPowerUp powerUp = new FastBallPowerUp(0, 0);
        double initialSpeedMultiplier = ball.getSpeedMultiplier();
        
        powerUp.applyEffect(paddle, ball, mockGameManager, screenWidth);
        
        assertTrue(ball.getSpeedMultiplier() > initialSpeedMultiplier, "Hệ số tốc độ của Ball phải tăng");
        assertTrue(ball.getFastEndTime() > System.currentTimeMillis(), "Thời gian kết thúc hiệu ứng phải được đặt");
        assertFalse(powerUp.isActive(), "PowerUp phải bị hủy (deactivate) sau khi áp dụng");
    }

    @Test
    @DisplayName("MultiBallPowerUp gọi hàm activateMultiBall trong GameManager")
    public void testMultiBallEffect() {
        MultiBallPowerUp powerUp = new MultiBallPowerUp(0, 0);
        
        // Sử dụng MockGameManager đã tạo
        MockGameManager mockManager = (MockGameManager) mockGameManager;
        
        int initialCallCount = mockManager.activateMultiBallCallCount;
        
        powerUp.applyEffect(paddle, ball, mockManager, screenWidth);
        
        assertEquals(initialCallCount + 1, mockManager.activateMultiBallCallCount, "Hàm activateMultiBall phải được gọi 1 lần");
        assertFalse(powerUp.isActive(), "PowerUp phải bị hủy (deactivate) sau khi áp dụng");
    }
}