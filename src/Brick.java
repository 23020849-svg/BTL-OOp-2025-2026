package arkanoid; // Đặt lớp Brick trong package arkanoid

/**
 * Brick.java
 *
 * Lớp cơ sở cho các gạch. Có hitPoints và trạng thái destroyed.
 */
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

// ======= Lớp trừu tượng Brick =======
// Kế thừa GameObject, cung cấp thuộc tính và hành vi chung cho các loại gạch (NormalBrick, StrongBrick, v.v.)
public abstract class Brick extends GameObject {
    protected int hitPoints;         // Số lần cần đánh trúng để phá hủy
    protected boolean destroyed = false; // Trạng thái gạch (đã bị phá hay chưa)

    // Constructor: khởi tạo vị trí, kích thước và số hitPoints
    public Brick(int x, int y, int width, int height, int hitPoints) {
        super(x, y, width, height); // Gọi constructor lớp cha GameObject
        this.hitPoints = hitPoints; // Gán số lần chịu đòn
    }

    /** Trừ máu khi bị đánh trúng. */
    public void takeHit() {
        hitPoints--;                 // Mỗi lần trúng bóng giảm 1 hit point
        if (hitPoints <= 0) destroyed = true; // Khi máu ≤ 0 → đánh dấu đã bị phá
    }

    // Trả về true nếu gạch đã bị phá hủy
    public boolean isDestroyed() {
        return destroyed;
    }

    // ======= Phương thức update trừu tượng =======
    // Các lớp con (NormalBrick, StrongBrick...) có thể override nếu muốn có hiệu ứng riêng
    @Override
    public abstract void update();

    // ======= Phương thức render: vẽ gạch lên màn hình =======
    @Override
    public void render(Graphics g) {
        if (destroyed) return; // Nếu đã vỡ thì không vẽ nữa
        g.setColor(getColorForHP());  // Chọn màu dựa theo số hitPoints còn lại
        g.fillRect(x, y, width, height); // Vẽ khối gạch
        g.setColor(Color.DARK_GRAY);     // Vẽ viền gạch
        g.drawRect(x, y, width, height);
    }

    // ======= Xác định màu của gạch dựa theo độ bền =======
    protected Color getColorForHP() {
        switch (Math.max(1, hitPoints)) { // Đảm bảo không bị giá trị ≤ 0
            case 1: return Color.ORANGE;   // Gạch yếu (1 HP)
            case 2: return Color.MAGENTA;  // Gạch trung bình (2 HP)
            default: return Color.GRAY;    // Gạch siêu bền (>2 HP)
        }
    }
}
