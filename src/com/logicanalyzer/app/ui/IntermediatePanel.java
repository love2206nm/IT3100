package com.logicanalyzer.app.ui;

import com.logicanalyzer.model.entity.Implicant;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class IntermediatePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JPanel columnsContainer;

    private static class IntermediateModel extends DefaultTableModel {
        private static final long serialVersionUID = 1L;
        public IntermediateModel(String[] cols, int rowCount) {
            super(cols, rowCount);
        }
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private static class IntermediateRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, val, isSel, hasFoc, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);
            String tickVal = (String) t.getValueAt(row, 2);
            if ("[PI]".equals(tickVal)) {
                c.setBackground(new Color(0xFA, 0xDF, 0xC5));
                c.setFont(VintageTheme.FONT_BODY_BOLD);
                if (col == 2) {
                    c.setForeground(new Color(0x9E, 0x48, 0x1A));
                } else {
                    c.setForeground(VintageTheme.TEXT_DARK);
                }
            } else {
                c.setBackground(row % 2 == 0 ? VintageTheme.BG_PARCHMENT : VintageTheme.BG_ROW_ALT);
                c.setFont(VintageTheme.FONT_BODY);
                c.setForeground(VintageTheme.TEXT_DARK);
            }
            return c;
        }
    }

    public IntermediatePanel() {
        setLayout(new BorderLayout());
        setBackground(VintageTheme.BG_PANEL);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        columnsContainer = new JPanel();
        columnsContainer.setBackground(VintageTheme.BG_PANEL);
        columnsContainer.setLayout(new BoxLayout(columnsContainer, BoxLayout.X_AXIS));

        JScrollPane scrollPane = new JScrollPane(columnsContainer);
        scrollPane.setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(VintageTheme.BG_PANEL);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        showEmptyMessage("Chưa có dữ liệu. Nhấn 'Tối giản / Minimize' để xem các bước ghép cột.");
    }

    public void displayColumns(List<List<Implicant>> allColumns) {
        columnsContainer.removeAll();

        if (allColumns == null || allColumns.isEmpty()) {
            showEmptyMessage("Không có bước trung gian (hàm hằng hoặc không có minterm).");
            columnsContainer.revalidate();
            columnsContainer.repaint();
            return;
        }

        for (int colIdx = 0; colIdx < allColumns.size(); colIdx++) {
            List<Implicant> col = allColumns.get(colIdx);
            JPanel colPanel = createSingleColumnPanel("Cột " + colIdx + " (Column " + colIdx + ")", col);
            columnsContainer.add(colPanel);
            columnsContainer.add(Box.createRigidArea(new Dimension(15, 0)));
        }

        columnsContainer.revalidate();
        columnsContainer.repaint();
    }

    private JPanel createSingleColumnPanel(String title, List<Implicant> implicants) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(VintageTheme.BG_PANEL);
        p.setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));
        p.setPreferredSize(new Dimension(280, 440));
        p.setMaximumSize(new Dimension(320, 3000));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(VintageTheme.FONT_HEADER);
        titleLbl.setForeground(VintageTheme.ACCENT_GOLD);
        titleLbl.setOpaque(true);
        titleLbl.setBackground(VintageTheme.BG_DARK);
        titleLbl.setBorder(new EmptyBorder(8, 0, 8, 0));
        p.add(titleLbl, BorderLayout.NORTH);

        String[] cols = {"Term", "Minterms", "Trạng thái"};
        IntermediateModel model = new IntermediateModel(cols, 0);

        for (Implicant imp : implicants) {
            String tick = imp.isTicked() ? "Đã ghép (v)" : "[PI]";
            model.addRow(new Object[]{imp.getMask(), imp.getMinterms().toString(), tick});
        }

        JTable table = new JTable(model);
        table.setFont(VintageTheme.FONT_BODY);
        table.setRowHeight(24);
        table.setShowGrid(true);
        table.setGridColor(VintageTheme.BORDER_COLOR);
        table.setBackground(VintageTheme.BG_PARCHMENT);
        table.setForeground(VintageTheme.TEXT_DARK);
        table.getTableHeader().setFont(VintageTheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(VintageTheme.BG_TABLE_HEADER);
        table.getTableHeader().setForeground(VintageTheme.TEXT_DARK);
        table.getTableHeader().setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));

        table.setDefaultRenderer(Object.class, new IntermediateRenderer());

        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(125);
        table.getColumnModel().getColumn(2).setPreferredWidth(85);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(null);
        p.add(sp, BorderLayout.CENTER);

        return p;
    }

    public void clear() {
        columnsContainer.removeAll();
        showEmptyMessage("Chưa có dữ liệu. Nhấn 'Tối giản / Minimize' để xem các bước ghép cột.");
        columnsContainer.revalidate();
        columnsContainer.repaint();
    }

    private void showEmptyMessage(String message) {
        JLabel msg = new JLabel(message);
        msg.setFont(VintageTheme.FONT_STATUS);
        msg.setForeground(VintageTheme.TEXT_MUTED);
        msg.setBorder(new EmptyBorder(20, 20, 0, 0));
        columnsContainer.add(msg);
    }
}
