// Đặt class trong package arkanoid

/**
 * MovableObject.java
 *
 * Lớp trừu tượng cho các đối tượng có thể di chuyển (có dx/dy).
 */
// Kế thừa từ GameObject và bổ sung khả năng di chuyển.
// Dùng cho các đối tượng có vận tốc như Ball, Paddle, PowerUp, v.v.
public abstract class MovableObject extends GameObject {

    protected double dx = 0; // Tốc độ di chuyển theo trục X (px/tick)
    protected double dy = 0; // Tốc độ di chuyển theo trục Y (px/tick)

    // ======= Constructor =======
    // Gọi constructor của GameObject để khởi tạo vị trí và kích thước
    public MovableObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    /**
     * Thực hiện di chuyển dựa trên dx, dy
     * (tức là thay đổi tọa độ x, y mỗi frame theo vận tốc hiện tại).
     */
    public void move() {
        // Cộng dx, dy vào vị trí hiện tại (làm tròn sang int để phù hợp với pixel)
        x += (int) Math.round(dx);
        y += (int) Math.round(dy);
    }

    // ======= Hai phương thức trừu tượng =======
    // Bắt buộc lớp con (như Ball, Paddle) phải tự định nghĩa cách cập nhật logic và vẽ hình.
    @Override
    public abstract void update(); // Cập nhật logic (vị trí, trạng thái, va chạm, v.v.)


    // ======= Getter / Setter =======
    public double getDx() { return dx; } // Lấy tốc độ theo trục X
    public double getDy() { return dy; } // Lấy tốc độ theo trục Y
    public void setDx(double dx) { this.dx = dx; } // Đặt lại tốc độ X
    public void setDy(double dy) { this.dy = dy; } // Đặt lại tốc độ Y
}
