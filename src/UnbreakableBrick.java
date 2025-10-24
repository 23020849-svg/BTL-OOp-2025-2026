

/**
 * UnbreakableBrick.java
 * 
 * Gạch không thể phá hủy. Va chạm với bóng sẽ khiến bóng nảy lại
 * nhưng gạch không bị ảnh hưởng và không bao giờ bị phá hủy.
 */
public class UnbreakableBrick extends Brick {
    
    /**
     * Đây là hàm khởi tạo (constructor).
     * Nó được gọi mỗi khi một viên gạch không thể bị phá hủy tạo ra.
     */
    public UnbreakableBrick(int x, int y, int width, int height) {
        // Gọi đến constructor của lớp cha (Brick) với "máu" là số lớn nhất.
        super(x, y, width, height, Integer.MAX_VALUE);
    }

    /**
     * Ghi đè phương thức takeHit().
     */
    @Override
    public void takeHit() {
        // Để trống, vì gạch này không nhận sát thương.
    }

    /**
     * Ghi đè phương thức getPowerUpDropChance().
     * Gạch này không bao giờ rời ra vật phẩm.
     */
    @Override
    public double getPowerUpDropChance() {
        return 0.0; // Trả về tỉ lệ là 0.
    }

    /**
     * Phương thức update() cũng cần được định nghĩa.
     */
    @Override
    public void update() {
        // Để trống, vì gạch này không di chuyển hay thay đổi.
    }
}
