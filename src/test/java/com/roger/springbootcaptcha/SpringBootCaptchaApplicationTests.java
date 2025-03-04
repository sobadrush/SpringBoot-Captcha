package com.roger.springbootcaptcha;

import com.roger.springbootcaptcha.core.NumberTextProducer;
import nl.captcha.Captcha;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.audio.Sample;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.renderer.WordRenderer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SpringBootTest
class SpringBootCaptchaApplicationTests {

    @Test
    @Disabled
    @DisplayName("【Test_001】產生圖形驗證碼 (SimpleCaptcha 套件)")
    void test_001() {
        int width = 180;  // 增加寬度，讓字距拉開
        int height = 50;  // 提高高度，確保字體清晰

        // 產生四碼純數字驗證碼
        Captcha captcha = new Captcha.Builder(width, height)
                .addText(new NumbersAnswerProducer(4), new CustomWordRenderer()) // 生成四碼數字，並調整字距
                .addBackground(new FlatColorBackgroundProducer(new Color(209, 235, 254))) // 設定淺藍色背景
                // .gimp(new RippleGimpyRenderer()) // 文字扭曲
                .build();

        // 取得 BufferedImage
        BufferedImage image = captcha.getImage();

        // 在圖片上畫更明顯的彩色線條
        addStrongerColorfulNoise(image, 10);  // 產生 10 條隨機彩色粗線

        // 顯示驗證碼文字（可用於比對）
        System.out.println("Generated Captcha Text: " + captcha.getAnswer());

        // 儲存圖片
        try {
            ImageIO.write(image, "jpg", new File(System.getProperty("user.dir") + "/output_files/" + "captcha_output.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 產生更明顯且均勻分布的彩色雜訊線條
    private static void addStrongerColorfulNoise(BufferedImage image, int numLines) {
        Graphics2D g = image.createGraphics();
        Random rand = new Random();
        int width = image.getWidth();
        int height = image.getHeight();

        for (int i = 0; i < numLines; i++) {
            g.setColor(getRandomColor()); // 設定隨機顏色

            // **將圖片分成區塊，確保線條均勻分布**
            int x1 = (i % 5) * (width / 5) + rand.nextInt(width / 5);  // 分 5 個區塊取隨機 x1
            int y1 = (i % 5) * (height / 5) + rand.nextInt(height / 5); // 分 5 個區塊取隨機 y1
            int x2 = rand.nextInt(width);  // 隨機 x2
            int y2 = rand.nextInt(height); // 隨機 y2

            g.setStroke(new BasicStroke(1.5f)); // 設定線條粗細
            g.drawLine(x1, y1, x2, y2); // 畫線
        }
        g.dispose();
    }

    // 取得隨機顏色
    private static Color getRandomColor() {
        Random rand = new Random();
        return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
    }

    class CustomWordRenderer implements WordRenderer {
        private static final List<Color> textColors = Arrays.asList(Color.BLUE, Color.PINK, Color.GREEN, Color.BLACK);
        private static final List<Font> textFonts = Arrays.asList(
                new Font("Arial", Font.BOLD, 30),
                new Font("Verdana", Font.BOLD, 30),
                new Font("TimesRoman", Font.BOLD, 30)
        );

        @Override
        public void render(String text, BufferedImage image) {
            Graphics2D g = image.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Random rand = new Random();
            int x = 20; // 設定起始 X 位置
            int y = 40; // 設定 Y 位置，讓字體置中

            // **確保每個數字使用不同顏色**
            List<Color> shuffledColors = new java.util.ArrayList<>(textColors);
            Collections.shuffle(shuffledColors); // 打亂顏色順序

            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                g.setFont(textFonts.get(rand.nextInt(textFonts.size()))); // 隨機字體
                g.setColor(shuffledColors.get(i)); // 確保每個數字使用不同顏色
                g.drawString(String.valueOf(c), x, y); // 畫字元
                x += 40; // 設定字距（加大字元間距）
            }
            g.dispose();
        }
    }

    @Test
    @Disabled
    @DisplayName("【Test-002】產生音訊驗證碼 (SimpleCaptcha 套件)")
    void test_002() throws IOException {
        // 1️⃣ 生成 4 位數字驗證碼
        AudioCaptcha ac = new AudioCaptcha.Builder()
                .addAnswer(new NumberTextProducer(4, "0123456789")) // 設定只使用數字且長度為4
                .addVoice()
                .addNoise()
                .build();

        String answer = ac.getAnswer();
        System.out.println("answer = " + answer);

        // IO流
        Sample sample = ac.getChallenge();

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
             OutputStream outputStream = new FileOutputStream(System.getProperty("user.dir") + "/output_files/" + "my_audio.wav")) {
            AudioSystem.write(sample.getAudioInputStream(), AudioFileFormat.Type.WAVE, bos);
            bos.writeTo(outputStream);
        }
    }

}
