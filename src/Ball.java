import java.awt.*;

public class Ball extends MovableObject { // hoặc MoveableObject nếu lớp cha em đặt vậy
    private int speed;
    // hướng di chuyển (-1 hoặc 1)
    private int dirX;
    private int dirY;

    public Ball(int x, int y, int size, int speed) {
        super(x, y, size, size);
        this.speed = speed;
        dirX = -1;
        dirY = -1;
    }

    @Override
    public void draw(Graphics2D g) {            // dùng Graphics2D cho đồng bộ
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);        // dùng width/height thay vì 20,20
    }

    // cập nhật vị trí bóng mỗi tick
    public void update() {
        x += dirX * speed;
        y += dirY * speed;
    }

    // kiểm tra va chạm với tường: panelWidth/Height là biên phải/dưới bên trong vùng chơi (đã trừ BORDER nếu có)
    public void checkWallCollision(int panelWidth, int panelHeight) {
        // trái
        if (x <= 0) {
            x = 0;
            dirX = -dirX;
        }
        // phải
        if (x + width >= panelWidth) {
            x = panelWidth - width;
            dirX = -dirX;
        }
        // trên
        if (y <= 0) {
            y = 0;
            dirY = -dirY;
        }
        // đáy: KHÔNG bật ở đây; để GameView xử lý mất mạng/ reset
    }

    // kiểm tra va chạm với paddle
    public void checkCollision(Paddle paddle) {
        if (getBounds().intersects(paddle.getBounds())) {
            dirY = -dirY;
            y = paddle.getY() - height;  // kéo bóng ra khỏi paddle tránh kẹt
        }
    }

    // getters/setters
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }

    public int getDirectionX() { return dirX; }
    public void setDirectionX(int dirX) { this.dirX = dirX; }

    public int getDirectionY() { return dirY; }
    public void setDirectionY(int dirY) { this.dirY = dirY; }
}
