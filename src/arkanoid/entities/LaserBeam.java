package arkanoid.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class LaserBeam extends MovableObject {

    private static final double LASER_SPEED = 600.0;
    private static final int LASER_WIDTH = 4;
    private static final int LASER_HEIGHT = 20;
    private Color laserColor;
    private boolean active;

    public LaserBeam(double x, double y) {
        super((int) x, (int) y, LASER_WIDTH, LASER_HEIGHT);
        this.dy = -LASER_SPEED; // Bay lên trên
        this.laserColor = new Color(255, 50, 50); // Màu đỏ
        this.active = true;
    }

    @Override
    public void update(double dt) {
        if (!active) return;
        move(dt);

        //vo hieu hoa neu ra khoi man hinh

        if(y+height < 0) {
            active = false;
        }
    }

    public void render(Graphics2D g2) {
        if (!active) return;

         Rectangle2D.Double beam = new Rectangle2D.Double(x, y, width, height);
        
          
        // Glow effect
        for (int i = 3; i >= 1; i--) {
            float alpha = 0.1f + 0.2f * (4 - i) / 3f;
            g2.setColor(new Color(255, 50, 50, (int)(alpha * 255)));
            Rectangle2D.Double glow = new Rectangle2D.Double(
                x - i, y - i, width + 2*i, height + 2*i
            );
            g2.fill(glow);
        }
        
        // Core beam
        g2.setColor(Color.WHITE);
        g2.fill(beam);
        
        // Outer glow
        g2.setColor(laserColor);
        g2.draw(beam); 
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean checkCollision(Rectangle bounds) {
        return active && getBounds().intersects(bounds);
    }

    public void deactivate() {
        this.active = false;
    }
    


    
}
