package com.logicanalyzer.app.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TruthTablePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private int numVariables = 3;
    private JTable table;
    private DefaultTableModel tableModel;

    private static class TruthTableModel extends DefaultTableModel {
        private static final long serialVersionUID = 1L;
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }

    private static class TruthTableRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
            Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
            setHorizontalAlignment(SwingConstants.CENTER);

            int lastCol = t.getColumnCount() - 1;
            if (!isSelected) {
                if (col == lastCol) {
                    int val = (value instanceof Integer) ? (int) value : Integer.parseInt(value.toString());
                    if (val == 1) {
                        c.setBackground(new Color(0xE8, 0xC3, 0x9E));
                        c.setForeground(new Color(0x6B, 0x22, 0x00));
                        c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    } else {
                        c.setBackground(new Color(0xF2, 0xEC, 0xE1));
                        c.setForeground(new Color(0x8C, 0x82, 0x7A));
                        c.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                    }
                } else if (col == 0) {
                    c.setBackground(VintageTheme.BG_ROW_ALT);
                    c.setForeground(new Color(0x5A, 0x48, 0x3C));
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    c.setBackground(row % 2 == 0 ? VintageTheme.BG_PARCHMENT : VintageTheme.BG_ROW_ALT);
                    c.setForeground(VintageTheme.TEXT_DARK);
                    c.setFont(VintageTheme.FONT_MONO);
                }
            } else {
                c.setFont(new Font("Segoe UI", Font.BOLD, 13));
            }
            return c;
        }
    }

    public TruthTablePanel() {
        setLayout(new BorderLayout(0, 8));
        setBackground(VintageTheme.BG_PANEL);
        setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(VintageTheme.BORDER_COLOR, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // 1. Title
        JLabel titleLabel = new JLabel("BẢNG CHÂN TRỊ (TRUTH TABLE)", SwingConstants.CENTER);
        titleLabel.setFont(VintageTheme.FONT_TITLE);
        titleLabel.setForeground(VintageTheme.ACCENT_GOLD);
        add(titleLabel, BorderLayout.NORTH);

        // 2. Table
        initTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(VintageTheme.BG_PARCHMENT);
        scrollPane.setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));
        add(scrollPane, BorderLayout.CENTER);

        // 3. Quick Action Toolbar (Bottom)
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 6));
        bottomPanel.setBackground(VintageTheme.BG_PANEL);

        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 6, 0));
        btnPanel.setBackground(VintageTheme.BG_PANEL);

        JButton btnAllZero = createSmallButton("Tất cả 0");
        btnAllZero.addActionListener(e -> setAllOutputs(0));
        btnPanel.add(btnAllZero);

        JButton btnAllOne = createSmallButton("Tất cả 1");
        btnAllOne.addActionListener(e -> setAllOutputs(1));
        btnPanel.add(btnAllOne);

        JButton btnInvert = createSmallButton("Đảo 0 ↔ 1");
        btnInvert.addActionListener(e -> invertAllOutputs());
        btnPanel.add(btnInvert);

        JButton btnQuickInput = createSmallButton("Nhập m(...)");
        btnQuickInput.addActionListener(e -> showQuickMintermDialog());
        btnPanel.add(btnQuickInput);

        bottomPanel.add(btnPanel, BorderLayout.NORTH);

        JLabel tipLabel = new JLabel("Click chuột hoặc bấm phím Cách / 0 / 1 để đổi giá trị F", SwingConstants.CENTER);
        tipLabel.setFont(VintageTheme.FONT_STATUS);
        tipLabel.setForeground(VintageTheme.TEXT_MUTED);
        bottomPanel.add(tipLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setBackground(new Color(0x4E, 0x3E, 0x35));
        btn.setForeground(VintageTheme.TEXT_LIGHT);
        btn.setFocusPainted(false);
        btn.setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void initTable() {
        tableModel = new TruthTableModel();
        table = new JTable(tableModel);
        table.setFont(VintageTheme.FONT_MONO);
        table.setRowHeight(25);
        table.setGridColor(VintageTheme.BORDER_COLOR);
        table.setShowGrid(true);
        table.setBackground(VintageTheme.BG_PARCHMENT);
        table.setForeground(VintageTheme.TEXT_DARK);
        table.setSelectionBackground(VintageTheme.ACCENT_COPPER);
        table.setSelectionForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getTableHeader().setFont(VintageTheme.FONT_BODY_BOLD);
        table.getTableHeader().setBackground(VintageTheme.BG_TABLE_HEADER);
        table.getTableHeader().setForeground(VintageTheme.TEXT_DARK);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    toggleRowOutput(row);
                }
            }
        });

        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
                        toggleRowOutput(row);
                        e.consume();
                    } else if (e.getKeyChar() == '0') {
                        setRowOutput(row, 0);
                        selectNextRow(row);
                        e.consume();
                    } else if (e.getKeyChar() == '1') {
                        setRowOutput(row, 1);
                        selectNextRow(row);
                        e.consume();
                    }
                }
            }
        });

        reloadTableData();
    }

    private void toggleRowOutput(int row) {
        int lastCol = tableModel.getColumnCount() - 1;
        Object val = tableModel.getValueAt(row, lastCol);
        int cur = (val instanceof Integer) ? (int) val : Integer.parseInt(val.toString());
        tableModel.setValueAt(cur == 0 ? 1 : 0, row, lastCol);
    }

    private void setRowOutput(int row, int val) {
        int lastCol = tableModel.getColumnCount() - 1;
        tableModel.setValueAt(val, row, lastCol);
    }

    private void selectNextRow(int curRow) {
        if (curRow + 1 < tableModel.getRowCount()) {
            table.setRowSelectionInterval(curRow + 1, curRow + 1);
        }
    }

    private void setAllOutputs(int val) {
        int lastCol = tableModel.getColumnCount() - 1;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(val, i, lastCol);
        }
    }

    private void invertAllOutputs() {
        int lastCol = tableModel.getColumnCount() - 1;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object val = tableModel.getValueAt(i, lastCol);
            int cur = (val instanceof Integer) ? (int) val : Integer.parseInt(val.toString());
            tableModel.setValueAt(cur == 0 ? 1 : 0, i, lastCol);
        }
    }

    private void showQuickMintermDialog() {
        String input = JOptionPane.showInputDialog(
                this,
                "Nhập danh sách minterm có f = 1 (cách nhau bởi dấu phẩy, ví dụ: 0, 2, 5, 7):",
                "Nhập nhanh Minterms",
                JOptionPane.QUESTION_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) return;

        setAllOutputs(0);
        String[] parts = input.split("[,\\s]+");
        int maxRow = tableModel.getRowCount();
        int lastCol = tableModel.getColumnCount() - 1;

        for (String p : parts) {
            p = p.trim();
            if (p.isEmpty()) continue;
            try {
                int m = Integer.parseInt(p);
                if (m >= 0 && m < maxRow) {
                    tableModel.setValueAt(1, m, lastCol);
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    public void setVariables(int numVars) {
        this.numVariables = numVars;
        reloadTableData();
    }

    private void reloadTableData() {
        int rows = 1 << numVariables;
        String[] cols;
        if (numVariables == 3) {
            cols = new String[]{"m", "A", "B", "C", "F"};
        } else {
            cols = new String[]{"m", "A", "B", "C", "D", "F"};
        }

        tableModel.setColumnIdentifiers(cols);
        tableModel.setRowCount(0);

        for (int i = 0; i < rows; i++) {
            Object[] rowData = new Object[cols.length];
            rowData[0] = "m" + i;
            for (int bit = 0; bit < numVariables; bit++) {
                int shift = numVariables - 1 - bit;
                rowData[1 + bit] = (i >> shift) & 1;
            }
            rowData[cols.length - 1] = 0;
            tableModel.addRow(rowData);
        }

        TruthTableRenderer renderer = new TruthTableRenderer();
        for (int i = 0; i < cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(45);
        table.getColumnModel().getColumn(cols.length - 1).setPreferredWidth(55);
    }

    public int[] getOutputs() {
        int rows = tableModel.getRowCount();
        int[] out = new int[rows];
        int lastCol = tableModel.getColumnCount() - 1;
        for (int i = 0; i < rows; i++) {
            Object val = tableModel.getValueAt(i, lastCol);
            out[i] = (val instanceof Integer) ? (int) val : Integer.parseInt(val.toString());
        }
        return out;
    }

    public void resetOutputs() {
        setAllOutputs(0);
    }
}
