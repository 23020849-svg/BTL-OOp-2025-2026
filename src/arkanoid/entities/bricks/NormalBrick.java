package arkanoid.entities.bricks;
/**
 * NormalBrick.java
 *
 * Gạch thường: bị phá hủy sau 1 lần va chạm.
 */

// ======= Lớp NormalBrick =======
// Kế thừa từ lớp Brick (là một loại gạch cụ thể trong game).
// Loại gạch này chỉ cần bị bóng chạm một lần là vỡ ngay.
public class NormalBrick extends Brick {

    // ======= Constructor =======
    // Nhận vào vị trí (x, y), kích thước (width, height)
    // Gọi super(...) để truyền cho lớp Brick với hitPoints = 1 (tức là máu của gạch = 1)
    public NormalBrick(int x, int y, int width, int height) {
        super(x, y, width, height, 1); // Gạch thường chỉ chịu được 1 lần đánh trúng
    }

    @Override
    public void update(double dt) {
        // Gạch không có hành vi động, không tự thay đổi theo thời gian
        // → Không cần xử lý gì trong mỗi khung hình (frame)
    }

    @Override
    public double getPowerUpDropChance() {
        return 0.15; // Gạch thường có 15% cơ hội rơi vật phẩm.
    }

   
}
