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

import arkanoid.entities.Ball;
import arkanoid.entities.LaserBeam;
import arkanoid.entities.Paddle;
import arkanoid.entities.bricks.Brick;
import arkanoid.entities.powerups.ExpandPaddlePowerUp;
import arkanoid.entities.powerups.FastBallPowerUp;
import arkanoid.entities.powerups.LaserPowerUp;
import arkanoid.entities.powerups.MultiBallPowerUp;
import arkanoid.entities.powerups.PowerUp;
import arkanoid.entities.powerups.PowerUpConfig;

public class Renderer {

    // ===== POWER-UP GIF IMAGES - GỘP TỪ 4 CLASS =====
    private Image expandPaddleGif;
    private Image extraBallGif;
    private Image fastBallGif;
    private Image laserGif;

    private long saveIndicatorTime = 0;
    private static final long SAVE_INDICATOR_DURATION = 2000;
    private Image heart;

    private static final double BALL_SCALE = 3.0;
    private static final int LIFE_ICON_SIZE = 40;

    public Renderer() {
        loadResources();
    }

    /**
     * Load tất cả resources (GIF và images)
     */
    private void loadResources() {
        try {
            // Load heart icon
            heart = new ImageIcon(getClass().getResource("/heart.png")).getImage();

            // Load power-up GIFs
            expandPaddleGif = new ImageIcon(getClass().getResource("/extra.gif")).getImage();
            extraBallGif = new ImageIcon(getClass().getResource("/extraball.gif")).getImage();
            fastBallGif = new ImageIcon(getClass().getResource("/fast.gif")).getImage();
            laserGif = new ImageIcon(getClass().getResource("/laser.gif")).getImage();

            System.out.println("✓ All renderer resources loaded successfully");
        } catch (Exception e) {
            System.err.println("Error loading renderer resources: " + e.getMessage());
            e.printStackTrace();
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
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // ===== Balls & beam =====
        if (balls != null) {
            for (Ball ball : new java.util.ArrayList<>(balls)) {
                if (ball == null)
                    continue;

                int bx = (int) ball.getX();
                int by = (int) ball.getY();
                int bw = ball.getWidth();
                int bh = ball.getHeight();

                int cx = bx + bw / 2;
                int cy = by + bh / 2;
                int rLogic = Math.min(bw, bh) / 2;
                int rDraw = (int) Math.round(rLogic * BALL_SCALE);

                List<double[]> t = ball.getTrail();
                double dirX = ball.getdx(), dirY = ball.getdy();
                if (t != null && t.size() >= 3) {
                    int k = Math.min(5, t.size());
                    double sx = 0, sy = 0;
                    for (int i = t.size() - k; i < t.size(); i++) {
                        sx += cx - t.get(i)[0];
                        sy += cy - t.get(i)[1];
                    }
                    dirX = sx / k;
                    dirY = sy / k;
                }
                drawTaperedBeamWhite(g2, cx, cy, rDraw, dirX, dirY);

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
            for (Brick b : new java.util.ArrayList<>(bricks)) {
                if (b == null || b.isDestroyed())
                    continue;
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

            if (paddle.isLaserActive()) {
                for (int i = 3; i >= 1; i--) {
                    float t = (float) i / 3f;
                    float alpha = 0.02f + 0.25f * t;
                    int a255 = (int) (alpha * 255);
                    g2.setColor(new Color(255, 50, 50, a255));
                    g2.setStroke(new BasicStroke(
                            6f + 8f * t, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.draw(rr);
                }
            }

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

        // Vẽ laser beams
        List<LaserBeam> lasers = paddle.getLasers();
        if (lasers != null) {
            for (LaserBeam laser : new java.util.ArrayList<>(lasers)) {
                if (laser != null && laser.isActive()) {
                    laser.render(g2);
                }
            }
        }

        // ===== PowerUps - SỬ DỤNG GIF ĐÃ LOAD =====
        if (powerUps != null) {
            for (PowerUp p : new java.util.ArrayList<>(powerUps)) {
                if (p == null || !p.isActive())
                    continue;
                Rectangle r = p.getBounds();

                // Vẽ GIF tương ứng với từng loại power-up
                if (p instanceof ExpandPaddlePowerUp && expandPaddleGif != null) {
                    g2.drawImage(expandPaddleGif, r.x, r.y, 100, 60, null);
                } else if (p instanceof FastBallPowerUp && fastBallGif != null) {
                    g2.drawImage(fastBallGif, r.x, r.y, 100, 60, null);
                } else if (p instanceof MultiBallPowerUp && extraBallGif != null) {
                    g2.drawImage(extraBallGif, r.x, r.y, 100, 60, null);
                } else if (p instanceof LaserPowerUp && laserGif != null) {
                    g2.drawImage(laserGif, r.x, r.y, 100, 60, null);
                } else {
                    // Fallback nếu không load được GIF
                    g2.setColor(Color.YELLOW);
                    g2.fillRect(r.x, r.y, r.width, r.height);
                    g2.setColor(Color.DARK_GRAY);
                    g2.drawRect(r.x, r.y, r.width, r.height);
                }
            }
        }

        // ===== HUD =====
        int w = (g.getClipBounds() != null) ? g.getClipBounds().width : 1440;
        int h = (g.getClipBounds() != null) ? g.getClipBounds().height : 800;

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2.getFontMetrics();
        int pad = 20;
        int textY = 40;

        // Score
        String scoreText = "Score: " + score;
        g2.drawString(scoreText, pad, textY);

        // Lives with icons
        drawLivesWithIcons(g2, lives, w, textY, fm, pad);

        // ===== ACTIVE POWER-UPS INFO =====
        if (activePowerUps != null && !activePowerUps.isEmpty()) {
            drawActivePowerUpsInfo(g2, activePowerUps, pad, textY);
        }

        // Save indicator
        if (saveIndicatorTime > 0) {
            drawSaveIndicator(g2, w);
        }

        // ===== Overlay: PAUSED =====
        if (paused) {
            drawPausedOverlay(g2, w, h);
        }
    }

    /**
     * Vẽ thông tin các power-up đang active
     */
    private void drawActivePowerUpsInfo(Graphics2D g2, List<PowerUp> activePowerUps,
            int pad, int baseY) {
        int infoX = pad - 15;
        int infoY = baseY + 200;

        infoY += 25;
        g2.setFont(new Font("Arial", Font.PLAIN, 16));

        for (PowerUp p : new java.util.ArrayList<>(activePowerUps)) {
            if (p == null || !p.isActivated())
                continue;

            String info = "";
            Color iconColor = Color.WHITE;

            if (p instanceof ExpandPaddlePowerUp) {
                info = " Expand: +" +
                        PowerUpConfig.EXPAND_EXTRA_PIXELS + "px (" +
                        p.getRemainingTime() + "s)";
                iconColor = new Color(100, 255, 100);

            } else if (p instanceof FastBallPowerUp) {
                info = "Fast: x" +
                        PowerUpConfig.FAST_BALL_MULTIPLIER + " (" +
                        p.getRemainingTime() + "s)";
                iconColor = new Color(100, 200, 255);

            } else if (p instanceof LaserPowerUp) {
                info = "Laser (" + p.getRemainingTime() + "s)";
                iconColor = new Color(255, 100, 100);
            }

            if (!info.isEmpty()) {
                g2.setColor(iconColor);
                g2.drawString(info, infoX + 10, infoY);
                infoY += 22;
            }
        }
    }

    /**
     * Vẽ thông báo "Game Saved!"
     */
    private void drawSaveIndicator(Graphics2D g2, int screenWidth) {
        long elapsed = System.currentTimeMillis() - saveIndicatorTime;
        if (elapsed < SAVE_INDICATOR_DURATION) {
            float alpha = 1.0f - (float) elapsed / SAVE_INDICATOR_DURATION;
            g2.setComposite(java.awt.AlphaComposite.getInstance(
                    java.awt.AlphaComposite.SRC_OVER, alpha));

            g2.setColor(new Color(76, 175, 80));
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            FontMetrics fmSave = g2.getFontMetrics();
            String text = "✓ Game Saved!";
            int x = (screenWidth - fmSave.stringWidth(text)) / 2;
            int y = 50;
            g2.drawString(text, x, y);

            g2.setComposite(java.awt.AlphaComposite.SrcOver);
        } else {
            saveIndicatorTime = 0;
        }
    }

    /**
     * Vẽ overlay "PAUSED"
     */
    private void drawPausedOverlay(Graphics2D g2, int w, int h) {
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

    // ===== Helper Methods =====

    private Color colorForHP(int hp) {
        if (hp >= 3)
            return new Color(0x7f8c8d);
        if (hp == 2)
            return new Color(0x9b59b6);
        return Color.ORANGE;
    }

    private void drawLivesWithIcons(Graphics2D g2, int lives, int screenWidth,
            int textY, FontMetrics fm, int pad) {
        String livesLabel = "Lives: ";
        int scoreWidth = fm.stringWidth("Score: 999999");
        int startX = scoreWidth + pad;

        g2.drawString(livesLabel, startX, textY);

        long time = System.currentTimeMillis();
        float pulse = (float) Math.abs(Math.sin(time / 400.0));

        int iconX = startX + fm.stringWidth(livesLabel);
        int iconY = textY - LIFE_ICON_SIZE + 14;

        for (int i = 0; i < 3; i++) {
            if (i < lives) {
                int alpha = (int) (150 + 100 * pulse);
                g2.setComposite(java.awt.AlphaComposite.getInstance(
                        java.awt.AlphaComposite.SRC_OVER, alpha / 255f));
                g2.drawImage(heart, iconX, iconY, LIFE_ICON_SIZE, LIFE_ICON_SIZE, null);
                g2.setComposite(java.awt.AlphaComposite.SrcOver);
            } else {
                g2.setComposite(java.awt.AlphaComposite.getInstance(
                        java.awt.AlphaComposite.SRC_OVER, 0.4f));
                g2.drawImage(heart, iconX, iconY, LIFE_ICON_SIZE, LIFE_ICON_SIZE, null);
                g2.setComposite(java.awt.AlphaComposite.SrcOver);
            }
            iconX += LIFE_ICON_SIZE + 5;
        }
    }

    private void drawTaperedBeamWhite(Graphics2D g2, double cx, double cy, int rDraw,
            double dirX, double dirY) {
        double L = Math.hypot(dirX, dirY);
        if (L < 1e-6)
            return;
        double ux = dirX / L, uy = dirY / L;

        float len = (float) Math.max(100, rDraw * 5.0f);
        float headW = rDraw * 0.8f;
        float tailW = rDraw * 0.06f;

        float headX = (float) cx, headY = (float) cy;
        float tailX = (float) (cx - ux * len), tailY = (float) (cy - uy * len);

        double nx = -uy, ny = ux;

        java.util.function.BiConsumer<float[], Integer> fillLayer = (ws, aHead) -> {
            float wHead = ws[0], wTail = ws[1];

            float hxL = (float) (headX - nx * (wHead * 0.5f));
            float hyL = (float) (headY - ny * (wHead * 0.5f));
            float hxR = (float) (headX + nx * (wHead * 0.5f));
            float hyR = (float) (headY + ny * (wHead * 0.5f));
            float txL = (float) (tailX - nx * (wTail * 0.5f));
            float tyL = (float) (tailY - ny * (wTail * 0.5f));
            float txR = (float) (tailX + nx * (wTail * 0.5f));
            float tyR = (float) (tailY + ny * (wTail * 0.5f));

            Path2D p = new Path2D.Float();
            p.moveTo(txL, tyL);
            p.lineTo(hxL, hyL);
            p.lineTo(hxR, hyR);
            p.lineTo(txR, tyR);
            p.closePath();

            Paint old = g2.getPaint();
            g2.setPaint(new LinearGradientPaint(
                    headX, headY, tailX, tailY,
                    new float[] { 0f, 0.55f, 1f },
                    new Color[] {
                            new Color(255, 255, 255, Math.min(255, aHead)),
                            new Color(255, 255, 255, Math.max(0, aHead - 70)),
                            new Color(255, 255, 255, 0)
                    }));
            g2.fill(p);
            g2.setPaint(old);
        };

        fillLayer.accept(new float[] { headW * 1.25f, rDraw * 0.10f }, 50);
        fillLayer.accept(new float[] { headW * 1.08f, rDraw * 0.08f }, 30);
        fillLayer.accept(new float[] { headW * 0.90f, tailW }, 20);
    }

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
            Color haloA = new Color(halo.getRed(), halo.getGreen(),
                    halo.getBlue(), a255);
            g2.setColor(haloA);
            g2.setStroke(new BasicStroke(5f + 10f * t,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(rr);
        }

        g2.setColor(core);
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.draw(rr);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));
        g2.draw(rr);
    }

    public void showSaveIndicator() {
        saveIndicatorTime = System.currentTimeMillis();
    }
}