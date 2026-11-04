package com.logicanalyzer.app.ui;

import com.logicanalyzer.model.entity.Implicant;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PITablePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JTable table;
    private final DefaultTableModel tableModel;
    private final Set<Integer> epiRowIndices = new HashSet<>();

    private static class NonEditableModel extends DefaultTableModel {
        private static final long serialVersionUID = 1L;
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private class PIMatrixRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFoc, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, val, isSel, hasFoc, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);

            if (!isSel) {
                if (epiRowIndices.contains(row)) {
                    c.setBackground(new Color(0xFA, 0xDF, 0xC5));
                    c.setFont(VintageTheme.FONT_BODY_BOLD);
                } else {
                    c.setBackground(row % 2 == 0 ? VintageTheme.BG_PARCHMENT : VintageTheme.BG_ROW_ALT);
                    c.setFont(VintageTheme.FONT_BODY);
                }

                if ("X".equals(val)) {
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    c.setForeground(new Color(0x9E, 0x48, 0x1A));
                } else {
                    c.setForeground(VintageTheme.TEXT_DARK);
                }
            }
            return c;
        }
    }

    public PITablePanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(VintageTheme.BG_PANEL);
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("MA TRẬN PHỦ PRIME IMPLICANT (PI TABLE)", SwingConstants.CENTER);
        title.setFont(VintageTheme.FONT_TITLE);
        title.setForeground(VintageTheme.ACCENT_GOLD);
        add(title, BorderLayout.NORTH);

        tableModel = new NonEditableModel();
        table = new JTable(tableModel);
        table.setFont(VintageTheme.FONT_BODY);
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(VintageTheme.BORDER_COLOR);
        table.setBackground(VintageTheme.BG_PARCHMENT);
        table.setForeground(VintageTheme.TEXT_DARK);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        table.getTableHeader().setFont(VintageTheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(VintageTheme.BG_TABLE_HEADER);
        table.getTableHeader().setForeground(VintageTheme.TEXT_DARK);
        table.getTableHeader().setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));

        table.setDefaultRenderer(Object.class, new PIMatrixRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        JLabel legendLabel = new JLabel("Ghi chú: Hàng màu cam đồng nổi bật (*) là Essential Prime Implicants (EPI) bắt buộc chọn.", SwingConstants.LEFT);
        legendLabel.setFont(VintageTheme.FONT_STATUS);
        legendLabel.setForeground(VintageTheme.TEXT_MUTED);
        add(legendLabel, BorderLayout.SOUTH);
    }

    public void displayPITable(List<Implicant> piList, List<Integer> targetMinterms, List<Implicant> essentialPIs) {
        epiRowIndices.clear();

        if (piList == null || piList.isEmpty() || targetMinterms == null || targetMinterms.isEmpty()) {
            tableModel.setColumnCount(0);
            tableModel.setRowCount(0);
            return;
        }

        Set<Implicant> epiSet = new HashSet<>(essentialPIs != null ? essentialPIs : Collections.emptyList());

        String[] headers = new String[2 + targetMinterms.size()];
        headers[0] = "PI Term";
        headers[1] = "Minterms";
        for (int i = 0; i < targetMinterms.size(); i++) {
            headers[2 + i] = "m" + targetMinterms.get(i);
        }

        tableModel.setColumnIdentifiers(headers);
        tableModel.setRowCount(0);

        for (int r = 0; r < piList.size(); r++) {
            Implicant pi = piList.get(r);
            if (epiSet.contains(pi)) {
                epiRowIndices.add(r);
            }

            Object[] rowData = new Object[headers.length];
            rowData[0] = pi.getMask() + (epiSet.contains(pi) ? " *" : "");
            rowData[1] = pi.getMinterms().toString();

            for (int c = 0; c < targetMinterms.size(); c++) {
                int m = targetMinterms.get(c);
                rowData[2 + c] = pi.covers(m) ? "X" : "";
            }
            tableModel.addRow(rowData);
        }

        if (table.getColumnCount() >= 2) {
            table.getColumnModel().getColumn(0).setPreferredWidth(95);
            table.getColumnModel().getColumn(1).setPreferredWidth(130);
            for (int i = 2; i < table.getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(48);
            }
        }
    }

    public void clear() {
        epiRowIndices.clear();
        tableModel.setColumnCount(0);
        tableModel.setRowCount(0);
    }
}
