package com.roger.springbootcaptcha.core;

import com.google.code.kaptcha.text.WordRenderer;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import com.google.code.kaptcha.text.WordRenderer;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class CustomWordRenderer implements WordRenderer {

    private static final List<Color> COLORS = Arrays.asList(
            new Color(30, 30, 30),  // 黑色
            new Color(0, 102, 204), // 藍色
            new Color(153, 51, 255),// 紫色
            new Color(0, 128, 0)    // 深綠色
    );

    private static final Color BACKGROUND_COLOR = new Color(204, 237, 254); // 淡藍色
    private static final List<Color> LINE_COLORS = Arrays.asList(
            new Color(50, 150, 255), // 天藍色
            new Color(200, 50, 50),  // 紅色
            new Color(100, 255, 100),// 淡綠色
            new Color(255, 200, 0)   // 橙色
    );

    private static final Font FONT = new Font("Arial", Font.BOLD | Font.ITALIC, 50);
    private static final Random RANDOM = new Random();

    @Override
    public BufferedImage renderWord(String word, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();

        // 設定背景顏色
        g2.setColor(BACKGROUND_COLOR);
        g2.fillRect(0, 0, width, height);

        g2.setFont(FONT);
        FontRenderContext frc = g2.getFontRenderContext();

        int x = 20; // 初始 X 位置
        int y = height / 2 + FONT.getSize() / 4; // 讓文字置中

        // 添加干擾線條
        addNoiseLines(g2, width, height);

        for (int i = 0; i < word.length(); i++) {
            String character = String.valueOf(word.charAt(i));

            // 設定隨機顏色
            g2.setColor(COLORS.get(i % COLORS.size()));

            // 添加旋轉角度
            double rotation = (RANDOM.nextDouble() - 0.5) * 0.4; // 介於 -0.2 ~ 0.2 弧度
            AffineTransform originalTransform = g2.getTransform();
            g2.rotate(rotation, x + 10, y - 10);

            // 繪製文字
            TextLayout textLayout = new TextLayout(character, FONT, frc);
            Shape shape = textLayout.getOutline(null);
            g2.translate(x, y);
            g2.fill(shape);
            g2.translate(-x, -y);
            g2.setTransform(originalTransform);

            // X 軸偏移，避免文字重疊
            x += FONT.getSize() - RANDOM.nextInt(10);
        }

        g2.dispose();
        return image;
    }

    private void addNoiseLines(Graphics2D g2, int width, int height) {
        for (int i = 0; i < 6; i++) { // 6 條干擾線
            g2.setColor(LINE_COLORS.get(RANDOM.nextInt(LINE_COLORS.size())));
            int x1 = RANDOM.nextInt(width);
            int y1 = RANDOM.nextInt(height);
            int x2 = RANDOM.nextInt(width);
            int y2 = RANDOM.nextInt(height);
            g2.setStroke(new BasicStroke(2)); // 線條粗細
            g2.drawLine(x1, y1, x2, y2);
        }
    }
}


