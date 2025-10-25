package arkanoid.core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import arkanoid.view.MenuRenderer;

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
        GAME,
        PAUSED,
        GAME_OVER
    }
    
    private MenuState currentState = MenuState.MAIN_MENU;
    private MenuRenderer menuRenderer;
    private GameManager gameManager;
    private Timer timer;
    
    // Menu options
    private String[] mainMenuOptions = {"Start Game", "Settings", "Instructions", "Exit"};
    private int selectedOption = 0;
    
    // Settings
    private boolean soundEnabled = true;
    private int difficulty = 1; // 1: Easy, 2: Medium, 3: Hard
    private String[] difficultyNames = {"Easy", "Medium", "Hard"};
    
    public MenuManager() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setOpaque(false);
        
        menuRenderer = new MenuRenderer();
        gameManager = new GameManager();
        
        initKeyBindings();
        
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
                } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
                    // Call game manager's left movement
                    gameManager.getPaddle().setMovingLeft(true);
                }
            }
        });
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        getActionMap().put("left_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
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
                } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
                    gameManager.getPaddle().setMovingRight(true);
                }
            }
        });
        
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        getActionMap().put("right_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
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
            case MAIN_MENU:
            case GAME_OVER:
                // No action for these states
                break;
        }
    }
    
    private void startGame() {
        currentState = MenuState.GAME;
        gameManager.initGame();
        // Make sure MenuManager keeps focus but forwards input to game
        setFocusable(true);
        requestFocusInWindow();
    }
    
    public void gameOver() {
        currentState = MenuState.GAME_OVER;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentState == MenuState.GAME) {
            gameManager.actionPerformed(e);
            // Check if game is over
            if (!gameManager.isRunning() && gameManager.getLives() <= 0) {
                gameOver();
            }
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
