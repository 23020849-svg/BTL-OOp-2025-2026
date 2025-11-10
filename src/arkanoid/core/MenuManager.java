package arkanoid.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import arkanoid.utils.Sound;
import arkanoid.view.LeaderboardDialog;
import arkanoid.view.LevelSelectDialog;
import arkanoid.view.MenuRenderer;
import arkanoid.view.PauseMenuDialog;

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
    private final Timer timer;
    private Sound selectingSound;
    private java.awt.Image pauseIcon;


    // Biến để quản lý fullscreen
    private JFrame mainFrame;
    private JLabel backgroundLabel;
    private Image originalBackgroundImg;
    private boolean isFullScreen = false;

    // Menu options
    private String[] mainMenuOptions = { "Continue", "New Game", "Level Select", "Custom", "Settings", "Instructions",
            "Leaderboard", "Exit" };
    private int selectedOption = 0;

    // Settings
    private boolean soundEnabled = true;

    // Countdown variables
    private int countdownValue = 3;
    private long countdownStartTime;
    private static final long COUNTDOWN_DURATION = 1000;

    // Customization
    private Color paddleColor = Color.BLUE;
    private Color ballColor = Color.RED;
    private String ballImagePath = "/balls/ball_red.png";

    // ===== NÚT RETURN =====
    private ReturnButton returnButton;
    private Image returnButtonIconImage;

    // pause
    private final PauseButton pauseButton;

    // track menu
    private boolean hasSavedGame = false;

    public MenuManager(JFrame frame, JLabel background, Image originalImg) {
        this.mainFrame = frame;
        this.backgroundLabel = background;
        this.originalBackgroundImg = originalImg;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        setOpaque(false);

        menuRenderer = new MenuRenderer();
        gameManager = new GameManager();
        pauseButton = new PauseButton();

        gameManager.setPaddleColor(paddleColor);
        gameManager.setBallColor(ballColor);
        gameManager.setBallImagePath(ballImagePath);
        startAssetLoader();
        updateMenuOptions();
        initKeyBindings();
        initMouseListeners();

        timer = new Timer(16, this);
        timer.start();
    }

    // Phải là volatile để đảm bảo các luồng thấy thay đổi của nhau
    private volatile boolean logicRunning = false;
    private Thread logicThread;

    private void startLogicThread() {
        logicRunning = true;
        logicThread = new Thread(() -> {
            long lastTime = System.currentTimeMillis();

            while (logicRunning) {
                long now = System.currentTimeMillis();
                double deltaTime = (now - lastTime) / 1000.0;
                lastTime = now;

                if (currentState == MenuState.GAME || currentState == MenuState.COUNTDOWN) {
                    gameManager.updateGame(deltaTime, getWidth(), getHeight());
                    if (gameManager.isGameOver()) {
                        gameOver();
                    }
                }

                // Đặt sleep bên trong vòng lặp while
                try {
                    Thread.sleep(5); // Tránh chiếm CPU 100%
                } catch (InterruptedException ignored) {
                    // Nếu luồng bị ngắt, cũng nên dừng lại
                    logicRunning = false;
                }
            }
        });

        logicThread.start();
    }

    private void stopLogicThread() {
        logicRunning = false; // Gửi tín hiệu dừng

        if (logicThread != null && logicThread.isAlive()) {
            try {
                // Đợi cho logicThread thực sự kết thúc
                logicThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private volatile boolean assetsLoaded = false;
    private volatile boolean loadingAssets = false;
    private Thread assetLoaderThread;

    private void startAssetLoader() {
        loadingAssets = true;

        assetLoaderThread = new Thread(() -> {
            System.out.println("[AssetLoader] Bắt đầu tải...");
            try {
                selectingSound = new Sound();
                selectingSound.loadSound("/selecting.wav");
                getGameManager().loadGameManagerResource();

                try {
                    returnButtonIconImage = new ImageIcon(getClass().getResource("/return.png")).getImage();
                } catch (Exception e) {
                    System.err.println("Không tìm thấy /return.png");
                }

                try {
                pauseIcon = new javax.swing.ImageIcon(
                        getClass().getResource("/pausebutton.png")).getImage();
            } catch (Exception e) {
                System.err.println("Không load được /pausebutton.png");
            }
                System.out.println("[AssetLoader] Tải xong!");

                SwingUtilities.invokeLater(() -> {
                    assetsLoaded = true;
                    loadingAssets = false;
                    repaint();
                });

            } catch (Exception e) {
                System.err.println("[AssetLoader] Lỗi khi tải tài nguyên: " + e.getMessage());
                loadingAssets = false;
            }
        }, "AssetLoaderThread");

        assetLoaderThread.start();
    }

    private void updateMenuOptions() {
        hasSavedGame = gameManager.hasSavedGame();

        if (hasSavedGame) {
            mainMenuOptions = new String[] {
                    "Continue",
                    "New Game",
                    "Level Select",
                    "Custom",
                    "Settings",
                    "Instructions",
                    "Leaderboard",
                    "Exit"
            };
        } else {
            mainMenuOptions = new String[] {
                    "Start Game",
                    "Level Select",
                    "Custom",
                    "Settings",
                    "Instructions",
                    "Leaderboard",
                    "Exit"
            };
        }
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
                    if (soundEnabled)
                        selectingSound.playOnce();
                }
            }
        });

        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("DOWN"), "down");
        getActionMap().put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentState == MenuState.MAIN_MENU) {
                    selectedOption = (selectedOption + 1) % mainMenuOptions.length;
                    if (soundEnabled)
                        selectingSound.playOnce();
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

                } else if (currentState == MenuState.GAME || currentState == MenuState.COUNTDOWN) {
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

                } else if (currentState == MenuState.GAME || currentState == MenuState.COUNTDOWN) {
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
                if (currentState == MenuState.GAME) {
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
            public void mousePressed(MouseEvent e) {
                if (pauseButton != null && pauseButton.contains(e.getX(), e.getY())) {
                    pauseButton.setPressed(true);
                }

                if (returnButton != null) {
                    returnButton.mousePressed(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (pauseButton != null) {
                    pauseButton.setPressed(false);
                }
                if (returnButton != null) {
                    returnButton.mouseReleased(e);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (returnButton != null) {
                    returnButton.mouseMoved(e);
                }
                handleMouseMove(e);
            }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        if (pauseButton != null && pauseButton.contains(e.getX(), e.getY())) {
            if (currentState == MenuState.GAME) {
                showPauseMenu();
                return;
            }
        }
        if (currentState != null)
            switch (currentState) {
                case MAIN_MENU -> {
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
                case CUSTOM -> {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
                    int paddleIndex = menuRenderer.getPaddleColorBoxClicked(mouseX, mouseY);
                    if (paddleIndex >= 0) {
                        paddleColor = menuRenderer.getPaddleColor(paddleIndex);
                        gameManager.setPaddleColor(paddleColor);
                        if (soundEnabled)
                            selectingSound.playOnce();
                        return;
                    }
                    int ballIndex = menuRenderer.getBallColorBoxClicked(mouseX, mouseY);
                    if (ballIndex >= 0) {
                        ballImagePath = menuRenderer.getBallImagePath(ballIndex);
                        ballColor = menuRenderer.getBallColor(ballIndex);
                        gameManager.setBallImagePath(ballImagePath);
                        gameManager.setBallColor(ballColor);
                        if (soundEnabled)
                            selectingSound.playOnce();

                    }
                }
                case SETTINGS -> soundEnabled = !soundEnabled;
                case INSTRUCTIONS -> currentState = MenuState.MAIN_MENU;
                case GAME_OVER -> currentState = MenuState.MAIN_MENU;
                case GAME, PAUSED -> {
                    if (!gameManager.isBallLaunched()) {
                        gameManager.launchBall();
                    }
                }
                default -> {
                }
            }
    }

    private void handleMouseMove(MouseEvent e) {

        if (pauseButton != null) {
            pauseButton.setHovered(pauseButton.contains(e.getX(), e.getY()));
        }

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

            if (newPaddleX < 0)
                newPaddleX = 0;
            if (newPaddleX > getWidth() - paddleWidth)
                newPaddleX = getWidth() - paddleWidth;

            gameManager.getPaddle().setX(newPaddleX);

            if (!gameManager.isBallLaunched()) {
                gameManager.alignBallToPaddle();
            }
        }
    }

    private void handleEnterKey() {
        switch (currentState) {
            case MAIN_MENU -> {
                if (hasSavedGame) {
                    // Menu có Continue
                    switch (selectedOption) {
                        case 0 -> continueGame();
                        case 1 -> confirmNewGame();
                        case 2 -> showLevelSelect();
                        case 3 -> {
                            currentState = MenuState.CUSTOM;
                            createReturnButton();
                        }
                        case 4 -> {
                            currentState = MenuState.SETTINGS;
                            createReturnButton();
                        }
                        case 5 -> {
                            currentState = MenuState.INSTRUCTIONS;
                            createReturnButton();
                        }
                        case 6 -> showLeaderboard();
                        case 7 -> System.exit(0);
                    }
                } else {
                    // Menu không có Continue
                    switch (selectedOption) {
                        case 0 -> startGame();
                        case 1 -> showLevelSelect();
                        case 2 -> {
                            currentState = MenuState.CUSTOM;
                            createReturnButton();
                        }
                        case 3 -> {
                            currentState = MenuState.SETTINGS;
                            createReturnButton();
                        }
                        case 4 -> {
                            currentState = MenuState.INSTRUCTIONS;
                            createReturnButton();
                        }
                        case 5 -> showLeaderboard();
                        case 6 -> System.exit(0);
                    }
                }
            }

            case SETTINGS -> soundEnabled = !soundEnabled;

            case INSTRUCTIONS, GAME_OVER, CUSTOM -> returnToMainMenu();
            default -> {
            }
        }
    }

    private void continueGame() {
        if (gameManager.loadGame()) {
            currentState = MenuState.COUNTDOWN;
            returnButton = null;
            setFocusable(true);
            requestFocusInWindow();

            if (soundEnabled) {
                selectingSound.playOnce();
            }
            startGame();
            System.out.println("Continuing saved game...");
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Không thể load game đã lưu!\nBắt đầu game mới.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            startGame();
        }
    }

    private void confirmNewGame() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Bạn có game đã lưu.\n" +
                        "Bắt đầu game mới sẽ XÓA game đã lưu!\n\n" +
                        "Tiếp tục?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            gameManager.deleteSavedGame();
            startGame();
        }
    }

    private void showLevelSelect() {
        timer.stop();

        Window parent = SwingUtilities.getWindowAncestor(this);
        int selectedLevel = LevelSelectDialog.showDialog(parent);

        if (selectedLevel > 0) {
            // Người chơi đã chọn level
            gameManager.startFromLevel(selectedLevel);
            currentState = MenuState.COUNTDOWN;
            countdownValue = 3;
            countdownStartTime = System.currentTimeMillis();
            returnButton = null;
        }

        timer.start();
        requestFocusInWindow();
    }

    private void handleEscapeKey() {
        switch (currentState) {
            case SETTINGS:
            case CUSTOM:
            case INSTRUCTIONS:
                returnToMainMenu();
                break;
            case GAME:
                showPauseMenu();
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
        returnButton = null; // TẮT nút return khi vào game

        startLogicThread(); // Bắt đầu luồng logic game
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

    private void returnToMainMenu() {
        currentState = MenuState.MAIN_MENU;
        returnButton = null; // TẮT nút return
        updateMenuOptions();
        repaint();
    }

    private void createReturnButton() {
        int btnSize = 50;
        returnButton = new ReturnButton(10, 10, btnSize, btnSize, () -> returnToMainMenu(), returnButtonIconImage);
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
            // gameManager.updateGame(deltaTime, getWidth(), getHeight()); chuyển qua thread
            // rồi
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
            // gameManager.updateGame(deltaTime, getWidth(), getHeight()); đã được chuyển
            // qua thread
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
                menuRenderer.drawSettings(g, soundEnabled, w, h);
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

        if (returnButton != null) {
            returnButton.paint((Graphics2D) g);
        }
        if (returnButton != null) {
            returnButton.paint((Graphics2D) g);
        }

        // VẼ PAUSE BUTTON
        if (pauseButton != null) {
            pauseButton.paint((Graphics2D) g);
        }
    }

    private class ReturnButton {
        Rectangle bounds;
        private Image icon;
        boolean hovered = false, pressed = false;
        Runnable onClick = null;

        ReturnButton(int x, int y, int w, int h, Runnable cb, Image icon) {
            this.bounds = new Rectangle(x, y, w, h);
            this.onClick = cb;
            this.icon = icon;
        }

        void paint(Graphics2D g2) {
            Graphics2D g = (Graphics2D) g2.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            RoundRectangle2D pill = new RoundRectangle2D.Float(
                    bounds.x, bounds.y, bounds.width, bounds.height,
                    bounds.height, bounds.height);

            int alpha = hovered ? 90 : 60;
            g.setColor(new Color(255, 255, 255, alpha));
            g.fill(pill);

            g.setStroke(new java.awt.BasicStroke(2f));
            g.setColor(new Color(255, 200, 0, hovered ? 200 : 120));
            g.draw(pill);

            if (icon != null) {
                int iw = (int) (bounds.height * 0.6), ih = iw;
                int ix = bounds.x + (bounds.width - iw) / 2;
                int iy = bounds.y + (bounds.height - ih) / 2 + (pressed ? 1 : 0);
                g.drawImage(icon, ix, iy, iw, ih, null);
            } else {
                g.setColor(Color.WHITE);
                g.setFont(g.getFont().deriveFont(java.awt.Font.BOLD, bounds.height * 0.55f));
                g.drawString("⟵",
                        bounds.x + bounds.width / 2 - (int) (bounds.height * 0.2),
                        bounds.y + (int) (bounds.height * 0.72));
            }
            g.dispose();
        }

        void mouseMoved(MouseEvent e) {
            hovered = bounds.contains(e.getPoint());
        }

        void mousePressed(MouseEvent e) {
            pressed = bounds.contains(e.getPoint());
        }

        void mouseReleased(MouseEvent e) {
            boolean fire = pressed && bounds.contains(e.getPoint());
            pressed = false;
            if (fire && onClick != null) {
                onClick.run();
            }
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

    private void showPauseMenu() {
        // Tạm dừng game
        currentState = MenuState.PAUSED;
        stopLogicThread();
        timer.stop();

        // Hiển thị dialog
        Window parent = javax.swing.SwingUtilities.getWindowAncestor(this);
        PauseMenuDialog dialog = new PauseMenuDialog(parent);
        dialog.setVisible(true);

        // Xử lý sau khi đóng dialog
        if (dialog.isResumeClicked()) {
            // Chơi tiếp
            currentState = MenuState.GAME;
            startLogicThread();
            timer.start();
            requestFocusInWindow();
        } else if (dialog.isRestartClicked()) {
            // Chơi lại
            startGame();
            timer.start();
            requestFocusInWindow();
        } else if (dialog.isExitClicked()) {
            // Khi chọn Exit to Menu
            gameManager.saveGame();
            returnToMainMenu(); // Quay về menu chính
            timer.start(); // Tiếp tục timer để menu hoạt động
            repaint();  // Vẽ lại giao diện menu
            requestFocusInWindow(); // Focus lại panel

        } else {
            // Nhấn ESC hoặc đóng dialog -> tiếp tục game
            currentState = MenuState.GAME;
            startLogicThread();
            timer.start();
            requestFocusInWindow();
        }
    }

    private class PauseButton {
        private java.awt.Rectangle bounds;
        private boolean hovered = false;
        private boolean pressed = false;

        PauseButton() {
            updateBounds();
        }

        void updateBounds() {
            int size = 35;
            int margin = 20;
            bounds = new Rectangle(
                    getWidth() - size - margin,
                    margin - 10,
                    size, size);
        }

        void paint(Graphics2D g2) {
            if (currentState != MenuState.GAME && currentState != MenuState.COUNTDOWN) {
                return;
            }

            updateBounds(); // Cập nhật vị trí theo kích thước màn hình

            g2 = (Graphics2D) g2.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Icon
            if (pauseIcon != null) {
                int iconSize = (int) (bounds.width * 1.7);
                int iconX = bounds.x + (bounds.width - iconSize) / 2;
                int iconY = bounds.y + (bounds.height - iconSize) / 2 + (pressed ? 2 : 0);
                g2.drawImage(pauseIcon, iconX, iconY, iconSize, iconSize, null);
            } else {
                // Vẽ ký hiệu ||
                g2.setColor(new Color(76, 175, 80));
                g2.setFont(new Font("Arial", Font.BOLD, 24));
                g2.drawString("| |",
                        bounds.x + bounds.width / 2 - 12,
                        bounds.y + bounds.height / 2 + 8);
            }

            g2.dispose();
        }

        boolean contains(int x, int y) {
            return bounds != null && bounds.contains(x, y);
        }

        void setHovered(boolean h) {
            this.hovered = h;
        }

        void setPressed(boolean p) {
            this.pressed = p;
        }
    }
}