 // Đặt lớp Brick trong package arkanoid

/**
 * Brick.java
 *
 * Lớp cơ sở cho các gạch. Có hitPoints và trạng thái destroyed.
 */
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
        if (destroyed) return;
        hitPoints--;                 // Mỗi lần trúng bóng giảm 1 hit point
        if (hitPoints <= 0) destroyed = true; // Khi máu ≤ 0 → đánh dấu đã bị phá
    }

    // Trả về true nếu gạch đã bị phá hủy
    public boolean isDestroyed() {
        return destroyed;
    }

    public int getHitPoints()     { return Math.max(0, hitPoints); }

    // ======= Phương thức update trừu tượng =======
    // Các lớp con (NormalBrick, StrongBrick...) có thể override nếu muốn có hiệu ứng riêng
    @Override
    public abstract void update();

      /** Cho View dùng để lấy khung va chạm/vẽ. */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Trả về tỉ lệ rơi power-up của loại gạch này.
     * Mỗi lớp con (NormalBrick, StrongBrick) sẽ định nghĩa tỉ lệ riêng.
     */
    public abstract double getPowerUpDropChance();
}
