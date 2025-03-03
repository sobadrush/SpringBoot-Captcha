package com.roger.springbootcaptcha.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.google.code.kaptcha.NoiseProducer;

public class RandomLineNoise implements NoiseProducer {
    private Random random = new Random();

    @Override
    public void makeNoise(BufferedImage image, float factorOne, float factorTwo, float factorThree, float factorFour) {
        Graphics2D graphics = image.createGraphics();

        int width = image.getWidth();
        int height = image.getHeight();

        // 设置抗锯齿
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 减少线条粗细
        graphics.setStroke(new BasicStroke(1.0f));

        int charCount = 4; // 假设有4个字符
        int charWidth = width / charCount;

        // 减少线条数量 - 每个字符后面添加1-3条线
        for (int i = 0; i < charCount; i++) {
            int lineCount = 1 + random.nextInt(3); // 1-3条线

            for (int j = 0; j < lineCount; j++) {
                // 使用彩色线条
                Color[] brightColors = {
                        new Color(0, 102, 204),    // 鲜蓝色
                        new Color(51, 153, 255),   // 天蓝色
                        new Color(0, 204, 255),    // 青色
                        new Color(102, 0, 204),    // 紫色
                        new Color(255, 51, 153),   // 粉红色
                        new Color(255, 153, 0),    // 橙色
                        new Color(0, 153, 0)       // 绿色
                };

                Color lineColor = brightColors[random.nextInt(brightColors.length)];
                graphics.setColor(lineColor);

                // 确定线条起始点 - 覆盖更大范围
                int startX = i * charWidth + random.nextInt(charWidth);
                int startY = random.nextInt(height);

                // 使用较小的角度范围
                double angle = Math.toRadians(random.nextInt(90) - 45); // -45度到45度

                // 减少线条长度
                int length = 10 + random.nextInt(20); // 10-30像素

                // 计算终点
                int endX = (int)(startX + length * Math.cos(angle));
                int endY = (int)(startY + length * Math.sin(angle));

                // 绘制线条
                graphics.drawLine(startX, startY, endX, endY);
            }
        }

        // 减少贯穿整个图像的长线条数量
        int longLines = 1 + random.nextInt(1); // 1-2条长线
        for (int i = 0; i < longLines; i++) {
            Color lineColor = new Color(
                    random.nextInt(100),
                    random.nextInt(100),
                    150 + random.nextInt(105)  // 确保蓝色成分较高
            );
            graphics.setColor(lineColor);

            int startX = random.nextInt(width / 4);
            int startY = random.nextInt(height);
            int endX = width - random.nextInt(width / 4);
            int endY = random.nextInt(height);

            graphics.drawLine(startX, startY, endX, endY);
        }

        graphics.dispose();
    }
}