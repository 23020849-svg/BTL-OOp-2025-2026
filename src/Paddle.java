package arkanoid; // Đặt class trong package arkanoid

/**
 * Paddle.java
 *
 * Thanh đỡ do người chơi điều khiển.
 * Hỗ trợ mở rộng (power-up), giới hạn di chuyển trong biên.
 */
import java.awt.Color;
import java.awt.Graphics;

// ======= Lớp Paddle =======
// Paddle là thanh đỡ dưới màn hình, do người chơi điều khiển để đỡ bóng.
// Có thể mở rộng tạm thời khi nhận power-up.
public class Paddle extends MovableObject {

    private int speed = 8;             // Tốc độ di chuyển ngang (px mỗi lần nhấn phím)
    private int defaultWidth;          // Chiều rộng ban đầu của paddle
    private long expandEndTime = 0;    // Thời điểm kết thúc hiệu ứng mở rộng (ms, dùng System.currentTimeMillis)

    // ======= Constructor =======
    // Nhận vị trí, kích thước ban đầu, và lưu lại chiều rộng mặc định.
    public Paddle(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.defaultWidth = width;
    }

    @Override
    public void update() {
        // ======= 1. Giữ paddle trong giới hạn màn hình =======
        // Nếu paddle chạm biên trái → đưa về 0
        if (x < 0) x = 0;
        // Nếu paddle chạm biên phải → giới hạn lại để không vượt ra ngoài
        if (x + width > GameManager.WIDTH) x = GameManager.WIDTH - width;

        // ======= 2. Kiểm tra hiệu ứng mở rộng =======
        // Nếu hiệu ứng mở rộng hết hạn, khôi phục lại chiều rộng ban đầu
        if (expandEndTime > 0 && System.currentTimeMillis() > expandEndTime) {
            width = defaultWidth;
            expandEndTime = 0;
        }
    }

    @Override
    public void render(Graphics g) {
        // Vẽ paddle bằng hình chữ nhật màu xanh dương
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    // ======= Di chuyển sang trái =======
    public void moveLeft() {
        dx = -speed; // Tốc độ âm → đi sang trái
        x += dx;     // Cập nhật vị trí ngay lập tức
        dx = 0;      // Đặt lại về 0 để không tiếp tục di chuyển sau khi nhả phím
    }

    // ======= Di chuyển sang phải =======
    public void moveRight() {
        dx = speed;  // Tốc độ dương → đi sang phải
        x += dx;     // Cập nhật vị trí
        dx = 0;      // Dừng sau khi di chuyển
    }

    /**
     * Áp dụng hiệu ứng mở rộng paddle trong thời gian xác định.
     * @param extraPixels số pixel mở rộng thêm so với kích thước mặc định
     * @param durationMillis thời lượng hiệu ứng (ms)
     */
    public void applyExpand(int extraPixels, long durationMillis) {
        // Tăng chiều rộng paddle
        width = defaultWidth + extraPixels;

        // Nếu paddle vượt biên phải, điều chỉnh lại cho vừa khung
        if (x + width > GameManager.WIDTH) x = GameManager.WIDTH - width;

        // Lưu thời điểm kết thúc hiệu ứng (tính từ thời điểm hiện tại)
        expandEndTime = System.currentTimeMillis() + durationMillis;
    }

    // ======= Getter/Setter =======
    public int getSpeed() { return speed; }        // Lấy tốc độ hiện tại
    public void setSpeed(int speed) { this.speed = speed; } // Đặt tốc độ mới
}
