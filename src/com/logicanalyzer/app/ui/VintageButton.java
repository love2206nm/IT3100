package com.logicanalyzer.app.ui;

import javax.swing.JButton;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Nút bấm phong cách Nâu cổ điển vuông vức với animation chuyển đổi màu mượt mà khi di chuột.
 */
public class VintageButton extends JButton {

    private static final long serialVersionUID = 1L;

    private float hoverAlpha = 0.0f; // 0.0 -> 1.0 cho hiệu ứng mượt
    private Timer animTimer;

    public VintageButton(String text) {
        super(text);
        setFont(VintageTheme.FONT_HEADER);
        setForeground(VintageTheme.TEXT_LIGHT);
        setBackground(VintageTheme.ACCENT_COPPER);
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setBorder(new LineBorder(VintageTheme.BORDER_COLOR, 1));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                startAnimation(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                startAnimation(false);
            }
        });
    }

    private void startAnimation(boolean forward) {
        if (animTimer != null && animTimer.isRunning()) {
            animTimer.stop();
        }
        animTimer = new Timer(15, e -> {
            if (forward) {
                hoverAlpha += 0.1f;
                if (hoverAlpha >= 1.0f) {
                    hoverAlpha = 1.0f;
                    animTimer.stop();
                }
            } else {
                hoverAlpha -= 0.1f;
                if (hoverAlpha <= 0.0f) {
                    hoverAlpha = 0.0f;
                    animTimer.stop();
                }
            }
            repaint();
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color base = VintageTheme.ACCENT_COPPER;
        Color hover = VintageTheme.ACCENT_HOVER;

        int r = (int) (base.getRed() + hoverAlpha * (hover.getRed() - base.getRed()));
        int gr = (int) (base.getGreen() + hoverAlpha * (hover.getGreen() - base.getGreen()));
        int b = (int) (base.getBlue() + hoverAlpha * (hover.getBlue() - base.getBlue()));

        g.setColor(new Color(r, gr, b));
        g.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
    }
}
