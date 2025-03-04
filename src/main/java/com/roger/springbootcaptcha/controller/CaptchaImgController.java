package com.roger.springbootcaptcha.controller;

import com.roger.springbootcaptcha.utils.CaptchaUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import nl.captcha.audio.AudioCaptcha;
import nl.captcha.audio.Sample;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;

/**
 * @author RogerLo
 * @date 2025/3/4
 */
@Controller
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaImgController {

    private String tempAnswer; // 傳遞答案用，因為 API 測試無 session

    private final CaptchaUtil captchaUtil;

    @GetMapping(path = "/getCaptcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // 設置標準的 HTTP/1.1 no-cache headers.
        response.setHeader("Pragma", "no-cache"); // 設置標準 HTTP/1.0 不緩存圖片
        response.setDateHeader("Expires", 0); // 禁止 Server 端緩存
        response.setContentType("image/png"); // 設定圖片內容類型

        CaptchaUtil.CaptchaData captchaData = captchaUtil.createCaptcha(20, 15);

        // UserContext.CAPTCHA_CODE.set(captchaData.ans());
        tempAnswer = captchaData.ans(); // 傳遞答案給 /getVerifyCodeAudio，for API 測試用，實際上不該如此做!

        // 輸出圖片
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(captchaData.image(), "jpg", out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }


    /**
     * 參考資料：https://blog.csdn.net/qq_37622244/article/details/107381899
     */
    @GetMapping(path = "/getVerifyCodeAudio")
    public void getCaptchaAudio(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate"); // 設置標準的 HTTP/1.1 no-cache headers.
        response.setHeader("Pragma", "no-cache"); // 設置標準 HTTP/1.0 不緩存圖片
        response.setDateHeader("Expires", 0); // 禁止 Server 端緩存
        response.setContentType("audio/wav"); // 設定回應的內容類型為 audio/wav

        // String answer = UserContext.CAPTCHA_CODE.get();
        String answer = tempAnswer;

        // 1️⃣ 生成 4 位數字驗證碼
        AudioCaptcha audioCaptcha = new AudioCaptcha.Builder()
            // .addAnswer(new NumberTextProducer(4, "0123456789")) // 設定只使用數字且長度為4
            .addAnswer(() -> answer) // 設定固定答案
            .addVoice()
            .addNoise()
            .build();

        System.out.println(">>> answer = " + audioCaptcha.getAnswer());

        Sample challenge = audioCaptcha.getChallenge();
        // 透過 ServletOutputStream 輸出音訊
        try (ServletOutputStream out = response.getOutputStream()) {
            AudioSystem.write(challenge.getAudioInputStream(), AudioFileFormat.Type.WAVE, out);
        }
    }

}
