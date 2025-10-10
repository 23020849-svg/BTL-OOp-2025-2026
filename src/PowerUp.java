package src; // Nằm trong package arkanoid

/**
 * PowerUp.java
 *
 * Lớp cơ sở cho các Power-up rơi xuống. Khi người chơi bắt được (va chạm với Paddle),
 * applyEffect được gọi. PowerUp tự rơi theo dy.
 */
import java.awt.Color;
import java.awt.Graphics;

// ======= Lớp trừu tượng PowerUp =======
// Đại diện cho vật phẩm đặc biệt rơi từ gạch xuống.
// Khi va chạm với Paddle, PowerUp kích hoạt hiệu ứng (ví dụ: mở rộng Paddle, tăng tốc bóng).
public abstract class PowerUp extends GameObject {

    // ======= Thuộc tính cơ bản =======
    protected double dy = 2.0;      // Tốc độ rơi theo trục y (đơn vị px mỗi frame)
    protected boolean active = true; // Đang tồn tại trên màn hình (chưa bị bắt hoặc rơi khỏi màn hình)
    protected int fallSpeed = 2;     // Tốc độ rơi (đơn giản, có thể điều chỉnh nếu cần)

    // ======= Constructor =======
    // Nhận vị trí (x, y) và kích thước, truyền lên GameObject để quản lý.
    public PowerUp(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    // ======= Cập nhật vị trí theo thời gian =======
    @Override
    public void update() {
        // Mỗi frame, power-up rơi xuống theo trục y
        y += fallSpeed;

        // Nếu rơi quá chiều cao màn hình (GameManager.HEIGHT), thì hủy (ngừng hiển thị)
        if (y > GameManager.HEIGHT) active = false;
    }

    // ======= Vẽ power-up lên màn hình =======
    // Mỗi loại power-up có màu sắc và hình dạng khác nhau, nên render là abstract.
    @Override
    public abstract void render(Graphics g);

    /**
     * Gọi khi paddle bắt được power-up.
     * Mỗi power-up sẽ cài đặt hiệu ứng riêng khi được kích hoạt.
     * @param paddle paddle của người chơi
     * @param ball bóng trong game (vì có loại ảnh hưởng đến tốc độ bóng)
     * @param game đối tượng GameManager để tương tác với toàn bộ game
     */
    public abstract void applyEffect(Paddle paddle, Ball ball, GameManager game);

    // ======= Trạng thái hoạt động =======
    public boolean isActive() { return active; }   // Kiểm tra còn tồn tại không
    public void deactivate() { active = false; }   // Đánh dấu đã bị bắt hoặc hủy
}
