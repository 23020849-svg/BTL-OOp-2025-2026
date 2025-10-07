package arkanoid; // Đặt class trong package arkanoid

/**
 * Renderer.java
 *
 * Lớp chịu trách nhiệm vẽ tất cả các đối tượng lên panel.
 * Ở đây Renderer được gọi từ GameManager để thực hiện vẽ khung hình mỗi frame.
 */
import java.awt.Graphics;
import java.util.List;

// ======= Lớp Renderer =======
// Đóng vai trò như một “họa sĩ” tổng hợp: nhận tất cả các đối tượng (paddle, ball, bricks, power-ups)
// và vẽ chúng lên màn hình mỗi khung hình (frame).
public class Renderer {

    // Hàm draw() được gọi từ GameManager.paintComponent() để vẽ toàn bộ game
    public void draw(Graphics g, Paddle paddle, Ball ball, List<Brick> bricks, java.util.List<PowerUp> powerUps, int score, int lives) {

        // ======= 1. Xóa nền cũ trước khi vẽ frame mới =======
        g.clearRect(0, 0, GameManager.WIDTH, GameManager.HEIGHT);

        // ======= 2. Vẽ các đối tượng chính =======
        // Paddle (thanh đỡ)
        paddle.render(g);
        // Ball (quả bóng)
        ball.render(g);

        // Vẽ toàn bộ gạch trong danh sách bricks
        for (Brick b : bricks) {
            b.render(g);
        }

        // Vẽ toàn bộ power-up đang hoạt động
        for (PowerUp p : powerUps) {
            p.render(g);
        }

        // ======= 3. Vẽ thông tin HUD (Score và Lives) =======
        g.setColor(java.awt.Color.BLACK);             // Màu chữ: đen
        g.drawString("Score: " + score, 10, 15);      // Hiển thị điểm ở góc trái trên
        g.drawString("Lives: " + lives, GameManager.WIDTH - 80, 15); // Hiển thị số mạng ở góc phải trên
    }
}
