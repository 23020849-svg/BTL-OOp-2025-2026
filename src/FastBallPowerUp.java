 // Đặt class trong package arkanoid

/**
 * FastBallPowerUp.java
 *
 * Power-up: tăng tốc bóng trong thời gian ngắn.
 */
// Khi người chơi hứng được, bóng sẽ di chuyển nhanh hơn tạm thời.
public class FastBallPowerUp extends PowerUp {
    private double multiplier;     // Hệ số nhân tốc độ (ví dụ 1.6 = nhanh hơn 60%)
    // ======= Constructor =======
    public FastBallPowerUp(int x, int y) {
        super(x, y, 20, 12);          // Gọi constructor lớp cha PowerUp (tọa độ + kích thước)
        this.multiplier = 1.6;        // Bóng nhanh gấp 1.6 lần tốc độ gốc
        this.durationMillis = 10_000;  // Hiệu ứng kéo dài 10 giây (10000 ms)
    }

    // ======= Phương thức render: vẽ power-up lên màn hình =======
  
    // ======= Khi người chơi hứng được power-up =======
    @Override
    public void applyEffect(Paddle paddle, Ball ball, GameManager game) {
        // Luôn reset thời gian về 10 giây khi nhặt PowerUp (không cộng dồn)
        ball.setSpeedMultiplier(multiplier, durationMillis);
        deactivate(); // Sau khi kích hoạt, tắt power-up (không thể kích hoạt lại)
    }
}
