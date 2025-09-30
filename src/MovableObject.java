import java.awt.*;

public abstract class MovableObject {
    protected int x, y, width, height;

    public MovableObject(int x, int y, int width, int height){
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public Rectangle getBounds(){ return new Rectangle(x, y, width, height); }

    public abstract void draw(Graphics2D g);

    public int getX(){ return x; }
    public void setX(int x){ this.x = x; }
    public int getY(){ return y; }
    public void setY(int y){ this.y = y; }
    public int getWidth(){ return width; }
    public void setWidth(int width){ this.width = width; }
    public int getHeight(){ return height; }
    public void setHeight(int height){ this.height = height; }
}
