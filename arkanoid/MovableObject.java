import java.awt.Graphics;

public abstract class MovableObject extends GameObject {
    protected double dx, dy;
    
    public MovableObject(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public void move() {
        x += (int)dx;
        y += (int)dy;
    }

    @Override
    public abstract void update();

    @Override
    public abstract void render(Graphics g);

    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public void setDx(double dx) { this.dx = dx; }
    public void setDy(double dy) { this.dy = dy; }

    
}
