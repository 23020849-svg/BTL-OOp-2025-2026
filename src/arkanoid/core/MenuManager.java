package arkanoid.core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import arkanoid.view.MenuRenderer;
import arkanoid.utils.Sound;

/**
 * MenuManager.java
 * 
 * Quản lý menu chính của game Arkanoid với các tùy chọn:
 * - Start Game: Bắt đầu chơi game
 * - Settings: Cài đặt game
 * - Instructions: Hướng dẫn chơi
 * - Exit: Thoát game
 */
public class MenuManager extends JPanel implements ActionListener {
    
    public static final int WIDTH = 1440;
    public static final int HEIGHT = 800;
    
    // Các trạng thái menu
    public enum MenuState {
        MAIN_MENU,
        SETTINGS,
        INSTRUCTIONS,
        COUNTDOWN,
        GAME,
        PAUSED,
        GAME_OVER
    }
    
    private MenuState currentState = MenuState.MAIN_MENU;
    private MenuRenderer menuRenderer;
    private GameManager gameManager;
    private Timer timer;
    private Sound selectingSound; // Âm thanh khi chọn menu
    
    // Menu options
    private String[] mainMenuOptions = {"Start Game", "Settings", "Instructions", "Exit"};
    private int selectedOption = 0;
    
    // Settings
    private boolean soundEnabled = true;
    private int difficulty = 1; // 1: Easy, 2: Medium, 3: Hard
    private String[] difficultyNames = {"Easy", "Medium", "Hard"};
    
    // Countdown variables
    private int countdownValue = 3;
    private long countdownStartTime;
    private static final long COUNTDOWN_DURATION = 1000; // 1 second per count
    
    public MenuManager() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setOpaque(false);
        
        menuRenderer = new MenuRenderer();
        gameManager = new GameManager();
        
        // Khởi tạo âm thanh
        selectingSound = new Sound();
        selectingSound.loadSound("/selecting.wav");
        
        initKeyBindings();
        initMouseListeners();
        
