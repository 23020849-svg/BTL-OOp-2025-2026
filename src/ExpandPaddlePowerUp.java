package src; // Đặt class trong package arkanoid


/**
 * ExpandPaddlePowerUp.java
 *
 * Power-up: mở rộng paddle trong khoảng thời gian.
 */


// ======= Lớp ExpandPaddlePowerUp kế thừa PowerUp =======
// Khi người chơi hứng được, paddle sẽ được kéo dài tạm thời.
public class ExpandPaddlePowerUp extends PowerUp {
    private int extraPixels;        // Số pixel mở rộng thêm cho paddle
    private long durationMillis;    // Thời gian hiệu lực (tính bằng mili-giây)

    // Constructor: khởi tạo vị trí, kích thước, giá trị mở rộng và thời gian hiệu lực
    public ExpandPaddlePowerUp(int x, int y) {
        super(x, y, 20, 12);          // Gọi constructor lớp cha PowerUp (tọa độ + kích thước)
        this.extraPixels = 80;        // Mở rộng thêm 80 pixel cho paddle
        this.durationMillis = 8_000;  // Hiệu ứng kéo dài tồn tại trong 8 giây (8000 ms)
    }

    // ======= Khi paddle hứng được power-up =======
    @Override
    public void applyEffect(Paddle paddle, Ball ball, GameManager game) {
        paddle.applyExpand(extraPixels, durationMillis); // Gọi hiệu ứng mở rộng paddle trong thời gian giới hạn
        deactivate(); // Sau khi kích hoạt → tắt power-up (không hoạt động nữa)
    }

  


   
}
