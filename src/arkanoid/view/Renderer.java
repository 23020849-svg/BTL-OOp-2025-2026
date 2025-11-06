package arkanoid.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

import javax.swing.ImageIcon;

import arkanoid.core.GameManager;
import arkanoid.entities.Ball;
import arkanoid.entities.LaserBeam;
import arkanoid.entities.Paddle;
import arkanoid.entities.bricks.Brick;
import arkanoid.entities.powerups.ExpandPaddlePowerUp;
import arkanoid.entities.powerups.FastBallPowerUp;
import arkanoid.entities.powerups.MultiBallPowerUp;
import arkanoid.entities.powerups.PowerUp;

public class Renderer {

    private arkanoid.view.expandpaddle ex = new arkanoid.view.expandpaddle();
    private arkanoid.view.extraball   exball = new arkanoid.view.extraball();
    private arkanoid.view.fast        fast = new arkanoid.view.fast();

    private Image heart;
    private Image returnIcon; // ảnh /return.png

    private static final double BALL_SCALE   = 3.0; // phóng to khi vẽ
    private static final int    ARROW_GAP    = 2;   // khoảng hở giữa mép bóng & mũi tên
    private static final int    LIFE_ICON_SIZE = 40;


    public Renderer() {
        try {
            heart      = new ImageIcon(getClass().getResource("/heart.png")).getImage();
           
        } catch (Exception e) {
            System.err.println("Không thể load ảnh: " + e.getMessage());
        }
    }

