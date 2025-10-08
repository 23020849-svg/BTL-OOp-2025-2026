import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class GameObject {
    protected int x, y, width, height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**Cập nhật trạng thái logic của đối tượng mỗi khung hình */
    public abstract void update();

    /**Vẽ đối tượng lên Graphics */
    public abstract void render(Graphics g);
}
