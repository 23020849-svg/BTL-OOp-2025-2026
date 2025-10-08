import java.awt.Color;
import java.awt.Graphics;
public class Ball extends MovableObject {
    private double speed;

    public Ball(int x, int y, int diameter, double initialSpeedX, double initialSpeedY) {
        super(x, y, diameter, diameter);
        this.dx = initialSpeedX;
        this.dy = initialSpeedY;    
    }

    public void checkCollision(GameObject obj) {
        // Logic to check for collision with another object
    }

    @Override
    public void update() {
        move();
        if (x <= 0) {
            x = 0;
            dx = -dx;
        }
        if (x + width > 800) {
            x = 800 - width;
            dx = -dx;
        }

        if (y <= 0) {
            y = 0;
            dy = -dy;
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x, y, width, height);
    }

    public void bounceOff(GameObject obj) {
        // Logic to change direction based on collision

    }

    
    
}
