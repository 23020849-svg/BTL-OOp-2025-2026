package arkanoid.view;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Window;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import arkanoid.utils.HighScoreManager;

public class LeaderboardDialog extends JDialog {

    public LeaderboardDialog(Window parent, int count) {
        super(parent, "Bảng Xếp Hạng", Dialog.ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());

        HighScoreManager hsm = new HighScoreManager();
        List<HighScoreManager.ScoreEntry> top = hsm.getTopScores(count);

        String[] columns = {"STT", "Tên", "Điểm", "Thời gian"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0, 2 -> Integer.class;
                    default -> String.class;
                };
            }
        };

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (int i = 0; i < top.size(); i++) {
            HighScoreManager.ScoreEntry e = top.get(i);
            model.addRow(new Object[]{
                i + 1,
                e.name,
                e.score,
                fmt.format(new Date(e.timestamp))
            });
        }

        JTable table = new JTable(model);
        table.setEnabled(false);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        // Canh giữa các cột số & thời gian
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);

        // Sort (mặc định theo điểm giảm dần nếu muốn)
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        sorter.toggleSortOrder(2); // Điểm
        sorter.toggleSortOrder(2); // Đảo để thành giảm dần

        // Độ rộng cột gợi ý
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(220);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(160);

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dispose());
        south.add(closeBtn);
        add(south, BorderLayout.SOUTH);

        // ESC để đóng, ENTER mặc định
        getRootPane().setDefaultButton(closeBtn);
        getRootPane().registerKeyboardAction(
            e -> dispose(),
            KeyStroke.getKeyStroke("ESCAPE"),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        setSize(560, 420);
        setLocationRelativeTo(parent);
    }

    /** Tiện ích gọi nhanh dialog */
    public static void showTop(Window parent, int count) {
        LeaderboardDialog d = new LeaderboardDialog(parent, count);
        d.setVisible(true);
    }
}
