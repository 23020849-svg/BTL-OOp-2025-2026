// ==================== MenuManager.java (FIXED - HOÀN CHỈNH) ====================
package arkanoid.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import arkanoid.utils.Sound;
import arkanoid.view.LeaderboardDialog;
import arkanoid.view.MenuRenderer;

public class MenuManager extends JPanel implements ActionListener {

    public static final int WIDTH = 1440;
    public static final int HEIGHT = 800;

    // Các trạng thái menu
    public enum MenuState {
        MAIN_MENU,
        CUSTOM,
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
    private Sound selectingSound;

    // Biến để quản lý fullscreen
    private JFrame mainFrame;
    private JLabel backgroundLabel;
    private Image originalBackgroundImg;
    private boolean isFullScreen = false;

    // Menu options
    private String[] mainMenuOptions = { "Start Game", "Custom", "Settings", "Instructions", "Leaderboard", "Exit" };
    private int selectedOption = 0;

    // Settings
    private boolean soundEnabled = true;
    private int difficulty = 1;
    private String[] difficultyNames = { "Easy", "Medium", "Hard" };

    // Countdown variables
    private int countdownValue = 3;
    private long countdownStartTime;
    private static final long COUNTDOWN_DURATION = 1000;

    // Customization
    private Color paddleColor = Color.BLUE;
    private Color ballColor = Color.RED;
    private String ballImagePath = "/balls/ball_red.png";

    public MenuManager(JFrame frame, JLabel background, Image originalImg) {
        this.mainFrame = frame;
        this.backgroundLabel = background;
        this.originalBackgroundImg = originalImg;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setOpaque(false);

        menuRenderer = new MenuRenderer();
        gameManager = new GameManager();
        gameManager.setPaddleColor(paddleColor);
        gameManager.setBallColor(ballColor);
        gameManager.setBallImagePath(ballImagePath);

        selectingSound = new Sound();
        selectingSound.loadSound("/selecting.wav");

        initKeyBindings();
        initMouseListeners();

        timer = new Timer(16, this);
        timer.start();
    }

    private void toggleFullScreen() {
        isFullScreen = !isFullScreen;
        mainFrame.dispose();

        if (isFullScreen) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Image scaled = originalBackgroundImg.getScaledInstance(screenSize.width, screenSize.height,
                    Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaled));
            backgroundLabel.setLayout(new BorderLayout());

            mainFrame.setContentPane(backgroundLabel);
            backgroundLabel.add(this, BorderLayout.CENTER);

            mainFrame.setUndecorated(true);
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

            if (currentState == MenuState.GAME || currentState == MenuState.PAUSED
                    || currentState == MenuState.COUNTDOWN) {
                gameManager.rescaleGame(screenSize.width, screenSize.height);
            }
        } else {
            Image scaled = originalBackgroundImg.getScaledInstance(MenuManager.WIDTH, MenuManager.HEIGHT,
                    Image.SCALE_SMOOTH);
            backgroundLabel.setIcon(new ImageIcon(scaled));
            backgroundLabel.setLayout(new BorderLayout());

            mainFrame.setContentPane(backgroundLabel);
            backgroundLabel.add(this, BorderLayout.CENTER);

            mainFrame.setUndecorated(false);
            mainFrame.setExtendedState(JFrame.NORMAL);

            mainFrame.pack();
            mainFrame.setLocationRelativeTo(null);

            if (currentState == MenuState.GAME || currentState == MenuState.PAUSED
                    || currentState == MenuState.COUNTDOWN) {
                gameManager.rescaleGame(MenuManager.WIDTH, MenuManager.HEIGHT);
            }
        }

