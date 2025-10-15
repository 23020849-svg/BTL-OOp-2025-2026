package src; // Nằm trong package arkanoid

/**
 * PowerUp.java
 *
 * Lớp cơ sở cho các Power-up rơi xuống. Khi người chơi bắt được (va chạm với Paddle),
 * applyEffect được gọi. PowerUp tự rơi theo dy.
 */
// Đại diện cho vật phẩm đặc biệt rơi từ gạch xuống.
// Khi va chạm với Paddle, PowerUp kích hoạt hiệu ứng (ví dụ: mở rộng Paddle, tăng tốc bóng).
public abstract class PowerUp extends GameObject {

    // ======= Thuộc tính cơ bản =======
    protected double dy = 2.0;      // Tốc độ rơi theo trục y (đơn vị px mỗi frame)
    protected boolean active = true; // Đang tồn tại trên màn hình (chưa bị bắt hoặc rơi khỏi màn hình)
    protected int fallSpeed = 2;     // Tốc độ rơi (đơn giản, có thể điều chỉnh nếu cần)
    protected long startTime;        // Thời điểm bắt đầu hiệu ứng (mili-giây)
    protected long durationMillis;   // Thời gian hiệu ứng kéo dài (mili-giây)
    // 🟢 Thêm: cờ cho biết PowerUp đã được kích hoạt (đang chạy hiệu ứng)
    protected boolean activated = false;

    // ======= Constructor =======
    public PowerUp(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    // 🟢 Ghi lại thời điểm bắt đầu hiệu ứng
    public void start() {
        startTime = System.currentTimeMillis();
        activated = true;
    }

    // 🟢 Kiểm tra còn thời gian hoạt động không
    public boolean isActivated() {
    if (activated && getRemainingTime() <= 0) {
        activated = false;
    }
    return activated;
    }

    // 🟢 Lấy thời gian còn lại (tính bằng giây)
    public int getRemainingTime() {
        if (startTime == 0) return 0;
        int remaining = (int) (durationMillis - (System.currentTimeMillis() - startTime));
        return Math.max(remaining / 1000, 0); // tính bằng giây
    }

    // ======= Cập nhật vị trí theo thời gian =======
    @Override
    public void update() {
        // Mỗi frame, power-up rơi xuống theo trục y
        y += fallSpeed;

        // Nếu rơi quá chiều cao màn hình (GameManager.HEIGHT), thì hủy (ngừng hiển thị)
        if (y > GameManager.HEIGHT) active = false;
    }

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
