package arkanoid.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import arkanoid.entities.bricks.NormalBrick;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm tra Logic của Ball")
public class BallLogicTest {

    private Ball ball;
    private final int screenWidth = 800;
    private final int screenHeight = 600;
    private final double deltaTime = 0.1; // Giả lập 0.1 giây
    
    // Sử dụng tốc độ 360.0, giống như baseSpeed trong game của bạn
    private final double GAME_SPEED = 360.0;

    @BeforeEach
    void setUp() {
        // Tạo bóng với hướng (-1, -1)
        // Constructor của Ball sẽ tự động chuẩn hóa tốc độ về GAME_SPEED (360)
        ball = new Ball(screenWidth / 2, screenHeight / 2, 8, -1, -1, Color.RED);
    }

    @Test
    @DisplayName("Bóng di chuyển đúng theo vận tốc sau 1 frame")
    public void testBallMovement() {
        // Lấy dx, dy sau khi constructor đã chuẩn hóa về 360
        // (sẽ là ~ -254.55 và -254.55)
        double initialX = ball.getX();
        double initialY = ball.getY();
        double dx = ball.getDx(); 
        double dy = ball.getDy();

        ball.update(deltaTime, screenWidth, screenHeight);

        double expectedX = initialX + dx * deltaTime;
        double expectedY = initialY + dy * deltaTime;

        assertEquals(expectedX, ball.getX(), 0.01, "Bóng phải di chuyển đúng trục X");
        assertEquals(expectedY, ball.getY(), 0.01, "Bóng phải di chuyển đúng trục Y");
    }

    @Test
    @DisplayName("Bóng nảy lại khi chạm tường trái")
    public void testBallBounceLeftWall() {
        ball.setX(0); // Đặt bóng ở tường trái
        ball.setDx(-GAME_SPEED); // Đặt vận tốc hướng sang trái
        double initialDx = ball.getDx();

        ball.update(deltaTime, screenWidth, screenHeight);

        assertTrue(ball.getDx() > 0, "Vận tốc X phải đổi chiều (dương)");
        assertEquals(-initialDx, ball.getDx(), 0.01, "Vận tốc X phải bằng giá trị đối");
    }

    @Test
    @DisplayName("Bóng nảy lại khi chạm tường phải")
    public void testBallBounceRightWall() {
        ball.setX(screenWidth - ball.getWidth()); // Đặt bóng ở tường phải
        ball.setDx(GAME_SPEED); // Đặt vận tốc hướng sang phải
        double initialDx = ball.getDx();

        ball.update(deltaTime, screenWidth, screenHeight);

        assertTrue(ball.getDx() < 0, "Vận tốc X phải đổi chiều (âm)");
        assertEquals(-initialDx, ball.getDx(), 0.01, "Vận tốc X phải bằng giá trị đối");
    }

    @Test
    @DisplayName("Bóng nảy lại khi chạm tường trên")
    public void testBallBounceTopWall() {
        ball.setY(0); // Đặt bóng ở tường trên
        ball.setDy(-GAME_SPEED); // Đặt vận tốc hướng lên trên
        double initialDy = ball.getDy();

        ball.update(deltaTime, screenWidth, screenHeight);

        assertTrue(ball.getDy() > 0, "Vận tốc Y phải đổi chiều (dương)");
        assertEquals(-initialDy, ball.getDy(), 0.01, "Vận tốc Y phải bằng giá trị đối");
    }

    @Test
    @DisplayName("Hiệu ứng FastBall tăng tốc độ bóng")
    public void testBallSpeedMultiplier() throws InterruptedException {
        double initialSpeed = ball.getSpeed(); // Tốc độ là 360
        double multiplier = 1.6;
        
        // SỬA LỖI: Đặt duration lớn hơn 1000ms để getFastRemainingTime() trả về 1
        long duration = 1500; // 1.5 giây

        ball.setSpeedMultiplier(multiplier, duration);
        ball.update(0.01, screenWidth, screenHeight); // Cập nhật để chuẩn hóa tốc độ

        double expectedSpeed = initialSpeed * multiplier; // 360 * 1.6 = 576
        
        // Kiểm tra tốc độ ngay lập tức
        assertEquals(expectedSpeed, ball.getSpeed(), 0.01, "Tốc độ bóng phải tăng ngay lập tức");
        assertEquals(1, ball.getFastRemainingTime(), "Thời gian còn lại phải là 1 (giây)");

        // Chờ cho hiệu ứng hết hạn
        Thread.sleep(duration + 20);
        ball.update(0.01, screenWidth, screenHeight); // Cập nhật để kiểm tra hết hạn

        // Kiểm tra tốc độ đã reset
        assertEquals(initialSpeed, ball.getSpeed(), 0.01, "Tốc độ bóng phải trở về ban đầu");
        assertEquals(0, ball.getFastRemainingTime(), "Thời gian còn lại phải là 0");
    }
    
    @Test
    @DisplayName("Bóng nảy lại (đổi chiều Y) khi va chạm GameObject")
    public void testBallBounceOffObject() {
        // Đặt bóng di chuyển thẳng xuống (dx=0, dy=360)
        ball.setDx(0);
        ball.setDy(GAME_SPEED);
        ball.normalizeSpeed(GAME_SPEED); // Đảm bảo dx=0, dy=360
        
        double initialDy = ball.getDy(); // initialDy = 360.0
        
        // SỬA LỖI: Tạo gạch CHỒNG LÊN 2 pixel để đảm bảo có va chạm
        GameObject brick = new NormalBrick((int)ball.getX(), (int)ball.getY() + ball.getHeight() - 2, 50, 20);
        
        ball.bounceOff(brick); 
        
        // Hàm bounceOff sẽ chạy:
        // 1. if (r.width > r.height) -> TRUE
        // 2. dy = -dy; (dy = -360)
        // 3. normalizeSpeed(360) (dy vẫn là -360)
        
        // Test mong đợi giá trị BỊ ĐẢO NGƯỢC (-360)
        assertEquals(-initialDy, ball.getDy(), 0.01, "Vận tốc Y của bóng phải đổi chiều sau va chạm");
    }
}