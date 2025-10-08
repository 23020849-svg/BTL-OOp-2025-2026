import java.awt.Color;
import java.awt.Graphics;
public class Brick extends GameObject {
    private int hitPoints; //Số lần va chạm để phá hủy
    private String type; //Loại gạch (ví dụ: thường, cứng, có power-up)
    private boolean destroyed = false; //Trạng thái gạch đã bị phá hủy hay chưa
    public Brick(int x, int y, int width, int height, int hitPoints, String type) {
        super(x, y, width, height);
        this.hitPoints = hitPoints;
        this.type = type;
    }

    public void takeHit() {
        hitPoints--;
        if (hitPoints <= 0) {
            isDestroyed();
        }
    }

    public void isDestroyed() {
        // Logic khi gạch bị phá hủy
            this.destroyed = true;

    }

    @Override
    public void update() {
        // Update brick state if needed
    }

    @Override
    public void render(Graphics g) {
        // Vẽ gạch lên Graphics
        if (!this.destroyed) {
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height); // Viền gạch
        }
    }
    
}
