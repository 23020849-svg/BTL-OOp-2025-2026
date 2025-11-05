package arkanoid.entities;

import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import arkanoid.utils.Sound;

public class Ball extends MovableObject {
    private double baseSpeed = 360.0;
    private int radius;
    private double speedMultiplier = 1.0;
    private long fastEndTime = 0;
    private Sound CollisionWall;
    private List<double[]> trail = new ArrayList<>();
    private static final int TRAIL_SIZE = 8;
    private Color ballColor;
    private String ballImagePath = "/balls/ball_red.png";
    private Image ballImage = null;

    public Ball(int x, int y, int radius, double initialSpeedX, double initialSpeedY, Color ballColor) {
        super(x, y, radius * 2, radius * 2);
        this.radius = radius;
        this.dx = initialSpeedX;
        this.dy = initialSpeedY;
        this.ballColor = ballColor;
        CollisionWall = new Sound();
        CollisionWall.loadSound("/tapwall.wav");
        normalizeSpeed(baseSpeed);
    }

    public void setBallImagePath(String imagePath) {
        this.ballImagePath = imagePath;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(imagePath));
            this.ballImage = icon.getImage();
        } catch (Exception e) {
            System.err.println("Không thể load ảnh ball: " + imagePath);
            this.ballImage = null;
        }
    }

    public Image getBallImage() {
        return ballImage;
    }

    public String getBallImagePath() {
        return ballImagePath;
    }

    public void setBallColor(Color color) {
        this.ballColor = color;
    }

    @Override
    public void update(double dt) {
        // Empty
    }

    public void update(double dt, int screenWidth, int screenHeight) {
        trail.add(new double[]{x + width/2.0, y + height/2.0});
        if(trail.size() > TRAIL_SIZE) trail.remove(0);
        move(dt);

        if (x <= 0) {
            x = 0;
            dx = -dx;
            CollisionWall.playOnce();
        }
        if (x + width >= screenWidth) {
            x = screenWidth - width;
            dx = -dx;
            CollisionWall.playOnce();
        }

        if (y <= 0) {
            y = 0;
            dy = -dy;
            CollisionWall.playOnce();
        }

        if (fastEndTime > 0 && System.currentTimeMillis() > fastEndTime) {
            speedMultiplier = 1.0;
            fastEndTime = 0;

            normalizeSpeed(baseSpeed * speedMultiplier);
        }
    }

    public List<double[]> getTrail() {
        return trail;
    }

    public void bounceOff(GameObject other) {
        Rectangle b = getBounds();
        Rectangle o = other.getBounds();

        Rectangle intersection = b.intersection(o);
        if (intersection.isEmpty()) return;

        if (intersection.width < intersection.height) {
            dx = -dx;
            if (b.getCenterX() < o.getCenterX()) {
                x -= intersection.width;
            } else {
                x += intersection.width;
            }
        } else {
            dy = -dy;
            if (b.getCenterY() < o.getCenterY()) {
                y -= intersection.height;
            } else {
                y += intersection.height;
            }
        }
    }

    public void setSpeedMultiplier(double m, long durationMillis) {
        speedMultiplier = m;
        fastEndTime = System.currentTimeMillis() + durationMillis;
        dx *= m;
        dy *= m;
    }

    public void normalizeSpeed(double targetMagnitude) {
        double mag = Math.sqrt(dx * dx + dy * dy);
        if (mag == 0) return;
        dx = (dx / mag) * targetMagnitude;
        dy = (dy / mag) * targetMagnitude;
    }

    public int getRadius() { return radius; }
    
    public int getFastRemainingTime() {
        if (fastEndTime == 0) return 0;
        long remaining = fastEndTime - System.currentTimeMillis();
        return Math.max((int)(remaining / 1000), 0);
    }

    public Ellipse2D getShape() {
        return new Ellipse2D.Double(x, y, width, height);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getdx() { return dx; }
    public double getdy() { return dy; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setPosition(double nx, double ny) {
        this.x = nx;
        this.y = ny;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public long getFastEndTime() {
        return fastEndTime;
    }

    public double getVX() { return dx; }
    public double getVY() { return dy; }
    
    public void setVelocity(double nvx, double nvy) {
        this.dx = nvx;
        this.dy = nvy;
    }

    public Color getBallColor() {
        return ballColor;
    }

    public double getSpeed() {
        return Math.sqrt(dx*dx + dy*dy);
    }
    
    @Override
    public void rescale(double scaleX, double scaleY) {
        super.rescale(scaleX, scaleY);
        this.radius = (int) (this.radius * scaleX);
    }

    public double getBaseSpeed() {
        return baseSpeed;
    }
}