        mainFrame.setVisible(true);
        requestFocusInWindow();
    }

    private void initKeyBindings() {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("UP"), "up");
        getActionMap().put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.MAIN_MENU) {
                    selectedOption = (selectedOption - 1 + mainMenuOptions.length) % mainMenuOptions.length;
                    if (soundEnabled) selectingSound.playOnce();
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
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
        getActionMap().put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEnterKey();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
        getActionMap().put("escape", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleEscapeKey();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("pressed LEFT"), "left_pressed");
        getActionMap().put("left_pressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.SETTINGS) {
                    if (difficulty > 1) difficulty--;
                } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED
                        || currentState == MenuState.COUNTDOWN) {
                    gameManager.getPaddle().setMovingLeft(true);
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released LEFT"), "left_released");
        getActionMap().put("left_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED
                        || currentState == MenuState.COUNTDOWN) {
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
                } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED
                        || currentState == MenuState.COUNTDOWN) {
                    gameManager.getPaddle().setMovingRight(true);
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released RIGHT"), "right_released");
        getActionMap().put("right_released", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED
                        || currentState == MenuState.COUNTDOWN) {
                    gameManager.getPaddle().setMovingRight(false);
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("SPACE"), "space");
        getActionMap().put("space", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
                    if (!gameManager.isBallLaunched()) {
                        gameManager.launchBall();
                    }
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("R"), "restart");
        getActionMap().put("restart", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
                    gameManager.initGame();
                }
            }
        });

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

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F11"), "toggle_fullscreen");
        getActionMap().put("toggle_fullscreen", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleFullScreen();
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("P"), "choose_paddle_color");
        getActionMap().put("choose_paddle_color", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.CUSTOM) {
                    Color newColor = JColorChooser.showDialog(MenuManager.this, "Choose Paddle Color", paddleColor);
                    if (newColor != null) {
                        paddleColor = newColor;
                        gameManager.setPaddleColor(newColor);
                    }
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("B"), "choose_ball_color");
        getActionMap().put("choose_ball_color", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.CUSTOM) {
                    Color newColor = JColorChooser.showDialog(MenuManager.this, "Choose Ball Color", ballColor);
                    if (newColor != null) {
                        ballColor = newColor;
                        gameManager.setBallColor(newColor);
                    }
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
        } 
        else if (currentState == MenuState.CUSTOM) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            
            // Kiểm tra click vào ô màu paddle
            int paddleIndex = menuRenderer.getPaddleColorBoxClicked(mouseX, mouseY);
            if (paddleIndex >= 0) {
                paddleColor = menuRenderer.getPaddleColor(paddleIndex);
                gameManager.setPaddleColor(paddleColor);
                if (soundEnabled) selectingSound.playOnce();
                return;
            }
            
            // Kiểm tra click vào ô ảnh ball
            int ballIndex = menuRenderer.getBallColorBoxClicked(mouseX, mouseY);
            if (ballIndex >= 0) {
                ballImagePath = menuRenderer.getBallImagePath(ballIndex);
                ballColor = menuRenderer.getBallColor(ballIndex);
                gameManager.setBallImagePath(ballImagePath);
                gameManager.setBallColor(ballColor);
                if (soundEnabled) selectingSound.playOnce();
                return;
            }
        }
        else if (currentState == MenuState.SETTINGS) {
            soundEnabled = !soundEnabled;
        } 
        else if (currentState == MenuState.INSTRUCTIONS) {
            currentState = MenuState.MAIN_MENU;
        } 
        else if (currentState == MenuState.GAME_OVER) {
            currentState = MenuState.MAIN_MENU;
        } 
        else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED) {
            if (!gameManager.isBallLaunched()) {
                gameManager.launchBall();
            }
        }
    }

    private void handleMouseMove(MouseEvent e) {
        if (currentState == MenuState.MAIN_MENU) {
            int startY = 350;
            int spacing = 60;
            int mouseY = e.getY();

            for (int i = 0; i < mainMenuOptions.length; i++) {
                int optionY = startY + i * spacing;
                if (mouseY >= optionY - 30 && mouseY <= optionY + 10) {
                    int oldSelected = selectedOption;
                    selectedOption = i;
                    if (oldSelected != selectedOption && soundEnabled) {
                        selectingSound.playOnce();
                    }
                    break;
                }
            }
        } else if (currentState == MenuState.GAME || currentState == MenuState.PAUSED
                || currentState == MenuState.COUNTDOWN) {
            int mouseX = e.getX();
            int paddleWidth = gameManager.getPaddle().getWidth();
            int newPaddleX = mouseX - paddleWidth / 2;

            if (newPaddleX < 0) newPaddleX = 0;
            if (newPaddleX > getWidth() - paddleWidth) newPaddleX = getWidth() - paddleWidth;

            gameManager.getPaddle().setX(newPaddleX);

            if (!gameManager.isBallLaunched()) {
                gameManager.alignBallToPaddle();
            }
        }
    }

    private void handleEnterKey() {
        switch (currentState) {
            case MAIN_MENU:
                switch (selectedOption) {
                    case 0: startGame(); break;
                    case 1: currentState = MenuState.CUSTOM; break;
                    case 2: currentState = MenuState.SETTINGS; break;
                    case 3: currentState = MenuState.INSTRUCTIONS; break;
                    case 4: showLeaderboard(); break;
                    case 5: System.exit(0); break;
                }
                break;
            case SETTINGS:
                soundEnabled = !soundEnabled;
                break;
            case INSTRUCTIONS:
            case GAME_OVER:
                currentState = MenuState.MAIN_MENU;
                break;
            case CUSTOM:
                currentState = MenuState.MAIN_MENU;
                break;
            default:
                break;
        }
    }

    private void handleEscapeKey() {
        switch (currentState) {
            case SETTINGS:
            case CUSTOM:
            case INSTRUCTIONS:
                currentState = MenuState.MAIN_MENU;
                break;
            case GAME:
                currentState = MenuState.PAUSED;
                break;
            case PAUSED:
                currentState = MenuState.GAME;
                break;
            default:
                break;
        }
    }

    private void startGame() {
        currentState = MenuState.COUNTDOWN;
        countdownValue = 3;
        countdownStartTime = System.currentTimeMillis();
        gameManager.initGame();
        setFocusable(true);
        requestFocusInWindow();
    }

    private void showLeaderboard() {
        timer.stop();
        Window parent = SwingUtilities.getWindowAncestor(this);
        LeaderboardDialog.showTop(parent, 10);
        timer.start();
        requestFocusInWindow();
    }

    public void gameOver() {
        currentState = MenuState.GAME_OVER;
    }

    private long lastUpdateTime = System.currentTimeMillis();

    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        double deltaTime = (currentTime - lastUpdateTime) / 1000.0;
        lastUpdateTime = currentTime;

        if (currentState == MenuState.COUNTDOWN) {
            long elapsed = currentTime - countdownStartTime;
            int newCountdownValue = 3 - (int) (elapsed / COUNTDOWN_DURATION);

            if (newCountdownValue != countdownValue) {
                countdownValue = newCountdownValue;
            }

            gameManager.getPaddle().update(deltaTime, getWidth());
            gameManager.updateGame(deltaTime, getWidth(), getHeight());
            if (!gameManager.isBallLaunched()) {
                gameManager.alignBallToPaddle();
            }

            if (countdownValue <= 0) {
                currentState = MenuState.GAME;
                if (gameManager.isFirstLife() && !gameManager.isBallLaunched()) {
                    gameManager.launchBall();
                }
            }
        } else if (currentState == MenuState.GAME) {
            gameManager.updateGame(deltaTime, getWidth(), getHeight());
            if (gameManager.isGameOver()) {
                gameOver();
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int w = getWidth();
        int h = getHeight();

        switch (currentState) {
            case MAIN_MENU:
                menuRenderer.drawMainMenu(g, mainMenuOptions, selectedOption, w, h);
                break;
            case CUSTOM:
                menuRenderer.drawCustomStore(g, w, h, paddleColor, ballColor, ballImagePath);
                break;
            case SETTINGS:
                menuRenderer.drawSettings(g, soundEnabled, difficulty, difficultyNames, w, h);
                break;
            case INSTRUCTIONS:
                menuRenderer.drawInstructions(g, w, h);
                break;
            case COUNTDOWN:
                gameManager.paintComponent(g, w, h);
                menuRenderer.drawCountdown(g, countdownValue, w, h);
                break;
            case GAME:
                gameManager.paintComponent(g, w, h);
                break;
            case PAUSED:
                gameManager.paintComponent(g, w, h);
                menuRenderer.drawPauseOverlay(g, w, h);
                break;
            case GAME_OVER:
                menuRenderer.drawGameOver(g, gameManager.getScore(), w, h);
                break;
        }
    }

    public Color getPaddleColor() {
        return paddleColor;
    }

    public Color getBallColor() {
        return ballColor;
    }

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