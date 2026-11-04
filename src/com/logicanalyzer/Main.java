package com.logicanalyzer;

import com.logicanalyzer.app.ui.MainFrame;

import javax.swing.*;
import java.awt.Font;
import java.util.Enumeration;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Áp dụng Look and Feel theo hệ điều hành Windows để thanh cuộn và tab hiển thị mượt mà
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            // Đặt font mặc định Segoe UI cho toàn bộ hệ thống Swing để không bao giờ bị lỗi font tiếng Việt
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
            Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, defaultFont);
                }
            }

            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
