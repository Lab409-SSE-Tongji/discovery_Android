package com.example.administrator.discovery_android.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageCodingUtil {
    public static ByteArrayOutputStream compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        double size = baos.toByteArray().length / 1024;
        double maxSize = 100;
        double i = size / maxSize;

        float width = image.getWidth();
        float height = image.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = (float) (image.getWidth() / Math.sqrt(i)) / width;
        float scaleHeight = (float) (image.getWidth() / Math.sqrt(i)) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(image, 0, 0, (int) width,
                (int) height, matrix, true);
        baos.reset();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos;
    }

    public static Bitmap getBitmapFromString(String str){
        byte[] arr = Base64.decode(str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(arr, 0, arr.length);
    }
}