    /** Vẽ toàn bộ frame: entities + HUD + overlay */
    public void draw(Graphics g,
                     Paddle paddle,
                     Color paddleColor,
                     List<Ball> balls,
                     Color ballColor,
                     List<Brick> bricks,
                     List<PowerUp> powerUps,
                     int score,
                     int lives,
                     boolean ballLaunched,
                     double launchAngle,
                     boolean paused,
                     List<PowerUp> activePowerUps,
                     boolean isFirstLife) {

        Graphics2D g2 = (Graphics2D) g;

        // Khử răng cưa
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ===== Balls & beam =====
        if (balls != null) {
            for (Ball ball : balls) {
                if (ball == null) continue;

                int bx = (int) ball.getX();
                int by = (int) ball.getY();
                int bw = ball.getWidth();
                int bh = ball.getHeight();

                int cx = bx + bw / 2;
                int cy = by + bh / 2;
                int rLogic = Math.min(bw, bh) / 2;
                int rDraw  = (int) Math.round(rLogic * BALL_SCALE);

                List<double[]> t = ball.getTrail();
                double dirX = ball.getdx(), dirY = ball.getdy(); // fallback theo vận tốc
                if (t != null && t.size() >= 3) {
                    int k = Math.min(5, t.size());   // lấy tối đa 5 điểm cuối để mượt hướng
                    double sx = 0, sy = 0;
                    for (int i = t.size() - k; i < t.size(); i++) {
                        sx += cx - t.get(i)[0];
                        sy += cy - t.get(i)[1];
                    }
                    dirX = sx / k;
                    dirY = sy / k;
                }
                drawTaperedBeamWhite(g2, cx, cy, rDraw, dirX, dirY);

                // --- Vẽ bóng ---
                Image ballImg = ball.getBallImage();
                if (ballImg != null) {
                    int d = rDraw * 2;
                    g2.drawImage(ballImg, cx - rDraw, cy - rDraw, d, d, null);
                } else {
                    g2.setColor(ball.getBallColor());
                    g2.fillOval(cx - rDraw, cy - rDraw, rDraw * 2, rDraw * 2);
                }
            }
        }

        // ===== Bricks =====
        if (bricks != null) {
            for (Brick b : bricks) {
                if (b == null || b.isDestroyed()) continue;
                Rectangle r = b.getBounds();
                Color base = colorForHP(b.getHitPoints());
                Graphics2D g2c = (Graphics2D) g2.create();
                drawNeonBrick(g2c, r, base);
                g2c.dispose();
            }
        }

        // ===== Paddle =====
        if (paddle != null) {
            Rectangle r = paddle.getBounds();
            float arc = 10f;
            RoundRectangle2D rr = new RoundRectangle2D.Float(
                    r.x + 1f, r.y + 1f, r.width - 2f, r.height - 2f, arc, arc);

            Color base = paddleColor;

            // nếu laser active, vẽ glow đỏ

            if (paddle.isLaserActive() ) {
                for(int i=3; i >=1; i--) {
                    float t = (float) i / 3f;
                    float alpha = 0.02f + 0.25f * t;
                    int a255 = (int) (alpha * 255);
                    g2.setColor(new Color(255, 50, 50, a255));
                    g2.setStroke(new BasicStroke(
                            6f + 8f * t, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(rr);
                }
    
            }

            // vẽ paddle chính
            for (int i = 2; i >= 1; i--) {
                float t = (float) i / 2f;
                float alpha = 0.03f + 0.18f * t;
                int a255 = (int) (alpha * 255);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), a255));
                g2.setStroke(new BasicStroke(
                        4f + 6f * t, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(rr);
            }
            g2.setColor(base);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(rr);

            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(rr);
        }

        // vẽ laser beams

        List<LaserBeam> lasers = paddle.getLasers();
        if (lasers != null) {
            for (LaserBeam laser : lasers) {
                if (laser != null && laser.isActive()){
                    laser.render(g2);
                }

            }
        }

        // ===== PowerUps =====
        if (powerUps != null) {
            for (PowerUp p : powerUps) {
                if (p == null || !p.isActive()) continue;
                Rectangle r = p.getBounds();

                if (p instanceof ExpandPaddlePowerUp) {
                    ImageIcon gif = ex.getGifIcon();
                    if (gif != null) g2.drawImage(gif.getImage(), r.x, r.y, 100, 60, null);
                } else if (p instanceof FastBallPowerUp) {
                    ImageIcon gif = fast.getGifIcon();
                    if (gif != null) g2.drawImage(gif.getImage(), r.x, r.y, 100, 60, null);
                } else if (p instanceof MultiBallPowerUp) {
                    ImageIcon gif = exball.getGifIcon();
                    if (gif != null) g2.drawImage(gif.getImage(), r.x, r.y, 100, 60, null);
                } else {
                    g2.setColor(Color.YELLOW);
                    g2.fillRect(r.x, r.y, r.width, r.height);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawRect(r.x, r.y, r.width, r.height);
                }
            }
        }

        // ===== HUD =====
        int w = (g.getClipBounds() != null) ? g.getClipBounds().width  : GameManager.WIDTH;
        int h = (g.getClipBounds() != null) ? g.getClipBounds().height : GameManager.HEIGHT;

        Font hudFont = MenuRenderer.loadCustomFont(22f);
        g2.setFont(hudFont);
        g2.setColor(Color.WHITE);
        int pad = 10;
        FontMetrics fm = g2.getFontMetrics();
        int textY = pad + fm.getAscent();

        g2.drawString("Score: " + score, pad, textY);
        drawLivesWithIcons(g2, lives, w, textY, fm, pad);

        // Active powerups (ví dụ)
        int yOffset = 300;
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        if (paddle != null && paddle.getExpandRemainingTime() > 0) {
            g2.drawString("PowerUp: ExpandPaddlePowerUp – " + paddle.getExpandRemainingTime() + "s", 10, yOffset);
        }

        // ===== Overlay: aiming arrow =====
        if (!ballLaunched && balls != null && !balls.isEmpty()) {
            Ball firstBall = balls.get(0);
            int fbw = firstBall.getWidth(), fbh = firstBall.getHeight();
            int fcx = (int) firstBall.getX() + fbw / 2;
            int fcy = (int) firstBall.getY() + fbh / 2;
            int frLogic = Math.min(fbw, fbh) / 2;
            int frDraw  = (int) Math.round(frLogic * BALL_SCALE);

            double rad = Math.toRadians(launchAngle);
            int lineLength = 60;

            int startX = (int) (fcx + (frDraw + ARROW_GAP) * Math.cos(rad));
            int startY = (int) (fcy + (frDraw + ARROW_GAP) * Math.sin(rad));
            int endX   = (int) (fcx + (frDraw + ARROW_GAP + lineLength) * Math.cos(rad));
            int endY   = (int) (fcy + (frDraw + ARROW_GAP + lineLength) * Math.sin(rad));

            Graphics2D g2c = (Graphics2D) g2.create();
            g2c.setColor(new Color(244, 63, 94));
            g2c.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                          1f, new float[]{8f, 6f}, 0f));
            g2c.drawLine(startX, startY, endX, endY);
            g2c.dispose();

            g2.drawString("Press SPACE to launch", w / 2 - 60, h / 2 - 10);
            g2.drawString("Use 4/6 to aim",       w / 2 - 50, h / 2 + 20);
        }

       

