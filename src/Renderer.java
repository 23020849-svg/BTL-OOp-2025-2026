package src;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class Renderer {

    
    /** Vẽ toàn bộ frame: entities + HUD + overlay */
    public void draw(Graphics g,
                     Paddle paddle,
                     Ball ball,
                     List<Brick> bricks,
                     List<PowerUp> powerUps,
                     int score,
                     int lives,
                     boolean ballLaunched,
                     double launchAngle,
                     boolean paused,
                     List<PowerUp> activePowerUps) {

        Graphics2D g2 = (Graphics2D) g;

        // Khử răng cưa
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // ===== Ball =====
        if (ball != null) {
            Ellipse2D circle = ball.getShape();
            Color base= new Color(190, 60, 255);

            g2.setColor(Color.WHITE);
            g2.fill(circle);
            for(int i=2;i>=1;i--)
            {
                float t= (float)i/2f;
                float alpha = 0.08f + 0.28f*t; //alpha từ 6% đen 40%
                int a255=(int)(alpha*255);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), a255));
                g2.setStroke(new BasicStroke(
                    5f + 8f*t,    //độ dày nét
                    BasicStroke.CAP_ROUND, //đầu nét tròn
                    BasicStroke.JOIN_ROUND)); //góc nối tròn
                     g2.draw(circle);


            }

            
        }

        // ===== Bricks =====
        if (bricks != null) {
            for (Brick b : bricks) {
                if (b == null || b.isDestroyed()) continue;
                Rectangle r = b.getBounds();
              int hp= b.getHitPoints();
                Color base=colorForHP(hp);

                Graphics2D g2c = (Graphics2D) g2.create();
                int m=8;
                g2c.setClip(r.x+m, r.y+m, r.width-2*m, r.height-2*m);
                drawNeonBrick(g2, r, base);
                g2c.dispose();
            }
        }

        // ===== Paddle =====
        if (paddle != null) {
            Rectangle r = paddle.getBounds();
            float arc=10f;
            RoundRectangle2D rr = new RoundRectangle2D.Float(r.x+1f, r.y+1f, r.width-2f, r.height-2f, arc, arc);
            Color base= new Color(80,240,255);

            
            for(int i=2;i>=1;i--)
            {
                float t= (float)i/2f;
                float alpha = 0.03f + 0.18f*t; //alpha từ 6% đen 40%
                int a255=(int)(alpha*255);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), a255));
                g2.setStroke(new BasicStroke(
                    4f + 6f*t,    //độ dày nét
                    BasicStroke.CAP_ROUND, //đầu nét tròn
                    BasicStroke.JOIN_ROUND)); //góc nối tròn
                     g2.draw(rr);


            }
            //core
            g2.setColor(base);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(rr);

            //lõi trắng
            g2.setColor(Color.WHITE);       
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(rr);

            

        }

        // ===== PowerUps =====
        if (powerUps != null) {
    for (PowerUp p : powerUps) {
        if (p == null || !p.isActive()) continue;

        Rectangle r = p.getBounds();

        if (p instanceof ExpandPaddlePowerUp) {
            // Power-up mở rộng paddle: oval xanh lá + viền xám
            g2.setColor(Color.GREEN);
            g2.fillOval(r.x, r.y, r.width, r.height);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(r.x, r.y, r.width, r.height);

        } else if (p instanceof FastBallPowerUp) {
            // Power-up tăng tốc bóng: oval xanh ngọc + viền xám
            g2.setColor(Color.CYAN);
            g2.fillOval(r.x, r.y, r.width, r.height);
            g2.setColor(Color.DARK_GRAY);
            g2.drawOval(r.x, r.y, r.width, r.height);

        } else {
            // Mặc định: hình chữ nhật vàng + viền xám
            g2.setColor(Color.YELLOW);
            g2.fillRect(r.x, r.y, r.width, r.height);
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(r.x, r.y, r.width, r.height);
        }
    }
}

        // ===== HUD =====
        g2.setColor(Color.WHITE);
        int pad = 10;
        int y = pad + g2.getFontMetrics().getAscent();
        String scoreText = "Score: " + score;
        String livesText = "Lives: " + lives;
        g2.drawString(scoreText, pad, y);

        int w = (g.getClipBounds() != null) ? g.getClipBounds().width : GameManager.WIDTH;
        int h = (g.getClipBounds() != null) ? g.getClipBounds().height : GameManager.HEIGHT;
        g2.drawString(livesText, w - pad - g2.getFontMetrics().stringWidth(livesText), y);

        // ===== Overlay: hướng dẫn & mũi tên ngắm khi chưa bắn =====
        if (!ballLaunched && ball != null) {
            g2.setColor(Color.WHITE);
            g2.drawString("Press SPACE to launch", w / 2 - 60, h / 2 - 10);

            g2.setColor(Color.RED);
            double rad = Math.toRadians(launchAngle);
            int cx = ball.getX() + ball.getWidth() /2;
            int cy = ball.getY() + ball.getHeight() / 2;
            int lineLength = 60;
            int endX = (int) (cx + lineLength * Math.cos(rad));
            int endY = (int) (cy + lineLength * Math.sin(rad));
            
            // vẽ đường ngắm nét đứt
            float thickness = 3f;
            float[] dash = {8f, 6f};
            Graphics2D g2c = (Graphics2D) g2.create();
            Color r= new Color(255,90,90);
            g2c.setColor(r);
            g2c.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, dash, 0f));
            g2c.drawLine(cx, cy, endX, endY);
            g2.drawString("Use 4/6 to aim", w / 2 - 50, h / 2 + 20);
        }

        // ===== Overlay: PAUSED =====
        if (paused) {
            String text = "PAUSED";
            Font old = g2.getFont();
            g2.setFont(new Font("Arial", Font.BOLD, 48));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (w - fm.stringWidth(text)) / 2;
            int ty = (h + fm.getAscent()) / 2 - 10;
            g2.setColor(Color.WHITE);
            g2.drawString(text, tx, ty);
            g2.setFont(old);
        }
    }

    // Màu gạch theo độ bền (HP)
    private Color colorForHP(int hp) {
        if (hp >= 3) return new Color(0x7f8c8d);
        if (hp == 2) return new Color(0x9b59b6);
        return Color.ORANGE;
    }
    private void drawNeonBrick(Graphics2D g2, Rectangle r, Color base) {
       float arc=10f;
       RoundRectangle2D rr = new RoundRectangle2D.Float(r.x + 1f, r.y + 1f, r.width-2f, r.height-2f, arc, arc);
       g2.setColor(new Color(10,35,50));
       g2.fill(rr);

       Color halo=base.darker();
       Color core= base.brighter();

       int layers=3;
       for(int i=layers;i>=1;i--)
       {
        float t= (float)i/layers;
        float alpha = 0.03f + 0.18f*t; //alpha từ 6% đen 40%
        int a255=(int)(alpha*255);
        Color haloA = new Color(halo.getRed(), halo.getGreen(), halo.getBlue(), a255);
        g2.setColor(haloA);
        g2.setStroke(new BasicStroke(
            5f + 10f*t,    //độ dày nét
            BasicStroke.CAP_ROUND, //đầu nét tròn
            BasicStroke.JOIN_ROUND)); //góc nối tròn
             g2.draw(rr);
       }
       // viền core
       g2.setColor(core);
       g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
       g2.draw(rr);

       // Lõi trắng
       g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    g2.draw(rr);

    }
}
