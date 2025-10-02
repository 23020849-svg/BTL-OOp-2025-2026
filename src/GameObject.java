// Up lên cho đỡ trống chưa biết đúng sai hay nào đâu hôm sau làm tiếp :))

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GameObject {
    protected double x, y;
    protected int width, height;

    public GameObject(double x, double y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void update(double delta);
    public abstract void render(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, width, height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}