        // ===== Overlay: PAUSED =====
        if (paused) {
            String text = "PAUSED";
            Font old = g2.getFont();
            Font pauseFont = MenuRenderer.loadCustomFont(48f);
            g2.setFont(pauseFont);

            FontMetrics pfm = g2.getFontMetrics();
            int tx = (w - pfm.stringWidth(text)) / 2;
            int ty = (h + pfm.getAscent()) / 2 - 10;
            g2.drawString(text, tx, ty);
            g2.setFont(old);
        }
    }

    // ===== Helpers =====

    private Color colorForHP(int hp) {
        if (hp >= 3) return new Color(0x7f8c8d);
        if (hp == 2) return new Color(0x9b59b6);
        return Color.ORANGE;
    }

    private void drawLivesWithIcons(Graphics2D g2, int lives, int screenWidth, int textY, FontMetrics fm, int pad) {
        String livesLabel = "Lives: ";
        int scoreWidth = fm.stringWidth("Score: 999999"); // Ước tính độ rộng Score

        int totalIconsWidth = 3 * (LIFE_ICON_SIZE + 5); // giả định max 3 mạng
        int startX = scoreWidth + pad ;

        g2.drawString(livesLabel, startX, textY);

        long time = System.currentTimeMillis();
        float pulse = (float) Math.abs(Math.sin(time / 400.0)); // 0 → 1 → 0 mỗi 800ms

        int iconX = startX + fm.stringWidth(livesLabel);
        int iconY = textY - LIFE_ICON_SIZE + 14;

        for (int i = 0; i < 3; i++) {
            if (i < lives) {
                int alpha = (int) (150 + 100 * pulse);
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha / 255f));
                g2.drawImage(heart, iconX, iconY, LIFE_ICON_SIZE, LIFE_ICON_SIZE, null);
                g2.setComposite(java.awt.AlphaComposite.SrcOver);
            } else {
                g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.4f));
                g2.drawImage(heart, iconX, iconY, LIFE_ICON_SIZE, LIFE_ICON_SIZE, null);
                g2.setComposite(java.awt.AlphaComposite.SrcOver);
            }
            iconX += LIFE_ICON_SIZE + 5;
        }
    }

    /** Beam trắng mờ, thu nhọn ở đuôi */
    private void drawTaperedBeamWhite(Graphics2D g2, double cx, double cy, int rDraw,
                                      double dirX, double dirY) {
        double L = Math.hypot(dirX, dirY);
        if (L < 1e-6) return;
        double ux = dirX / L, uy = dirY / L;

        float len   = (float)Math.max(100, rDraw * 5.0f); // dài -> mềm
        float headW = rDraw * 0.8f;
        float tailW = rDraw * 0.06f; // nhọn

        float headX = (float)cx, headY = (float)cy;
        float tailX = (float)(cx - ux*len), tailY = (float)(cy - uy*len);

        double nx = -uy, ny = ux;

        // helper vẽ 1 lớp tapered với alpha đầu/giữa/đuôi
        java.util.function.BiConsumer<float[], Integer> fillLayer = (ws, aHead) -> {
            float wHead = ws[0], wTail = ws[1];  // [headW, tailW]

            float hxL = (float)(headX - nx * (wHead*0.5f));
            float hyL = (float)(headY - ny * (wHead*0.5f));
            float hxR = (float)(headX + nx * (wHead*0.5f));
            float hyR = (float)(headY + ny * (wHead*0.5f));
            float txL = (float)(tailX - nx * (wTail*0.5f));
            float tyL = (float)(tailY - ny * (wTail*0.5f));
            float txR = (float)(tailX + nx * (wTail*0.5f));
            float tyR = (float)(tailY + ny * (wTail*0.5f));

            Path2D p = new Path2D.Float();
            p.moveTo(txL, tyL); p.lineTo(hxL, hyL); p.lineTo(hxR, hyR); p.lineTo(txR, tyR); p.closePath();

            Paint old = g2.getPaint();
            g2.setPaint(new LinearGradientPaint(
                    headX, headY, tailX, tailY,
                    new float[]{0f, 0.55f, 1f},
                    new Color[]{
                        new Color(255,255,255, Math.min(255, aHead)), // đầu
                        new Color(255,255,255, Math.max(0, aHead-70)),// giữa
                        new Color(255,255,255, 0)                     // đuôi
                    }
            ));
            g2.fill(p);
            g2.setPaint(old);
        };

        // feather ngoài cùng (rộng, rất mờ)
        fillLayer.accept(new float[]{ headW*1.25f, rDraw*0.10f }, 50);
        // feather giữa (vừa, mờ)
        fillLayer.accept(new float[]{ headW*1.08f, rDraw*0.08f }, 30);
        // lõi (hẹp, đậm)
        fillLayer.accept(new float[]{ headW*0.90f, tailW          }, 20);
    }

    /** Gạch hiệu ứng neon */
    private void drawNeonBrick(Graphics2D g2, Rectangle r, Color base) {
        float arc = 10f;
        RoundRectangle2D rr = new RoundRectangle2D.Float(
                r.x + 1f, r.y + 1f, r.width - 2f, r.height - 2f, arc, arc);

        g2.setColor(new Color(10, 35, 50));
        g2.fill(rr);

        Color halo = base.darker();
        Color core = base.brighter();

        int layers = 3;
        for (int i = layers; i >= 1; i--) {
            float t = (float) i / layers;
            float alpha = 0.03f + 0.3f * t;
            int a255 = (int) (alpha * 255);
            Color haloA = new Color(halo.getRed(), halo.getGreen(), halo.getBlue(), a255);
            g2.setColor(haloA);
            g2.setStroke(new BasicStroke(5f + 10f * t, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(rr);
        }

        g2.setColor(core);
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(rr);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(rr);
    }

    

   
}