        timer = new Timer(16, this);
        timer.start();
    }
    
    private void initKeyBindings() {
        // Navigation keys
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
        getActionMap().put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.MAIN_MENU) {
                    selectedOption = (selectedOption - 1 + mainMenuOptions.length) % mainMenuOptions.length;
                    if (soundEnabled) selectingSound.playOnce();
                } else if (currentState == MenuState.SETTINGS) {
                    // Navigate settings options
                }
            }
        });
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
        getActionMap().put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.MAIN_MENU) {
                    selectedOption = (selectedOption + 1) % mainMenuOptions.length;
                    if (soundEnabled) selectingSound.playOnce();
                } else if (currentState == MenuState.SETTINGS) {
                    // Navigate settings options
                }
            }
        });
        
        // Enter key
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
        getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEnterKey();
            }
        });
        
        // Escape key
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEscapeKey();
            }
        });
        
        // Left/Right for settings and game
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        getActionMap().put("left_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.SETTINGS) {
                    if (difficulty > 1) difficulty--;
                } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED || currentState == MenuState.COUNTDOWN) {
                    // Call game manager's left movement
                    gameManager.getPaddle().setMovingLeft(true);
                }
            }
        });
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        getActionMap().put("left_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED || currentState == MenuState.COUNTDOWN) {
                    gameManager.getPaddle().setMovingLeft(false);
                }
            }
        });
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed RIGHT"), "right_pressed");
        getActionMap().put("right_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.SETTINGS) {
                    if (difficulty < 3) difficulty++;
                } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED || currentState == MenuState.COUNTDOWN) {
                    gameManager.getPaddle().setMovingRight(true);
                }
            }
        });
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        getActionMap().put("right_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED || currentState == MenuState.COUNTDOWN) {
                    gameManager.getPaddle().setMovingRight(false);
                }
            }
        });
        
        // Space key for game
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
        getActionMap().put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
                    // Launch ball if not already launched
                    if (!gameManager.isBallLaunched()) {
                        gameManager.launchBall();
                    }
                }
            }
        });
        
        // R key for restart
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
                    gameManager.initGame();
                }
            }
        });
        
        // 4 and 6 keys for aiming
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("4"), "aim_left");
        getActionMap().put("aim_left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
                    gameManager.adjustLaunchAngle(-5);
                }
            }
        });
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("6"), "aim_right");
        getActionMap().put("aim_right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
                    gameManager.adjustLaunchAngle(5);
                }
            }
        });
    }
    
    private void initMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e);
            }
        });
        
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                handleMouseMove(e);
            }
        });
    }
    
    private void handleMouseClick(MouseEvent e) {
        if (currentState == MenuState.MAIN_MENU) {
            // Kiểm tra xem click có nằm trong vùng menu options không
            int startY = 350;
            int spacing = 60;
            int clickY = e.getY();
            
            for (int i = 0; i < mainMenuOptions.length; i++) {
                int optionY = startY + i * spacing;
                if (clickY >= optionY - 30 && clickY <= optionY + 10) {
                    selectedOption = i;
                    handleEnterKey();
                    break;
                }
            }
        } else if (currentState == MenuState.SETTINGS) {
            // Click để toggle sound
            soundEnabled = !soundEnabled;
        } else if (currentState == MenuState.INSTRUCTIONS) {
            // Click để quay về main menu
            currentState = MenuState.MAIN_MENU;
        } else if (currentState == MenuState.GAME_OVER) {
            // Click để quay về main menu
            currentState = MenuState.MAIN_MENU;
        } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
            // Click để bắn bóng nếu chưa bắn
            if (!gameManager.isBallLaunched()) {
                gameManager.launchBall();
            }
        }
    }
    
    private void handleMouseMove(MouseEvent e) {
        if (currentState == MenuState.MAIN_MENU) {
            // Highlight menu option khi hover
            int startY = 350;
            int spacing = 60;
            int mouseY = e.getY();
            
            for (int i = 0; i < mainMenuOptions.length; i++) {
                int optionY = startY + i * spacing;
                if (mouseY >= optionY - 30 && mouseY <= optionY + 10) {
                    int oldSelected = selectedOption;
                    selectedOption = i;
                    // Phát âm thanh nếu thay đổi lựa chọn và có bật sound
                    if (oldSelected != selectedOption && soundEnabled) {
                        selectingSound.playOnce();
                    }
                    break;
                }
            }
        } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED || currentState == MenuState.COUNTDOWN) {
            // Điều khiển paddle bằng chuột
            int mouseX = e.getX();
            int paddleWidth = gameManager.getPaddle().getWidth();
            int newPaddleX = mouseX - paddleWidth / 2;
            
            // Giới hạn paddle trong màn hình
            if (newPaddleX < 0) newPaddleX = 0;
            if (newPaddleX > WIDTH - paddleWidth) newPaddleX = WIDTH - paddleWidth;
            
            gameManager.getPaddle().setX(newPaddleX);
            
            // Căn chỉnh bóng theo paddle nếu chưa bắn
            if (!gameManager.isBallLaunched()) {
                gameManager.alignBallToPaddle();
            }
        }
    }
    
    private void handleEnterKey() {
        switch (currentState) {
            case MAIN_MENU:
                switch (selectedOption) {
                    case 0: // Start Game
                        startGame();
                        break;
                    case 1: // Settings
                        currentState = MenuState.SETTINGS;
                        break;
                    case 2: // Instructions
                        currentState = MenuState.INSTRUCTIONS;
                        break;
                    case 3: // Exit
                        System.exit(0);
                        break;
                }
                break;
            case SETTINGS:
                // Toggle sound
                soundEnabled = !soundEnabled;
                break;
            case INSTRUCTIONS:
                currentState = MenuState.MAIN_MENU;
                break;
            case GAME_OVER:
                currentState = MenuState.MAIN_MENU;
                break;
            case COUNTDOWN:
            case GAME:
            case PAUSED:
                // No action for these states
                break;
        }
    }
    
    private void handleEscapeKey() {
        switch (currentState) {
            case SETTINGS:
            case INSTRUCTIONS:
                currentState = MenuState.MAIN_MENU;
                break;
            case GAME:
                currentState = MenuState.PAUSED;
                break;
            case PAUSED:
                currentState = MenuState.GAME;
                break;
            case COUNTDOWN:
            case MAIN_MENU:
            case GAME_OVER:
                // No action for these states
                break;
        }
    }
    
    private void startGame() {
        currentState = MenuState.COUNTDOWN;
        countdownValue = 3;
        countdownStartTime = System.currentTimeMillis();
        gameManager.initGame();
        // Make sure MenuManager keeps focus but forwards input to game
        setFocusable(true);
        requestFocusInWindow();
    }
    
    public void gameOver() {
        currentState = MenuState.GAME_OVER;
    }
    
    private long lastUpdateTime = System.currentTimeMillis();

    @Override
    public void actionPerformed(ActionEvent e) {
        // Tính Delta Time
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0; // Đổi sang giây
        lastUpdateTime = currentTime; // Lưu lại thời gian cho các lần lặp sau

        if (currentState == MenuState.COUNTDOWN) {
            // Handle countdown logic
            long elapsed = currentTime - countdownStartTime;
            int newCountdownValue = 3 - (int)(elapsed / COUNTDOWN_DURATION);
            
            if (newCountdownValue != countdownValue) {
                countdownValue = newCountdownValue;
            }
            
            // Update paddle during countdown so player can position it
            gameManager.getPaddle().update(deltaTime);
            if (!gameManager.isBallLaunched()) {
                gameManager.alignBallToPaddle();
            }
            
            // When countdown reaches 0, start the game
            if (countdownValue <= 0) {
                currentState = MenuState.GAME;
                // Auto-launch ball if it's the first life
                if (gameManager.isFirstLife() && !gameManager.isBallLaunched()) {
                    gameManager.launchBall();
                }
            }
        } else if (currentState == MenuState.GAME) {
            // Truyền deltaTime vào GameManager
            gameManager.updateGame(deltaTime);
        } else if (currentState == MenuState.PAUSED) {
            // Don't update game when paused, just repaint
        }
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        switch (currentState) {
            case MAIN_MENU:
                menuRenderer.drawMainMenu(g, mainMenuOptions, selectedOption);
                break;
            case SETTINGS:
                menuRenderer.drawSettings(g, soundEnabled, difficulty, difficultyNames);
                break;
            case INSTRUCTIONS:
                menuRenderer.drawInstructions(g);
                break;
            case COUNTDOWN:
                gameManager.paintComponent(g);
                menuRenderer.drawCountdown(g, countdownValue);
                break;
            case GAME:
                gameManager.paintComponent(g);
                break;
            case PAUSED:
                gameManager.paintComponent(g);
                menuRenderer.drawPauseOverlay(g);
                break;
            case GAME_OVER:
                menuRenderer.drawGameOver(g, gameManager.getScore());
                break;
        }
    }
    
    // Getters for game state
    public boolean isInGame() {
        return currentState == MenuState.GAME;
    }
    
    public boolean isPaused() {
        return currentState == MenuState.PAUSED;
    }
    
    public GameManager getGameManager() {
        return gameManager;
    }
}
