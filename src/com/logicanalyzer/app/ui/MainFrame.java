package com.logicanalyzer.app.ui;

import com.logicanalyzer.app.presentation.NormalizerPresenter;
import com.logicanalyzer.app.presentation.NormalizerView;
import com.logicanalyzer.application.usecase.NormalizeExpressionUseCase;
import com.logicanalyzer.model.entity.ExpressionType;
import com.logicanalyzer.model.entity.QMResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class MainFrame extends JFrame implements NormalizerView {

    private static final long serialVersionUID = 1L;

    private final NormalizerPresenter presenter;

    // Components
    private JRadioButton rb3Vars;
    private JRadioButton rb4Vars;
    private JRadioButton rbSOP;
    private JRadioButton rbPOS;
    private VintageButton btnMinimize;
    private VintageButton btnReset;

    private TruthTablePanel truthTablePanel;
    private JTabbedPane tabbedPane;
    private IntermediatePanel intermediatePanel;
    private PITablePanel piTablePanel;
    private ResultPanel resultPanel;
    private JLabel statusLabel;

    public MainFrame() {
        super("Logic Expression Normalizer (Quine-McCluskey Method)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1160, 740);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        getContentPane().setBackground(VintageTheme.BG_DARK);

        NormalizeExpressionUseCase useCase = new NormalizeExpressionUseCase();
        this.presenter = new NormalizerPresenter(this, useCase);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 1. Top Control Bar (Nâu cổ điển sang trọng)
        JPanel controlBar = createControlBar();
        add(controlBar, BorderLayout.NORTH);

        // 2. Center Workspace (Chia đôi: Trái là TruthTable, Phải là Tabs kết quả)
        JPanel centerPanel = new JPanel(new BorderLayout(12, 12));
        centerPanel.setBackground(VintageTheme.BG_DARK);
        centerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        truthTablePanel = new TruthTablePanel();
        truthTablePanel.setPreferredSize(new Dimension(340, 0));
        centerPanel.add(truthTablePanel, BorderLayout.WEST);

        // Tabs kết quả
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(VintageTheme.FONT_HEADER);
        tabbedPane.setBackground(VintageTheme.BG_PANEL);
        tabbedPane.setForeground(VintageTheme.TEXT_DARK);
        tabbedPane.setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));

        intermediatePanel = new IntermediatePanel();
        piTablePanel = new PITablePanel();
        resultPanel = new ResultPanel();

        tabbedPane.addTab("Các bước trung gian (Columns)", intermediatePanel);
        tabbedPane.addTab("Ma trận PI Table", piTablePanel);
        tabbedPane.addTab("Biểu thức tối giản & Tổng kết", resultPanel);

        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Status Bar
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(VintageTheme.BG_DARK);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(VintageTheme.BORDER_COLOR, 1),
                new EmptyBorder(8, 15, 8, 15)
        ));
        statusLabel = new JLabel("Sẵn sàng. Nhập giá trị bảng chân trị (click ô cột F) và nhấn 'Tối giản / Minimize'.");
        statusLabel.setFont(VintageTheme.FONT_STATUS);
        statusLabel.setForeground(VintageTheme.TEXT_LIGHT);
        statusBar.add(statusLabel, BorderLayout.WEST);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel createControlBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 12));
        bar.setBackground(VintageTheme.BG_PANEL);
        bar.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(VintageTheme.BORDER_COLOR, 1),
                new EmptyBorder(4, 12, 4, 12)
        ));

        // Group chọn số biến
        JLabel lblVars = new JLabel("Số biến:");
        lblVars.setFont(VintageTheme.FONT_HEADER);
        lblVars.setForeground(VintageTheme.ACCENT_GOLD);
        bar.add(lblVars);

        rb3Vars = createVintageRadio("3 Biến (A, B, C)", true);
        rb4Vars = createVintageRadio("4 Biến (A, B, C, D)", false);
        ButtonGroup bgVars = new ButtonGroup();
        bgVars.add(rb3Vars);
        bgVars.add(rb4Vars);
        bar.add(rb3Vars);
        bar.add(rb4Vars);

        rb3Vars.addActionListener(e -> presenter.onVariableCountChanged(3));
        rb4Vars.addActionListener(e -> presenter.onVariableCountChanged(4));

        JSeparator sep1 = new JSeparator(SwingConstants.VERTICAL);
        sep1.setPreferredSize(new Dimension(2, 28));
        sep1.setForeground(VintageTheme.BORDER_COLOR);
        bar.add(sep1);

        // Group chọn dạng chuẩn
        JLabel lblForm = new JLabel("Dạng chuẩn:");
        lblForm.setFont(VintageTheme.FONT_HEADER);
        lblForm.setForeground(VintageTheme.ACCENT_GOLD);
        bar.add(lblForm);

        rbSOP = createVintageRadio("SOP (Sum of Products)", true);
        rbPOS = createVintageRadio("POS (Product of Sums)", false);
        ButtonGroup bgForm = new ButtonGroup();
        bgForm.add(rbSOP);
        bgForm.add(rbPOS);
        bar.add(rbSOP);
        bar.add(rbPOS);

        JSeparator sep2 = new JSeparator(SwingConstants.VERTICAL);
        sep2.setPreferredSize(new Dimension(2, 28));
        sep2.setForeground(VintageTheme.BORDER_COLOR);
        bar.add(sep2);

        // Nút hành động
        btnMinimize = new VintageButton("Tối giản / Minimize");
        btnMinimize.setPreferredSize(new Dimension(185, 38));
        btnMinimize.addActionListener(e -> {
            ExpressionType type = rbSOP.isSelected() ? ExpressionType.SOP : ExpressionType.POS;
            int[] outputs = truthTablePanel.getOutputs();
            presenter.onSubmitClicked(outputs, type);
        });
        bar.add(btnMinimize);

        btnReset = new VintageButton("Đặt lại / Reset");
        btnReset.setPreferredSize(new Dimension(140, 38));
        btnReset.addActionListener(e -> presenter.onResetClicked());
        bar.add(btnReset);

        return bar;
    }

    private JRadioButton createVintageRadio(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text, selected);
        rb.setFont(VintageTheme.FONT_BODY);
        rb.setForeground(VintageTheme.TEXT_LIGHT);
        rb.setBackground(VintageTheme.BG_PANEL);
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return rb;
    }

    // --- Cài đặt các phương thức của NormalizerView (MVP) ---

    @Override
    public void setVariableCount(int numVars) {
        truthTablePanel.setVariables(numVars);
        statusLabel.setText("Đã chuyển sang chế độ " + numVars + " biến.");
    }

    @Override
    public void displayResult(QMResult result) {
        intermediatePanel.displayColumns(result.getIntermediateColumns());
        piTablePanel.displayPITable(result.getPrimeImplicants(), result.getTargetMinterms(), result.getEssentialPrimeImplicants());
        resultPanel.displayResult(result);

        tabbedPane.setSelectedIndex(2); // Chuyển thẳng sang tab kết quả cuối cho trực quan
        statusLabel.setText("Rút gọn thành công dạng " + result.getExpressionType().getDisplayName() + "! Biểu thức tối giản: F = " + result.getSimplifiedExpression());
    }

    @Override
    public void clearResult() {
        intermediatePanel.clear();
        piTablePanel.clear();
        resultPanel.clear();
        statusLabel.setText("Đã xóa kết quả.");
    }

    @Override
    public void resetTruthTable() {
        truthTablePanel.resetOutputs();
        statusLabel.setText("Bảng chân trị đã được đặt lại về 0.");
    }

    @Override
    public void showError(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage, "Thông báo lỗi", JOptionPane.WARNING_MESSAGE);
        statusLabel.setText("Lỗi: " + errorMessage);
    }
}
