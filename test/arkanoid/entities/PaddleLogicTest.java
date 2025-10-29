package arkanoid.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm tra Logic của Paddle")
public class PaddleLogicTest {

    private Paddle paddle;
    private final int screenWidth = 800;
    private final int defaultWidth = 120;
    private final double speed = 420.0;
    private final double deltaTime = 0.1; // Giả lập 0.1 giây

    @BeforeEach
    // Thiết lập một Paddle mới trước mỗi bài test
    void setUp() {
        paddle = new Paddle(100, 100, defaultWidth, 20);
    }

    @Test
    @DisplayName("Paddle di chuyển sang trái khi ấn phím")
    public void testPaddleMoveLeft() {
        double initialX = paddle.getX();
        paddle.setMovingLeft(true);
        paddle.update(deltaTime, screenWidth);
        
        double expectedX = initialX - speed * deltaTime;
        assertEquals(expectedX, paddle.getX(), "Paddle phải di chuyển sang trái");
    }

    @Test
    @DisplayName("Paddle di chuyển sang phải khi ấn phím")
    public void testPaddleMoveRight() {
        double initialX = paddle.getX();
        paddle.setMovingRight(true);
        paddle.update(deltaTime, screenWidth);
        
        double expectedX = initialX + speed * deltaTime;
        assertEquals(expectedX, paddle.getX(), "Paddle phải di chuyển sang phải");
    }

    @Test
    @DisplayName("Paddle dừng lại ở biên trái màn hình")
    public void testPaddleStopsAtLeftBoundary() {
        paddle.setX(0); // Đặt paddle ở biên trái
        paddle.setMovingLeft(true);
        paddle.update(deltaTime, screenWidth);
        
        // Paddle không được di chuyển ra ngoài biên (x < 0)
        assertEquals(0, paddle.getX(), "Paddle phải dừng ở biên trái (x=0)");
    }

    @Test
    @DisplayName("Paddle dừng lại ở biên phải màn hình")
    public void testPaddleStopsAtRightBoundary() {
        double rightEdge = screenWidth - defaultWidth;
        paddle.setX(rightEdge); // Đặt paddle ở biên phải
        
        paddle.setMovingRight(true);
        paddle.update(deltaTime, screenWidth);
        
        // Paddle không được di chuyển ra ngoài biên (x + width > screenWidth)
        assertEquals(rightEdge, paddle.getX(), "Paddle phải dừng ở biên phải");
    }

    @Test
    @DisplayName("Power-up ExpandPaddle tăng chiều rộng của Paddle")
    public void testPaddleApplyExpandPowerUp() {
        int extraPixels = 80;
        paddle.applyExpand(extraPixels, 10000, screenWidth);
        
        int expectedWidth = defaultWidth + extraPixels;
        assertEquals(expectedWidth, paddle.getWidth(), "Chiều rộng Paddle phải tăng lên");
        assertTrue(paddle.isExpanded(), "Paddle phải ở trạng thái isExpanded");
    }

    @Test
    @DisplayName("Paddle trở lại kích thước cũ sau khi hết hiệu ứng")
    public void testPaddleExpandResetsAfterTime() throws InterruptedException {
        paddle.applyExpand(80, 20, screenWidth); // Áp dụng hiệu ứng trong 20 mili-giây
        
        assertTrue(paddle.isExpanded(), "Paddle phải được mở rộng ngay lập tức");
        
        // Chờ 30 mili-giây để đảm bảo hiệu ứng hết hạn
        Thread.sleep(30); 
        
        // Gọi update() để logic kiểm tra hết hạn được kích hoạt
        paddle.update(0.01, screenWidth);
        
        assertFalse(paddle.isExpanded(), "Paddle phải trở lại trạng thái bình thường");
        assertEquals(defaultWidth, paddle.getWidth(), "Chiều rộng Paddle phải trở về mặc định");
    }
}