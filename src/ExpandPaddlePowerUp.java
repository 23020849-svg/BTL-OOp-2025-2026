 // Đặt class trong package arkanoid


/**
 * ExpandPaddlePowerUp.java
 *
 * Power-up: mở rộng paddle trong khoảng thời gian.
 */


// ======= Lớp ExpandPaddlePowerUp kế thừa PowerUp =======
// Khi người chơi hứng được, paddle sẽ được kéo dài tạm thời.
public class ExpandPaddlePowerUp extends PowerUp {
    private int extraPixels;        // Số pixel mở rộng thêm cho paddle
    // Constructor: khởi tạo vị trí, kích thước, giá trị mở rộng và thời gian hiệu lực
    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 20, 12);          // Gọi constructor lớp cha PowerUp (tọa độ + kích thước)
        this.extraPixels = 80;        // Mở rộng thêm 80 pixel cho paddle
        this.durationMillis = 10_000;  // Hiệu ứng kéo dài tồn tại trong 10 giây (10000 ms)
    }

    // ======= Khi paddle hứng được power-up =======
    @Override
    public void applyEffect(Paddle paddle, Ball ball, GameManager game) {
        // Luôn reset thời gian về 10 giây khi nhặt PowerUp (không cộng dồn)
        paddle.applyExpand(extraPixels, durationMillis);
        deactivate(); // Sau khi kích hoạt → tắt power-up (không hoạt động nữa)
    }

  


   
}
