 // Đặt class trong package arkanoid

/**
 * GameObject.java
 *
 * Lớp trừu tượng cơ sở cho mọi đối tượng trong game.
 * Chứa vị trí và kích thước cơ bản, cùng hai phương thức update() và render().
 */
import java.awt.Rectangle;

// ======= Lớp trừu tượng GameObject =======
// Đây là lớp nền (superclass) cho mọi đối tượng trong game Arkanoid:
// ví dụ như Ball, Paddle, Brick, PowerUp, v.v.
public abstract class GameObject {
    protected int x, y;           // Tọa độ (x, y) — góc trên bên trái của đối tượng
    protected int width, height;  // Kích thước chiều rộng và chiều cao

    // ======= Constructor =======
    // Khởi tạo vị trí và kích thước cơ bản cho mọi GameObject
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Cập nhật trạng thái logic của đối tượng (được gọi mỗi frame/tick).
     * Mỗi lớp con sẽ tự định nghĩa lại cách cập nhật riêng (vd: Ball di chuyển, PowerUp rơi xuống...).
     */
    public abstract void update();

    /**
     * Vẽ đối tượng lên màn hình thông qua đối tượng Graphics.
     * Mỗi lớp con tự định nghĩa cách hiển thị (vd: Ball vẽ hình tròn, Brick vẽ hình chữ nhật).
     */

    // ======= Hỗ trợ kiểm tra va chạm =======
    public Rectangle getBounds() {
        // Trả về hình chữ nhật bao quanh đối tượng, dùng trong xử lý va chạm.
        return new Rectangle(x, y, width, height);
    }

    // ======= Getter/Setter tiện lợi =======
    public int getX() { return x; }          // Lấy tọa độ x
    public int getY() { return y; }          // Lấy tọa độ y
    public int getWidth() { return width; }  // Lấy chiều rộng
    public int getHeight() { return height; }// Lấy chiều cao

    public void setX(int x) { this.x = x; }  // Đặt lại tọa độ x
    public void setY(int y) { this.y = y; }  // Đặt lại tọa độ y
}
