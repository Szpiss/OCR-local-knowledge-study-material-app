package com.example.smartscan.ocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;

/**
 * OCR 管理类。
 *
 * 答辩重点：
 * 1. 这里封装 Google ML Kit Text Recognition，页面不直接调用 ML Kit。
 * 2. MODE_CHINESE 用于中文教材/题干，MODE_LATIN 用于英文和数字资料。
 * 3. 保留模拟 OCR 文本，是为了现场网络、权限或图片质量异常时仍可演示完整流程。
 */
public class OcrManager {
    public static final int MODE_LATIN = 0;
    public static final int MODE_CHINESE = 1;

    public void recognizeTextFromImage(Context context, Uri imageUri, OcrResultCallback callback) {
        recognizeTextFromImage(context, imageUri, MODE_LATIN, callback);
    }

    public void recognizeTextFromImage(Context context, Uri imageUri, int mode, OcrResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (context == null || imageUri == null) {
            callback.onFailure(new IllegalArgumentException("图片 Uri 为空"));
            return;
        }

        InputImage image;
        try {
            // ML Kit 需要 InputImage 作为输入。这里从相册 Uri 构造 InputImage。
            image = InputImage.fromFilePath(context, imageUri);
        } catch (IOException e) {
            callback.onFailure(e);
            return;
        }

        processImage(image, mode, callback);
    }

    public void recognizeTextFromBitmap(Bitmap bitmap, int mode, OcrResultCallback callback) {
        if (callback == null) {
            return;
        }
        if (bitmap == null) {
            callback.onFailure(new IllegalArgumentException("图片为空"));
            return;
        }
        // 真实 OCR 走这个方法：ScanActivity 把相册图片解码成 Bitmap 后传给 ML Kit。
        processImage(InputImage.fromBitmap(bitmap, 0), mode, callback);
    }

    private void processImage(InputImage image, int mode, OcrResultCallback callback) {
        // 根据用户选择切换识别模型。中文教材默认建议用中文模型。
        TextRecognizer recognizer = mode == MODE_CHINESE
                ? TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build())
                : TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        recognizer.process(image)
                .addOnSuccessListener(text -> callback.onSuccess(text.getText()))
                .addOnFailureListener(callback::onFailure)
                // 识别器用完后关闭，避免多次识别时占用资源。
                .addOnCompleteListener(task -> recognizer.close());
    }

    public String recognizeTextFromImage(String imagePath) {
        // 模拟 OCR 兜底：即使真实 OCR 识别失败，也能演示数据库保存、知识库和错题流程。
        return "设 A 为三阶矩阵，若 A 与对角矩阵相似，则 A 的特征值为主对角线元素。\n"
                + "本题考察矩阵相似、特征值与对角化条件。\n"
                + "复习提示：注意区分“有三个特征值”和“可对角化”的充分必要条件。";
    }
}
