package com.logicanalyzer.app.ui;

import java.awt.Color;
import java.awt.Font;

public class VintageTheme {
    // Tông màu Nâu cổ điển (Vintage / Classic Espresso & Parchment)
    public static final Color BG_DARK = new Color(0x2B, 0x22, 0x1E);         // Nâu cà phê đậm / nền viền
    public static final Color BG_PANEL = new Color(0x3E, 0x32, 0x2C);        // Nâu hạt dẻ / nền panel
    public static final Color BG_PARCHMENT = new Color(0xF7, 0xF2, 0xEA);    // Giấy cổ parchment kem sáng (cho bảng)
    public static final Color BG_TABLE_HEADER = new Color(0xE3, 0xD9, 0xCB);// Nâu cát cổ điển
    public static final Color BG_ROW_ALT = new Color(0xED, 0xE6, 0xDC);     // Hàng xen kẽ giấy ngà
    
    // Màu nhấn (Accents)
    public static final Color ACCENT_COPPER = new Color(0xBF, 0x6E, 0x35);   // Đồng đỏ cổ điển (Copper / Amber)
    public static final Color ACCENT_HOVER = new Color(0xD6, 0x85, 0x4B);    // Đồng sáng khi hover
    public static final Color ACCENT_GOLD = new Color(0xDA, 0xA5, 0x48);     // Vàng đồng cổ điển (Gold Leaf)
    public static final Color TEXT_LIGHT = new Color(0xFD, 0xFB, 0xF7);          // Chữ sáng kem trên nền nâu
    public static final Color TEXT_MUTED = new Color(0xC7, 0xBC, 0xB1);      // Chữ phụ
    public static final Color TEXT_DARK = new Color(0x26, 0x1D, 0x18);       // Chữ đen nâu trên giấy parchment
    
    public static final Color BORDER_COLOR = new Color(0x61, 0x4E, 0x42);    // Viền kim loại đồng cổ (sắc nét, vuông vức)
    public static final Color BORDER_FOCUS = new Color(0xD6, 0x85, 0x4B);    // Viền đồng khi focus
    
    // Font Segoe UI & Tahoma: ĐẢM BẢO 100% HIỂN THỊ TIẾNG VIỆT CHUẨN KHÔNG BAO GIỜ LỖI FONT
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_MONO = new Font("Consolas", Font.BOLD, 13);
    public static final Font FONT_MATH = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_STATUS = new Font("Segoe UI", Font.ITALIC, 12);
}
