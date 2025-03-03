package com.roger.springbootcaptcha.core;

import com.google.code.kaptcha.GimpyEngine;

import java.awt.image.BufferedImage;

/**
 * @author RogerLo
 * @date 2025/3/4
 */
public class NoDistortion implements GimpyEngine {
    @Override
    public BufferedImage getDistortedImage(BufferedImage image) {
        // 不做任何扭曲，直接返回原图
        return image;
    }
}