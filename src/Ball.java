package arkanoid; // Đặt class trong package arkanoid

/**
 * Ball.java
 *
 * Quả bóng di chuyển, xử lý bật tường, bật paddle, va chạm bricks.
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

// Lớp Ball kế thừa MovableObject (có sẵn các thuộc tính x, y, width, height, dx, dy)
public class Ball extends MovableObject {
    private int radius; // Bán kính của quả bóng
    private double speedMultiplier = 1.0; // Hệ số nhân tốc độ (dùng khi tăng tốc tạm thời)
    private long fastEndTime = 0; // Thời điểm kết thúc hiệu ứng tăng tốc (tính bằng mili-giây)

    // Constructor: khởi tạo vị trí, kích thước, và tốc độ ban đầu
    public Ball(int x, int y, int radius, double initialSpeedX, double initialSpeedY) {
        super(x, y, radius * 2, radius * 2); // Gọi constructor của MovableObject (width/height = đường kính)
        this.radius = radius;                // Lưu bán kính
        this.dx = initialSpeedX;             // Tốc độ theo trục X
        this.dy = initialSpeedY;             // Tốc độ theo trục Y
    }

    @Override
    public void update() {
        move(); // Gọi hàm di chuyển (cập nhật x, y dựa trên dx, dy)

        // ======= Xử lý va chạm với tường trái/phải =======
        if (x <= 0) {            // Chạm tường trái
            x = 0;                 // Giữ không vượt ra ngoài
            dx = -dx;              // Đảo hướng X
        }
        if (x + width >= GameManager.WIDTH) { // Chạm tường phải
            x = GameManager.WIDTH - width;      // Giữ lại trong màn hình
            dx = -dx;                           // Đảo hướng X
        }

        // ======= Bật trần =======
        if (y <= 0) {    // Chạm đỉnh màn hình
            y = 0;         // Giữ lại vị trí
            dy = -dy;      // Đảo hướng Y
        }

        // ======= Hết thời gian tăng tốc =======
        if (fastEndTime > 0 && System.currentTimeMillis() > fastEndTime) {
            speedMultiplier = 1.0; // Trở về tốc độ bình thường
            fastEndTime = 0;       // Reset thời gian
            // Ghi chú: dx, dy vẫn giữ nguyên hướng và tốc độ hiện tại,
            // vì code này không nhân ngược lại (tránh đổi tốc độ đột ngột).
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);              // Màu bóng: đỏ
        g.fillOval(x, y, width, height);    // Vẽ hình tròn (oval) tại vị trí (x, y)
    }

    // ======= Xử lý khi bóng va chạm với đối tượng khác (gạch/paddle) =======
    public void bounceOff(GameObject other) {
        Rectangle b = getBounds(); // Lấy khung chữ nhật của bóng
        Rectangle o = other.getBounds(); // Lấy khung chữ nhật của đối tượng va chạm

        // Tính tâm của hai hình chữ nhật để xác định hướng va chạm
        double centerX = b.getCenterX();
        double centerY = b.getCenterY();
        double otherCenterX = o.getCenterX();
        double otherCenterY = o.getCenterY();

        // Hiệu số tọa độ tâm
        double diffX = centerX - otherCenterX;
        double diffY = centerY - otherCenterY;

        // Nếu lệch nhiều theo trục X → va chạm ngang → đảo hướng X
        // Ngược lại → va chạm theo trục Y → đảo hướng Y
        if (Math.abs(diffX) > Math.abs(diffY)) {
            dx = -dx;
        } else {
            dy = -dy;
        }
    }

    // ======= Áp dụng hiệu ứng tăng tốc bóng trong thời gian ngắn =======
    public void setSpeedMultiplier(double m, long durationMillis) {
        speedMultiplier = m;                                  // Ghi hệ số nhân tốc độ
        fastEndTime = System.currentTimeMillis() + durationMillis; // Lưu thời điểm kết thúc hiệu ứng

        // Nhân dx, dy theo multiplier (giữ nguyên hướng, chỉ thay đổi độ lớn)
        dx *= m;
        dy *= m;
    }

    // ======= Chuẩn hóa vận tốc bóng (giữ độ lớn nhất định) =======
    public void normalizeSpeed(double targetMagnitude) {
        double mag = Math.sqrt(dx * dx + dy * dy); // Tính độ lớn của vector vận tốc
        if (mag == 0) return;                      // Tránh chia cho 0
        dx = dx / mag * targetMagnitude;           // Tính lại dx theo độ lớn mới
        dy = dy / mag * targetMagnitude;           // Tính lại dy theo độ lớn mới
    }

    // ======= Getter cho bán kính =======
    public int getRadius() { return radius; }
}
