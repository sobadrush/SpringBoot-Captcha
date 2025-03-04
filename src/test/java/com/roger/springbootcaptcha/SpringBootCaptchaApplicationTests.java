package com.roger.springbootcaptcha;

import com.roger.springbootcaptcha.core.NumberTextProducer;
import nl.captcha.Captcha;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.audio.Sample;
import nl.captcha.backgrounds.FlatColorBackgroundProducer;
import nl.captcha.text.producer.NumbersAnswerProducer;
import nl.captcha.text.renderer.WordRenderer;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.awt.*;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

@ExtendWith(MockitoExtension.class)
class SpringBootCaptchaApplicationTests {

    private final StopWatch stopWatch = new StopWatch();

    @BeforeEach
    void setUp() {
        stopWatch.start();
    }

    @AfterEach
    void tearDown() {
        stopWatch.stop();
        System.out.println("Time elapsed: " + stopWatch.getTime() + " ms");
    }

    @Test
    @Disabled
    @DisplayName("【Test_001】產生圖形驗證碼 (SimpleCaptcha 套件)")
    void test_001() {
        // 增加寬度以支持更寬的字元間距
        int width = 160;
        int height = 40;

        // 自訂字體渲染器，實現多色數字和間距
        WordRenderer multiColorRenderer = new WordRenderer() {
            @Override
            public void render(String word, BufferedImage image) {
                Graphics2D g = image.createGraphics();

                // 設置抗鋸齒效果
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // 使用寬鬆的字體設定
                Font customFont = new Font("Arial", Font.BOLD, 25);
                g.setFont(customFont);

                Color[] digitColors = {
                        new Color(20, 50, 100),    // 深藍
                        new Color(180, 30, 40),    // 暗紅
                        new Color(20, 120, 50),    // 深綠
                        new Color(150, 50, 150)    // 紫色
                };

                // 計算每個字元的初始位置，留出更多間距
                FontMetrics fm = g.getFontMetrics();
                int totalWidth = fm.stringWidth(word);
                int spacing = 20;  // 增加字元間距
                int startX = (image.getWidth() - (totalWidth + spacing * (word.length() - 1))) / 2;
                int y = (image.getHeight() + fm.getAscent()) / 2;

                // 逐字繪製，顏色和位置各不相同
                Random random = new Random();
                for (int i = 0; i < word.length(); i++) {
                    // 為每個數字選擇不同顏色
                    g.setColor(digitColors[i]);

                    // 計算當前字元位置，加入輕微隨機偏移
                    int x = startX + i * (fm.stringWidth(word.substring(0, 1)) + spacing);
                    int yOffset = random.nextInt(10) - 5;  // 上下輕微偏移

                    // 加入輕微旋轉
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.rotate(Math.toRadians(random.nextInt(10) - 5), x, y + yOffset);
                    g2d.drawString(String.valueOf(word.charAt(i)), x, y + yOffset);
                    g2d.dispose();
                }

                g.dispose();
            }
        };

        // 產生驗證碼
        Captcha captcha = new Captcha.Builder(width, height)
                .addText(new NumbersAnswerProducer(4), multiColorRenderer)
                .addBackground(new FlatColorBackgroundProducer(new Color(209, 235, 254))) // 淺藍色背景
                // .gimp(new RippleGimpyRenderer()) // 輕微扭曲效果
                .build();

        // 取得 BufferedImage
        BufferedImage image = captcha.getImage();

        // 增加更多樣化的雜訊線
        this.addComplexNoise(image);

        // 顯示驗證碼文字
        System.out.println(">>> Generated Captcha Text: " + captcha.getAnswer());

        // 儲存圖片
        try {
            File outputDir = new File(System.getProperty("user.dir") + "/output_files");
            outputDir.mkdirs(); // 確保目錄存在
            ImageIO.write(image, "jpg", new File(outputDir, "captcha_output.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 更複雜的雜訊生成方法
    private void addComplexNoise(BufferedImage image) {
        Graphics2D g2d = image.createGraphics();
        Random random = new Random();

        // 準備多種顏色的干擾線
        Color[] noiseColors = {
            new Color(100, 150, 200, 100),  // 淺藍
            new Color(200, 100, 100, 100),  // 淺紅
            new Color(100, 200, 100, 100),  // 淺綠
            new Color(150, 100, 200, 100),  // 淺紫
            new Color(200, 150, 100, 100)   // 淺橙
        };

        // 繪製 15 條變化的干擾線
        for (int i = 0; i < 15; i++) {
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

    // 創建曲線路徑的輔助方法（保持不變）
    private Shape createCurve(int x1, int y1, int x2, int y2) {
        QuadCurve2D curve = new QuadCurve2D.Float();
        int ctrlX = (x1 + x2) / 2;
        int ctrlY = (int) (Math.random() * 50); // 隨機控制點高度
        curve.setCurve(x1, y1, ctrlX, ctrlY, x2, y2);
        return curve;
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
