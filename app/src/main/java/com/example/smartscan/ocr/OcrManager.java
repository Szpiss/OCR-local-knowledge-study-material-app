package com.example.smartscan.ocr;

/**
 * OCR 管理类。第一阶段返回模拟文本，后续可在此接入 Google ML Kit Text Recognition。
 */
public class OcrManager {
    public String recognizeTextFromImage(String imagePath) {
        return "设 A 为三阶矩阵，若 A 与对角矩阵相似，则 A 的特征值为主对角线元素。\n"
                + "本题考察矩阵相似、特征值与对角化条件。\n"
                + "复习提示：注意区分“有三个特征值”和“可对角化”的充分必要条件。";
    }
}
