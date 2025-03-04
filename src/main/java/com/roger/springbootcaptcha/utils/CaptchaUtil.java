package com.roger.springbootcaptcha.utils;

import lombok.extern.slf4j.Slf4j;
import nl.captcha.Captcha;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.renderer.WordRenderer;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Random;

/**
 * @author RogerLo
 * @date 2025/3/4
 */
@Component
@Slf4j
public class CaptchaUtil {

    // 包裝結果用的 Record
    public record CaptchaData(BufferedImage image, String ans) {
    }

    /**
     * spacing: 字元間距
     * numOfLines: 雜訊線數量
     */
    public CaptchaData createCaptcha(int spacing, int numOfLines) {
        // 增加寬度以支持更寬的字元間距
        int width = 160;
        int height = 40;

        // 自訂字體渲染器，實現多色數字和間距
        WordRenderer multiColorRenderer = (word, image) -> {
            Graphics2D gph2d = image.createGraphics();

            // 設置抗鋸齒效果
            gph2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            gph2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // 使用寬鬆的字體設定
            Font customFont = new Font("Arial", Font.BOLD, 25);
            gph2d.setFont(customFont);

            Color[] digitColors = {
                new Color(20, 50, 100),    // 深藍
                new Color(180, 30, 40),    // 暗紅
                new Color(20, 120, 50),    // 深綠
                new Color(150, 50, 150)    // 紫色
            };

            // 計算每個字元的初始位置，留出更多間距
            FontMetrics fm = gph2d.getFontMetrics();
            int totalWidth = fm.stringWidth(word);
            int startX = (image.getWidth() - (totalWidth + spacing * (word.length() - 1))) / 2;
            int y = (image.getHeight() + fm.getAscent()) / 2;

            // 逐字繪製，顏色和位置各不相同
            Random random = new SecureRandom();
            for (int i = 0; i < word.length(); i++) {
                // 為每個數字選擇不同顏色
                gph2d.setColor(digitColors[i]);

                // 計算當前字元位置，加入輕微隨機偏移
                int x = startX + i * (fm.stringWidth(word.substring(0, 1)) + spacing);
                int yOffset = random.nextInt(10) - 5;  // 上下輕微偏移

                // 加入輕微旋轉
                Graphics2D g2d = (Graphics2D) gph2d.create();
                g2d.rotate(Math.toRadians(random.nextInt(10) - 5), x, y + yOffset);
                g2d.drawString(String.valueOf(word.charAt(i)), x, y + yOffset);
                g2d.dispose();
            }

            gph2d.dispose();
        };

        // 產生驗證碼
        Captcha captcha = new Captcha.Builder(width, height)
            .addText(new NumbersAnswerProducer(4), multiColorRenderer)
            .addBackground(new FlatColorBackgroundProducer(new Color(209, 235, 254))) // 淺藍色背景
            // .gimp(new RippleGimpyRenderer()) // 輕微扭曲效果
            .build();

        // 取得 BufferedImage
        BufferedImage captchaImage = captcha.getImage();

        this.addComplexNoise(captchaImage, numOfLines);

        // 顯示驗證碼文字
        log.info(">>> Generated Captcha Text: {}", captcha.getAnswer());

        return new CaptchaUtil.CaptchaData(captchaImage, captcha.getAnswer());
    }

    /**
     * 增加雜訊線
     */
    private void addComplexNoise(BufferedImage image, int numOfLines) {
        Graphics2D g2d = image.createGraphics();
        Random random = new SecureRandom();

        // 準備多種顏色的干擾線
        Color[] noiseColors = {
            new Color(100, 150, 200, 100),  // 淺藍
            new Color(200, 100, 100, 100),  // 淺紅
            new Color(100, 200, 100, 100),  // 淺綠
            new Color(150, 100, 200, 100),  // 淺紫
            new Color(200, 150, 100, 100)   // 淺橙
        };

        // 繪製 15 條變化的干擾線
        for (int i = 0; i < numOfLines; i++) {
            // 隨機選擇線條顏色
            g2d.setColor(noiseColors[random.nextInt(noiseColors.length)]);

            // 線條粗細變化
            float lineWidth = 1f + random.nextFloat() * 1.5f;
            g2d.setStroke(new BasicStroke(lineWidth));

            // 線條長度和位置隨機
            int x1 = random.nextInt(image.getWidth());
            int y1 = random.nextInt(image.getHeight());
            int x2 = random.nextInt(image.getWidth());
            int y2 = random.nextInt(image.getHeight());

            g2d.draw(this.createCurve(x1, y1, x2, y2));
        }

        g2d.dispose();
    }

    /**
     * 生成曲線
     */
    private Shape createCurve(int x1, int y1, int x2, int y2) {
        QuadCurve2D curve = new QuadCurve2D.Float();
        int ctrlX = (x1 + x2) / 2;
        int ctrlY = (int) (Math.random() * 50); // 隨機控制點高度
        curve.setCurve(x1, y1, ctrlX, ctrlY, x2, y2);
        return curve;
    }

}
