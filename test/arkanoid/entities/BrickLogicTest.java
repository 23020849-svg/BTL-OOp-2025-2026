package arkanoid.entities;

// Import các lớp cần test
import arkanoid.entities.bricks.Brick;
import arkanoid.entities.bricks.NormalBrick;
import arkanoid.entities.bricks.StrongBrick;
import arkanoid.entities.bricks.UnbreakableBrick;

// Import thư viện JUnit 5
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm tra Logic của các loại Gạch")
public class BrickLogicTest {

    @Test
    @DisplayName("Gạch thường (1 HP) bị phá hủy sau 1 lần va chạm")
    public void testNormalBrickTakeHit() {
        // --- Sắp đặt (Arrange) ---
        // Tạo một viên gạch thường (có 1 HP)
        Brick brick = new NormalBrick(0, 0, 10, 10);

        // --- Hành động (Act) ---
        // Gạch nhận 1 lần va chạm
        brick.takeHit();

        // --- Kiểm chứng (Assert) ---
        // Gạch phải bị phá hủy
        assertTrue(brick.isDestroyed(), "NormalBrick phải bị phá hủy sau 1 lần va chạm");
        // HP của gạch phải bằng 0
        assertEquals(0, brick.getHitPoints(), "HP của NormalBrick phải bằng 0");
    }

    @Test
    @DisplayName("Gạch cứng (3 HP) bị phá hủy sau 3 lần va chạm")
    public void testStrongBrickTakeHit() {
        // --- Sắp đặt (Arrange) ---
        // Tạo một viên gạch cứng với 3 HP
        Brick brick = new StrongBrick(0, 0, 10, 10, 3);

        // --- Hành động 1 (Act 1) ---
        brick.takeHit(); // Va chạm lần 1

        // --- Kiểm chứng 1 (Assert 1) ---
        // Gạch không bị phá hủy
        assertFalse(brick.isDestroyed(), "StrongBrick không nên bị phá hủy sau 1 lần va chạm");
        // HP của gạch phải giảm còn 2
        assertEquals(2, brick.getHitPoints(), "HP của StrongBrick phải là 2");

        // --- Hành động 2 (Act 2) ---
        brick.takeHit(); // Va chạm lần 2
        brick.takeHit(); // Va chạm lần 3

        // --- Kiểm chứng 2 (Assert 2) ---
        // Gạch phải bị phá hủy sau 3 lần va chạm
        assertTrue(brick.isDestroyed(), "StrongBrick phải bị phá hủy sau 3 lần va chạm");
        assertEquals(0, brick.getHitPoints(), "HP của StrongBrick phải bằng 0 sau 3 lần va chạm");
    }

    @Test
    @DisplayName("Gạch không thể phá hủy không bao giờ bị phá hủy")
    public void testUnbreakableBrickTakeHit() {
        // --- Sắp đặt (Arrange) ---
        // Tạo một viên gạch không thể bị phá hủy
        Brick brick = new UnbreakableBrick(0, 0, 10, 10);
        int initialHitPoints = brick.getHitPoints(); // Lấy HP ban đầu (Integer.MAX_VALUE)

        // --- Hành động (Act) ---
        // Ghi đè phương thức takeHit() để không làm gì
        brick.takeHit();
        brick.takeHit();
        brick.takeHit();

        // --- Kiểm chứng (Assert) ---
        // Gạch không bao giờ bị phá hủy
        assertFalse(brick.isDestroyed(), "UnbreakableBrick không bao giờ bị phá hủy");
        // HP của gạch phải giữ nguyên
        assertEquals(initialHitPoints, brick.getHitPoints(), "HP của UnbreakableBrick phải giữ nguyên");
        assertEquals(Integer.MAX_VALUE, brick.getHitPoints(), "HP của UnbreakableBrick phải là MAX_VALUE");
    }
}