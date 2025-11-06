package arkanoid.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

public class MenuRenderer {

    // Colors
    private static final Color BACKGROUND_COLOR = new Color(10, 15, 25);
    private static final Color PRIMARY_COLOR = new Color(80, 240, 255);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color SELECTED_COLOR = new Color(255, 200, 0);

    private static Font ARKANOID_TITLE_FONT = null;

    private Rectangle leaderboardButtonBounds = new Rectangle();
    
    // Custom Store - Color boxes bounds
    private Rectangle[] paddleColorBoxes = new Rectangle[8];
    private Rectangle[] ballColorBoxes = new Rectangle[8];

    // Các màu mẫu cho paddle và ball
    private Color[] PADDLE_COLORS = {
        new Color(255, 100, 100),  // Red
        new Color(100, 255, 100),  // Green
        new Color(100, 100, 255),  // Blue
        new Color(255, 255, 100),  // Yellow
        new Color(255, 100, 255),  // Magenta
        new Color(100, 255, 255),  // Cyan
        new Color(255, 200, 100),  // Orange
        Color.WHITE                // White
    };

    private Color[] BALL_COLORS = {
        new Color(255, 50, 50),    // Red
        new Color(50, 255, 50),    // Green
        new Color(50, 50, 255),    // Blue
        new Color(255, 255, 50),   // Yellow
        new Color(255, 50, 255),   // Magenta
        new Color(50, 255, 255),   // Cyan
        new Color(255, 150, 50),   // Orange
        Color.WHITE                // White
    };

    // Các đường dẫn đến file ảnh ball
    private String[] BALL_IMAGE_PATHS = {
        "/balls/ball_red.png",
        "/balls/ball_blue.png",
        "/balls/ball_gradient.png",
        "/balls/ball_green.png",
        "/balls/ball_orange.png",
        "/balls/ball_pink.png",
        "/balls/ball_violet.png",
        "/balls/ball_xanhlo.png"
    };

    // Cache ảnh ball để không phải load lại nhiều lần
    private Image[] ballImages = new Image[8];

    /**
     * Load ảnh ball từ resources
     */
    private Image loadBallImage(int index) {
        if (index < 0 || index >= BALL_IMAGE_PATHS.length) {
            return null;
        }
        
        if (ballImages[index] == null) {
            try {
                ImageIcon icon = new ImageIcon(getClass().getResource(BALL_IMAGE_PATHS[index]));
                ballImages[index] = icon.getImage();
            } catch (Exception e) {
                System.err.println("Không thể load ảnh ball: " + BALL_IMAGE_PATHS[index]);
                ballImages[index] = null;
            }
        }
        return ballImages[index];
    }

    public static Font loadCustomFont(float size) {
        if (ARKANOID_TITLE_FONT == null) {
            try {
                InputStream is = MenuRenderer.class.getResourceAsStream("/font.otf");
                if (is == null) {
                    throw new IOException("Font file not found in resources: /font.otf");
                }
                ARKANOID_TITLE_FONT = Font.createFont(Font.TRUETYPE_FONT, is);
                is.close();
            } catch (IOException | FontFormatException e) {
                e.printStackTrace();
                System.err.println("Không thể tải font tùy chỉnh. Sử dụng font Arial mặc định.");
                ARKANOID_TITLE_FONT = new Font("Arial", Font.BOLD, (int) size);
            }
        }
        return ARKANOID_TITLE_FONT.deriveFont(size);
    }

    public void drawMainMenu(Graphics g, String[] options, int selectedOption, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/menu.gif"));
            Image img = icon.getImage();
            if (img != null) {
                g2.drawImage(img, 0, 0, screenWidth, screenHeight, null);
            } else {
                drawBackground(g2, screenWidth, screenHeight);
            }
        } catch (Exception e) {
            drawBackground(g2, screenWidth, screenHeight);
        }

