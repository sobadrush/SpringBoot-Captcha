package com.roger.springbootcaptcha.controller;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.roger.springbootcaptcha.core.CustomWordRenderer;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

/**
 * @author RogerLo
 * @date 2025/3/4
 */
@Controller
@RequestMapping("/captcha")
@RequiredArgsConstructor
public class CaptchaImgController {

    private final DefaultKaptcha verifyCodeProducer;
    private final CustomWordRenderer customWordRenderer;

    /**
     * 使用 Google Kaptcha 產生驗證碼圖片
     */
    @GetMapping(path = "/getVerifyCodeImage")
    public void getVerifyCodeImage(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("image/jpeg"); // 設定為回傳一個 jpg 檔案
        String capText = verifyCodeProducer.createText(); // 建立驗證碼文字
        BufferedImage bi = verifyCodeProducer.createImage(capText);// 使用驗證碼文字建立驗證碼圖片

        HttpSession session = request.getSession();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream(); // 取得 ServletOutputStream 實例
            ImageIO.write(bi, "jpg", out); // 輸出圖片
            out.flush();  // 強制請求清空緩存區
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 自訂驗證碼圖片
     */
    @GetMapping(path = "/getVerifyCodeImage222")
    public void getVerifyCodeImage222(HttpServletRequest request, HttpServletResponse response) {
        response.setDateHeader("Expires", 0);// 禁止 Server 端緩存
        // 設置標準的 HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // 設置IE擴展 HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");// 設置標準 HTTP/1.0 不緩存圖片
        response.setContentType("image/jpeg");// 返回一個 jpeg 圖片，默認是 text/html (輸出文檔的 MIMI 類型)

        // 確保驗證碼文字有效
        String capText = verifyCodeProducer.createText();
        System.out.println("驗證碼內容：" + capText);

        HttpSession session = request.getSession();
        session.setAttribute(Constants.KAPTCHA_SESSION_KEY, capText);

        // 確保圖片大小合理
        BufferedImage buffImg = customWordRenderer.renderWord(capText, 200, 70);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(buffImg, "jpg", out);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}
