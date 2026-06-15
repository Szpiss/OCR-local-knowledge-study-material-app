package com.example.smartscan.ocr;

/**
 * OCR 识别结果回调。
 *
 * ML Kit 是异步识别，不会立即返回结果，所以用回调把成功文本或失败异常通知给 ScanActivity。
 */
public interface OcrResultCallback {
    void onSuccess(String text);

    void onFailure(Exception e);
}