        drawTitle(g2, screenWidth, screenHeight);
        drawMenuOptions(g2, options, selectedOption, screenWidth, screenHeight);
        drawFooterInstructions(g2, screenWidth, screenHeight);
    }

    private void drawTitle(Graphics2D g2, int screenWidth, int screenHeight) {
        String title = "ARKANOID";
        int y = 200;
        float titleSize = 100f;
        Font customFont = loadCustomFont(titleSize);
        g2.setFont(customFont);
        FontMetrics fm = g2.getFontMetrics();
        int x = (screenWidth - fm.stringWidth(title)) / 2;

        for (int i = 3; i >= 1; i--) {
            g2.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), 50 / i));
            g2.setFont(customFont.deriveFont(titleSize + i * 2));
            FontMetrics glowFm = g2.getFontMetrics();
            int glowX = (screenWidth - glowFm.stringWidth(title)) / 2;
            int glowY = y + i * 2;
            g2.drawString(title, glowX, glowY);
        }

        g2.setColor(PRIMARY_COLOR);
        g2.setFont(customFont.deriveFont(titleSize));
        g2.drawString(title, x, y);
    }

    private void drawMenuOptions(Graphics2D g2, String[] options, int selectedOption, int screenWidth, int screenHeight) {
        int startY = 350;
        int spacing = 60;
        Font menuFont = loadCustomFont(28f).deriveFont(Font.PLAIN);

        for (int i = 0; i < options.length; i++) {
            int y = startY + i * spacing;
            if (i == selectedOption) {
                drawSelectedOption(g2, options[i], y, screenWidth);
            } else {
                g2.setColor(TEXT_COLOR);
                g2.setFont(menuFont);
                FontMetrics fm = g2.getFontMetrics();
                int x = (screenWidth - fm.stringWidth(options[i])) / 2;
                g2.drawString(options[i], x, y);
            }
        }
    }

    private void drawSelectedOption(Graphics2D g2, String text, int y, int screenWidth) {
        Font selectedFont = loadCustomFont(28f).deriveFont(Font.BOLD);
        g2.setFont(selectedFont);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (screenWidth - textWidth) / 2;

        RoundRectangle2D background = new RoundRectangle2D.Float(
                x - 20, y - 30, textWidth + 40, 40, 20, 20
        );

        g2.setColor(new Color(SELECTED_COLOR.getRed(), SELECTED_COLOR.getGreen(), SELECTED_COLOR.getBlue(), 50));
        g2.fill(background);

        g2.setColor(SELECTED_COLOR);
        g2.setStroke(new BasicStroke(2));
        g2.draw(background);

        g2.setColor(SELECTED_COLOR);
        g2.drawString(text, x, y);
    }

    /**
     * Vẽ Custom Store với ảnh ball thực tế
     */
    public void drawCustomStore(Graphics g, int screenWidth, int screenHeight, 
                                Color currentPaddleColor, Color currentBallColor,
                                String currentBallImagePath) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        drawBackground(g2, screenWidth, screenHeight);

        // Title
        g2.setColor(PRIMARY_COLOR);
        Font titleFont = loadCustomFont(48f).deriveFont(Font.BOLD);
        g2.setFont(titleFont);
        FontMetrics fm = g2.getFontMetrics();
        String title = "CUSTOM COLOR MENU";
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        g2.drawString(title, titleX, 100);

        int leftMargin = 150;
        int topMargin = 180;
        int boxSize = 80;
        int spacing = 20;
        int cols = 4;

        // === PADDLE SECTION ===
        g2.setColor(SELECTED_COLOR);
        g2.setFont(loadCustomFont(28f).deriveFont(Font.BOLD));
        g2.drawString("PADDLE COLOR", leftMargin, topMargin);

        for (int i = 0; i < PADDLE_COLORS.length; i++) {
            int row = i / cols;
            int col = i % cols;
            int x = leftMargin + col * (boxSize + spacing);
            int y = topMargin + 20 + row * (boxSize + spacing);

            paddleColorBoxes[i] = new Rectangle(x, y, boxSize, boxSize);

            g2.setColor(PADDLE_COLORS[i]);
            RoundRectangle2D box = new RoundRectangle2D.Float(x, y, boxSize, boxSize, 15, 15);
            g2.fill(box);

            if (PADDLE_COLORS[i].equals(currentPaddleColor)) {
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(4));
                g2.draw(box);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 12));
                FontMetrics smallFm = g2.getFontMetrics();
                String selected = "✓";
                int sx = x + (boxSize - smallFm.stringWidth(selected)) / 2;
                int sy = y + (boxSize + smallFm.getAscent()) / 2;
                g2.drawString(selected, sx, sy);
            } else {
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.draw(box);
            }
            
        }

        // === BALL SECTION ===
        int ballSectionY = topMargin + 240;
        g2.setColor(SELECTED_COLOR);
        g2.setFont(loadCustomFont(28f).deriveFont(Font.BOLD));
        g2.drawString("BALL SKIN", leftMargin, ballSectionY);

        for (int i = 0; i < BALL_IMAGE_PATHS.length; i++) {
            int row = i / cols;
            int col = i % cols;
            int x = leftMargin + col * (boxSize + spacing);
            int y = ballSectionY + 20 + row * (boxSize + spacing);

            ballColorBoxes[i] = new Rectangle(x, y, boxSize, boxSize);

            // Vẽ background box tối màu
            g2.setColor(new Color(30, 30, 50));
            RoundRectangle2D box = new RoundRectangle2D.Float(x, y, boxSize, boxSize, 15, 15);
            g2.fill(box);

            // Vẽ ảnh ball (chỉ hiển thị ảnh, không có fallback)
            Image ballImg = loadBallImage(i);
            if (ballImg != null) {
                int imgSize = 50;
                int imgX = x + (boxSize - imgSize) / 2;
                int imgY = y + (boxSize - imgSize) / 2;
                g2.drawImage(ballImg, imgX, imgY, imgSize, imgSize, null);
            }

            // Kiểm tra xem có phải ball đang chọn không
            boolean isSelected = (currentBallImagePath != null && 
                                currentBallImagePath.equals(BALL_IMAGE_PATHS[i]));

            if (isSelected) {
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new BasicStroke(4));
                g2.draw(box);
                
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                FontMetrics smallFm = g2.getFontMetrics();
                String selected = "✓";
                int sx = x + (boxSize - smallFm.stringWidth(selected)) / 2;
                int sy = y + boxSize - 10;
                g2.drawString(selected, sx, sy);
            } else {
                g2.setColor(new Color(255, 255, 255, 100));
                g2.setStroke(new BasicStroke(2));
                g2.draw(box);
            }
        }

        // === INSTRUCTIONS ===
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        g2.drawString("Click on a color/skin to select it", leftMargin, screenHeight - 80);
        g2.drawString("Press ESC to return to main menu", leftMargin, screenHeight - 50);
    }

    /**
     * Kiểm tra xem click có trúng ô màu paddle nào không
     */
    public int getPaddleColorBoxClicked(int mouseX, int mouseY) {
        for (int i = 0; i < paddleColorBoxes.length; i++) {
            if (paddleColorBoxes[i] != null && paddleColorBoxes[i].contains(mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Kiểm tra xem click có trúng ô màu ball nào không
     */
    public int getBallColorBoxClicked(int mouseX, int mouseY) {
        for (int i = 0; i < ballColorBoxes.length; i++) {
            if (ballColorBoxes[i] != null && ballColorBoxes[i].contains(mouseX, mouseY)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Lấy màu paddle theo index
     */
    public Color getPaddleColor(int index) {
        if (index >= 0 && index < PADDLE_COLORS.length) {
            return PADDLE_COLORS[index];
        }
        return Color.BLUE;
    }

    /**
     * Lấy màu ball theo index
     */
    public Color getBallColor(int index) {
        if (index >= 0 && index < BALL_COLORS.length) {
            return BALL_COLORS[index];
        }
        return Color.RED;
    }

    /**
     * Lấy đường dẫn ảnh ball theo index - PHƯƠNG THỨC THIẾU
     */
    public String getBallImagePath(int index) {
        if (index >= 0 && index < BALL_IMAGE_PATHS.length) {
            return BALL_IMAGE_PATHS[index];
        }
        return "/balls/ball_red.png"; // Default
    }

    private void drawFooterInstructions(Graphics2D g2, int screenWidth, int screenHeight) {
        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        String instruction = "Use ↑↓ or MOUSE to navigate, ENTER or CLICK to select, ESC to go back";
        FontMetrics fm = g2.getFontMetrics();
        int x = (screenWidth - fm.stringWidth(instruction)) / 2;
        int y = screenHeight - 30;
        g2.drawString(instruction, x, y);
    }

    public void drawSettings(Graphics g, boolean soundEnabled, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackground(g2, screenWidth, screenHeight);

        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String title = "SETTINGS";
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        int titleY = 150;
        g2.drawString(title, titleX, titleY);

        int y = 250;
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String soundText = "Sound: " + (soundEnabled ? "ON" : "OFF");
        g2.setColor(soundEnabled ? PRIMARY_COLOR : Color.GRAY);
        g2.drawString(soundText, 100, y);

        y += 60;
        g2.setColor(TEXT_COLOR);
       

        int indicatorX = 400;
        int indicatorY = y - 20;
       

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("Use LEFT/RIGHT to change difficulty", 100, y + 40);
        g2.drawString("Press ENTER to toggle sound", 100, y + 60);
        g2.drawString("Press ESC to return to main menu", 100, y + 80);
    }

    public void drawInstructions(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawBackground(g2, screenWidth, screenHeight);

        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g2.getFontMetrics();
        String title = "HOW TO PLAY";
        int titleX = (screenWidth - fm.stringWidth(title)) / 2;
        int titleY = 100;
        g2.drawString(title, titleX, titleY);

        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(TEXT_COLOR);

        String[] instructions = {
                "CONTROLS:",
                "• ← / → or MOUSE : Move Paddle",
                "• SPACE or CLICK : Launch Ball",
                "• 4 / 6 : Aim before launching",
                "• R : Restart Game",
                "• ESC : Pause/Resume",
                "",
                "POWER-UPS:",
                "• Green : Expand Paddle",
                "• Cyan : Fast Ball",
                "",
                "OBJECTIVE:",
                "• Break all bricks to advance to next level",
                "• Don't let the ball fall off the screen",
                "• Collect power-ups for advantages"
        };

        int y = titleY + 60;
        for (String instruction : instructions) {
            if (instruction.startsWith("•")) {
                g2.setColor(PRIMARY_COLOR);
            } else if (instruction.endsWith(":")) {
                g2.setColor(SELECTED_COLOR);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
            } else {
                g2.setColor(TEXT_COLOR);
                g2.setFont(new Font("Arial", Font.PLAIN, 20));
            }
            g2.drawString(instruction, 100, y);
            y += 30;
        }

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("Press ESC to return to main menu", screenWidth - 300, screenHeight - 50);
    }

    public void drawPauseOverlay(Graphics g, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String text = "PAUSED";
        int x = (screenWidth - fm.stringWidth(text)) / 2;
        int y = (screenHeight + fm.getAscent()) / 2 - 50;
        g2.drawString(text, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.setColor(Color.GRAY);
        String instruction = "Press ESC to resume";
        fm = g2.getFontMetrics();
        x = (screenWidth - fm.stringWidth(instruction)) / 2;
        y += 60;
        g2.drawString(instruction, x, y);
    }

    public void drawCountdown(Graphics g, int countdownValue, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 0, 0, 100));
        g2.fillRect(0, 0, screenWidth, screenHeight);

        String countdownText = String.valueOf(countdownValue);
        Font countdownFont = loadCustomFont(200f).deriveFont(Font.BOLD);
        g2.setFont(countdownFont);
        
        FontMetrics fm = g2.getFontMetrics();
        int x = (screenWidth - fm.stringWidth(countdownText)) / 2;
        int y = (screenHeight + fm.getAscent()) / 2;

        for (int i = 5; i >= 1; i--) {
            float alpha = 0.1f + 0.15f * (6 - i) / 5f;
            g2.setColor(new Color(PRIMARY_COLOR.getRed(), PRIMARY_COLOR.getGreen(), PRIMARY_COLOR.getBlue(), (int)(alpha * 255)));
            g2.setFont(countdownFont.deriveFont(200f + i * 10));
            
            FontMetrics glowFm = g2.getFontMetrics();
            int glowX = (screenWidth - glowFm.stringWidth(countdownText)) / 2;
            int glowY = (screenHeight + glowFm.getAscent()) / 2;
            g2.drawString(countdownText, glowX, glowY);
        }

        g2.setColor(PRIMARY_COLOR);
        g2.setFont(countdownFont);
        g2.drawString(countdownText, x, y);

        if (countdownValue == 3) {
            g2.setColor(TEXT_COLOR);
            g2.setFont(new Font("Arial", Font.BOLD, 36));
            fm = g2.getFontMetrics();
            String readyText = "GET READY!";
            int readyX = (screenWidth - fm.stringWidth(readyText)) / 2;
            int readyY = y - 100;
            g2.drawString(readyText, readyX, readyY);
        }
    }

    public void drawGameOver(Graphics g, int score, int screenWidth, int screenHeight) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2, screenWidth, screenHeight);

        g2.setColor(Color.RED);
        g2.setFont(new Font("Arial", Font.BOLD, 48));
        FontMetrics fm = g2.getFontMetrics();
        String text = "GAME OVER";
        int x = (screenWidth - fm.stringWidth(text)) / 2;
        int y = 200;
        g2.drawString(text, x, y);

        g2.setColor(TEXT_COLOR);
        g2.setFont(new Font("Arial", Font.PLAIN, 24));
        String scoreText = "Final Score: " + score;
        fm = g2.getFontMetrics();
        x = (screenWidth - fm.stringWidth(scoreText)) / 2;
        y += 80;
        g2.drawString(scoreText, x, y);

        g2.setColor(Color.GRAY);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        String instruction = "Press ENTER to return to main menu";
        fm = g2.getFontMetrics();
        x = (screenWidth - fm.stringWidth(instruction)) / 2;
        y += 100;
        g2.drawString(instruction, x, y);
    }

    private void drawBackground(Graphics2D g2, int screenWidth, int screenHeight) {
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, screenWidth, screenHeight);

        for (int i = 0; i < screenHeight; i++) {
            float ratio = (float) i / screenHeight;
            Color color = new Color(
                    (int) (BACKGROUND_COLOR.getRed() + (PRIMARY_COLOR.getRed() - BACKGROUND_COLOR.getRed()) * ratio * 0.1),
                    (int) (BACKGROUND_COLOR.getGreen() + (PRIMARY_COLOR.getGreen() - BACKGROUND_COLOR.getGreen()) * ratio * 0.1),
                    (int) (BACKGROUND_COLOR.getBlue() + (PRIMARY_COLOR.getBlue() - BACKGROUND_COLOR.getBlue()) * ratio * 0.1)
            );
            g2.setColor(color);
            g2.drawLine(0, i, screenWidth, i);
        }
    }
    
    public Rectangle getLeaderboardButtonBounds() {
        return new Rectangle(leaderboardButtonBounds);
    }
}