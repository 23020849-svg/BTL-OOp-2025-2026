package arkanoid.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class PauseMenuDialog extends JDialog {
	
	
	private static final long serialVersionUID = 1L;
	 private static final Color BG_COLOR = new Color(20, 30, 45, 230);
	private JPanel contentPanel = new JPanel();
	
	private boolean resumeClicked = false;
	private boolean restartClicked = false;
	private boolean exitClicked = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PauseMenuDialog dialog = new PauseMenuDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
		}
	}

	/**
	 * Create the dialog.
	 */
	public PauseMenuDialog(Window parent) {
		
		super(parent);
		setModal(true);
	    setUndecorated(true);
	    setBackground(new Color(0, 0, 0, 0));
		setBounds(600, 300, 430, 350);
		getContentPane().setLayout(new BorderLayout());
		
		// NHấn phím ESC để đóng dialog
		getRootPane().registerKeyboardAction(e -> {
            dispose();
        }, KeyStroke.getKeyStroke("ESCAPE"), JComponent.WHEN_IN_FOCUSED_WINDOW);


		contentPanel = new JPanel() {
            private static final long serialVersionUID = 1L;

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
                
                g2.setColor(new Color(80, 240, 255));
                g2.setStroke(new java.awt.BasicStroke(3f));
                g2.draw(bg);
            }
        };
        contentPanel.setOpaque(false);
		contentPanel.setToolTipText("PAUSE");
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel mainPanel = new JPanel();
			mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
			{
				JButton btnResume = new JButton("  Chơi Tiếp");
				btnResume.setForeground(new Color(0, 255, 255));
				ImageIcon originalIcon = new ImageIcon("rsc/button.png");
				Image scaledImage = originalIcon.getImage().getScaledInstance(280, 55, Image.SCALE_SMOOTH);
				btnResume.setIcon(new ImageIcon(scaledImage));
				btnResume.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
				btnResume.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
				btnResume.setBackground(new Color(100, 149, 237));
				btnResume.setAlignmentX(0.5f);
				mainPanel.add(btnResume);
				btnResume.setPreferredSize(new Dimension(280, 55));
				btnResume.setMaximumSize(new Dimension(280, 55));
				btnResume.setAlignmentY(1.5f);
				btnResume.addActionListener((ActionEvent e) -> {
                                    resumeClicked = true;
                                    dispose();
                                });
				btnResume.setFont(new Font("Arial", Font.BOLD, 18));
			}
			mainPanel.setOpaque(false);
			contentPanel.add(mainPanel, BorderLayout.CENTER);

			mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
			{
				JButton btnRestart = new JButton("Chơi lại");
				btnRestart.setForeground(new Color(0, 255, 255));
				ImageIcon originalIcon = new ImageIcon("rsc/button.png");
				Image scaledImage = originalIcon.getImage().getScaledInstance(280, 55, Image.SCALE_SMOOTH);
				btnRestart.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
				btnRestart.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
				btnRestart.setIcon(new ImageIcon(scaledImage));
				btnRestart.setBackground(new Color(100, 149, 237));
				btnRestart.addActionListener((ActionEvent e) -> {
                                    restartClicked = true;
                                    dispose();
                                });
				btnRestart.setPreferredSize(new Dimension(280, 55));
				btnRestart.setMaximumSize(new Dimension(280, 55));
				btnRestart.setFont(new Font("Arial", Font.BOLD, 18));
				btnRestart.setAlignmentY(1.5f);
				btnRestart.setAlignmentX(0.5f);
				mainPanel.add(btnRestart);
			}
			{
				Component rigidArea = Box.createRigidArea(new Dimension(0, 15));
				mainPanel.add(rigidArea);
			}
			{
				JButton btnExit = new JButton("Về Menu Chính");
				btnExit.setForeground(new Color(0, 255, 255));
				ImageIcon originalIcon = new ImageIcon("rsc/button.png");
				Image scaledImage = originalIcon.getImage().getScaledInstance(285, 60, Image.SCALE_SMOOTH);
				btnExit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
				btnExit.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
				btnExit.setIcon(new ImageIcon(scaledImage));
				btnExit.addActionListener((ActionEvent e) -> {
                                    exitClicked = true;
                                    dispose();
                                });
				btnExit.setPreferredSize(new Dimension(280, 55));
				btnExit.setMaximumSize(new Dimension(280, 55));
				btnExit.setFont(new Font("Arial", Font.BOLD, 18));
				btnExit.setAlignmentY(1.5f);
				btnExit.setAlignmentX(0.5f);
				mainPanel.add(btnExit);
			}
			
		}
		{
			JLabel titleLabel = new JLabel("GAME PAUSE");
			titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
			try {
	            titleLabel.setFont(MenuRenderer.loadCustomFont(42f).deriveFont(Font.BOLD));
	        } catch (Exception e) {
	            titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
	        }
			contentPanel.add(titleLabel, BorderLayout.NORTH);
			titleLabel.setForeground(new Color(0, 255, 255));
			titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
		}
		
	}
	public boolean isResumeClicked() { return resumeClicked; }
	public boolean isRestartClicked() { return restartClicked; }
	public boolean isExitClicked() { return exitClicked; }

}
