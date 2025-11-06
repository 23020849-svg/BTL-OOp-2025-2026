package arkanoid.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import arkanoid.utils.GameProgress;
import arkanoid.utils.ProgressManager;

/**
 * Dialog cho phép người chơi chọn level
 */
public class LevelSelectDialog extends JDialog {
    
    private static final Color BG_COLOR = new Color(20, 30, 45, 250);
    private static final Color PRIMARY_COLOR = new Color(80, 240, 255);
    private static final Color LOCKED_COLOR = new Color(100, 100, 100);
    private static final Color COMPLETED_COLOR = new Color(100, 255, 100);
    
    private int selectedLevel = -1;
    private boolean cancelled = false;
    
    public LevelSelectDialog(Window parent) {
        super(parent, "CHỌN LEVEL", Dialog.ModalityType.APPLICATION_MODAL);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        
        initComponents();
        
        setSize(700, 600);
        setLocationRelativeTo(parent);
    }
    
    private void initComponents() {
        // Main panel với background tùy chỉnh
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D bg = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 30, 30
                );
                g2.setColor(BG_COLOR);
                g2.fill(bg);
                
                g2.setColor(PRIMARY_COLOR);
                g2.setStroke(new java.awt.BasicStroke(3f));
                g2.draw(bg);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("CHỌN LEVEL", SwingConstants.CENTER);
        try {
            titleLabel.setFont(MenuRenderer.loadCustomFont(42f).deriveFont(Font.BOLD));
        } catch (Exception e) {
            titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        }
        titleLabel.setForeground(PRIMARY_COLOR);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Level grid
        JPanel levelPanel = createLevelGrid();
        JScrollPane scrollPane = new JScrollPane(levelPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton cancelBtn = createStyledButton("Hủy");
        cancelBtn.addActionListener(e -> {
            cancelled = true;
            dispose();
        });
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // ESC to close
        getRootPane().registerKeyboardAction(
            e -> {
                cancelled = true;
                dispose();
            },
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
        
        setContentPane(mainPanel);
    }
    
    private JPanel createLevelGrid() {
        JPanel gridPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        gridPanel.setOpaque(false);
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        ProgressManager pm = ProgressManager.getInstance();
        GameProgress progress = pm.getProgress();
        
        // Tạo 10 level buttons
        for (int i = 1; i <= 10; i++) {
            gridPanel.add(createLevelCard(i, progress));
        }
        
        return gridPanel;
    }
    
    private JPanel createLevelCard(int levelNum, GameProgress progress) {
        boolean unlocked = progress.isLevelUnlocked(levelNum);
        GameProgress.LevelProgress lp = progress.getLevelProgress(levelNum);
        boolean completed = lp != null && lp.isCompleted();
        
        JPanel card = new JPanel() {
            private boolean hovered = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                   RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D bg = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 20, 20
                );
                
                // Background color
                if (!unlocked) {
                    g2.setColor(new Color(50, 50, 50, 200));
                } else if (completed) {
                    g2.setColor(new Color(30, 100, 50, 200));
                } else {
                    g2.setColor(new Color(40, 60, 80, 200));
                }
                g2.fill(bg);
                
                // Border
                if (hovered && unlocked) {
                    g2.setColor(PRIMARY_COLOR);
                    g2.setStroke(new java.awt.BasicStroke(3f));
                } else if (completed) {
                    g2.setColor(COMPLETED_COLOR);
                    g2.setStroke(new java.awt.BasicStroke(2f));
                } else if (unlocked) {
                    g2.setColor(new Color(100, 150, 200));
                    g2.setStroke(new java.awt.BasicStroke(2f));
                } else {
                    g2.setColor(LOCKED_COLOR);
                    g2.setStroke(new java.awt.BasicStroke(1f));
                }
                g2.draw(bg);
            }
            
            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (unlocked) {
                            hovered = true;
                            repaint();
                        }
                    }
                    
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        repaint();
                    }
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (unlocked) {
                            selectedLevel = levelNum;
                            dispose();
                        }
                    }
                });
            }
        };
        
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(15, 10, 15, 10));
        card.setPreferredSize(new Dimension(180, 150));
        
        if (!unlocked) {
            card.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
        } else {
            card.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }
        
        // Level number
        JLabel levelLabel = new JLabel("LEVEL " + levelNum, SwingConstants.CENTER);
        levelLabel.setFont(new Font("Arial", Font.BOLD, 24));
        levelLabel.setForeground(unlocked ? Color.WHITE : LOCKED_COLOR);
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(levelLabel);
        
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Status
        String statusText;
        Color statusColor;
        if (!unlocked) {
            statusText = "🔒 Khóa";
            statusColor = LOCKED_COLOR;
        } else if (completed) {
            statusText = "✓ Hoàn thành";
            statusColor = COMPLETED_COLOR;
        } else {
            statusText = "Chưa hoàn thành";
            statusColor = new Color(255, 200, 100);
        }
        
        JLabel statusLabel = new JLabel(statusText, SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(statusColor);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);
        
        // Stats (if completed)
        if (completed && lp != null) {
            card.add(Box.createRigidArea(new Dimension(0, 10)));
            
            JLabel scoreLabel = new JLabel("Best: " + lp.getBestScore(), SwingConstants.CENTER);
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            scoreLabel.setForeground(new Color(200, 200, 200));
            scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(scoreLabel);
            
            JLabel timeLabel = new JLabel("Time: " + lp.getFormattedTime(), SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            timeLabel.setForeground(new Color(200, 200, 200));
            timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(timeLabel);
        }
        
        return card;
    }
    
    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(76, 175, 80));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(56, 142, 60));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(76, 175, 80));
            }
        });
        
        return btn;
    }
    
    public int getSelectedLevel() {
        return selectedLevel;
    }
    
    public boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * Hiển thị dialog và trả về level được chọn
     * @return level number (1-10) hoặc -1 nếu hủy
     */
    public static int showDialog(Window parent) {
        LevelSelectDialog dialog = new LevelSelectDialog(parent);
        dialog.setVisible(true);
        return dialog.getSelectedLevel();
    }
}