package arkanoid; // Nằm trong package arkanoid

/**
 * StrongBrick.java
 *
 * Gạch cứng: cần 2 lần hoặc nhiều lần va chạm tuỳ cấu hình.
 * Kế thừa từ lớp Brick, sử dụng hitPoints để xác định số lần va chạm cần thiết
 * trước khi bị phá hủy hoàn toàn.
 */
public class StrongBrick extends Brick {

    /**
     * Constructor khởi tạo gạch cứng.
     *
     * @param x          vị trí góc trái trên theo trục X
     * @param y          vị trí góc trái trên theo trục Y
     * @param width      chiều rộng viên gạch
     * @param height     chiều cao viên gạch
     * @param hitPoints  số lần cần va chạm để phá hủy (≥ 2)
     */
    public StrongBrick(int x, int y, int width, int height, int hitPoints) {
        super(x, y, width, height, hitPoints);
    }

    @Override
    public void update() {
        // Gạch không di chuyển hoặc thay đổi theo thời gian,
        // nên không cần logic cập nhật mỗi khung hình.
    }

    @Override
    public double getPowerUpDropChance() {
        // Gạch càng nhiều máu tỉ lệ rơi càng cao.
        // Ví dụ: gạch 3 máu trở lên có tỉ lệ 60%, gạch 2 máu có tỉ lệ 40%.
        if (this.hitPoints >= 3) {
            return 0.60; // 60%
        }
        return 0.40; // 40% (cho gạch 2 máu)
    }
}
