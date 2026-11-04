package com.logicanalyzer.app.ui;

import com.logicanalyzer.model.entity.Implicant;
import com.logicanalyzer.model.entity.QMResult;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class ResultPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final JLabel lblCanonicalType;
    private final JLabel lblTargetMinterms;
    private final JLabel lblPrimeImplicants;
    private final JLabel lblEssentialPIs;
    private final JLabel lblSelectedPIs;
    private final JTextField txtFinalExpression;
    private final VintageButton btnCopy;

    public ResultPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(VintageTheme.BG_PANEL);
        setBorder(new EmptyBorder(20, 25, 20, 25));

        // Panel thông tin tổng hợp
        JPanel infoPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        infoPanel.setBackground(VintageTheme.BG_PANEL);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(VintageTheme.BORDER_COLOR, 1),
                new EmptyBorder(15, 18, 15, 18)
        ));

        lblCanonicalType = createInfoLabel("Dạng chuẩn tối giản: Chưa có dữ liệu");
        lblTargetMinterms = createInfoLabel("Minterms cần tối giản: Chưa có dữ liệu");
        lblPrimeImplicants = createInfoLabel("Tổng số Prime Implicants (PI): 0");
        lblEssentialPIs = createInfoLabel("Essential Prime Implicants (EPI): []");
        lblSelectedPIs = createInfoLabel("Tập PI tối thiểu được chọn (Selected PIs): []");

        infoPanel.add(lblCanonicalType);
        infoPanel.add(lblTargetMinterms);
        infoPanel.add(lblPrimeImplicants);
        infoPanel.add(lblEssentialPIs);
        infoPanel.add(lblSelectedPIs);
        add(infoPanel, BorderLayout.NORTH);

        // Panel biểu thức tối giản nổi bật
        JPanel exprPanel = new JPanel(new BorderLayout(12, 12));
        exprPanel.setBackground(VintageTheme.BG_PANEL);
        exprPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(VintageTheme.BORDER_COLOR, 1),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel exprTitle = new JLabel("BIỂU THỨC ĐẠI SỐ LOGIC TỐI GIẢN CUỐI CÙNG:", SwingConstants.LEFT);
        exprTitle.setFont(VintageTheme.FONT_TITLE);
        exprTitle.setForeground(VintageTheme.ACCENT_GOLD);
        exprPanel.add(exprTitle, BorderLayout.NORTH);

        txtFinalExpression = new JTextField("F = ...");
        txtFinalExpression.setFont(new Font("Segoe UI", Font.BOLD, 22));
        txtFinalExpression.setEditable(false);
        txtFinalExpression.setBackground(VintageTheme.BG_PARCHMENT);
        txtFinalExpression.setForeground(new Color(0x8B, 0x25, 0x00)); // Màu son đồng cổ
        txtFinalExpression.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(VintageTheme.BORDER_FOCUS, 2),
                new EmptyBorder(12, 15, 12, 15)
        ));
        exprPanel.add(txtFinalExpression, BorderLayout.CENTER);

        btnCopy = new VintageButton("Sao chép biểu thức");
        btnCopy.setPreferredSize(new Dimension(200, 42));
        btnCopy.addActionListener(e -> {
            String expr = txtFinalExpression.getText();
            if (!expr.isEmpty() && !expr.equals("F = ...")) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(expr), null);
                btnCopy.setText("Đã sao chép thành công! ✓");
                Timer t = new Timer(1500, ev -> btnCopy.setText("Sao chép biểu thức"));
                t.setRepeats(false);
                t.start();
            }
        });
        exprPanel.add(btnCopy, BorderLayout.SOUTH);

        add(exprPanel, BorderLayout.CENTER);
    }

    private JLabel createInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(VintageTheme.FONT_BODY);
        lbl.setForeground(VintageTheme.TEXT_LIGHT);
        return lbl;
    }

    public void displayResult(QMResult result) {
        if (result == null) {
            clear();
            return;
        }

        lblCanonicalType.setText("Dạng chuẩn tối giản: " + result.getExpressionType().getDisplayName());
        lblTargetMinterms.setText("Minterms cần tối giản: " + result.getTargetMinterms().toString());
        lblPrimeImplicants.setText("Tổng số Prime Implicants (PI): " + result.getPrimeImplicants().size() + " term");
        lblEssentialPIs.setText("Essential Prime Implicants (EPI): " + formatImplicantList(result.getEssentialPrimeImplicants()));
        lblSelectedPIs.setText("Tập PI tối thiểu được chọn (Selected PIs): " + formatImplicantList(result.getSelectedPrimeImplicants()));

        txtFinalExpression.setText("F = " + result.getSimplifiedExpression());
    }

    private String formatImplicantList(List<Implicant> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[ ");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i).getMask()).append(" ").append(list.get(i).getMinterms());
        }
        sb.append(" ]");
        return sb.toString();
    }

    public void clear() {
        lblCanonicalType.setText("Dạng chuẩn tối giản: Chưa có dữ liệu");
        lblTargetMinterms.setText("Minterms cần tối giản: Chưa có dữ liệu");
        lblPrimeImplicants.setText("Tổng số Prime Implicants (PI): 0");
        lblEssentialPIs.setText("Essential Prime Implicants (EPI): []");
        lblSelectedPIs.setText("Tập PI tối thiểu được chọn (Selected PIs): []");
        txtFinalExpression.setText("F = ...");
    }
}
