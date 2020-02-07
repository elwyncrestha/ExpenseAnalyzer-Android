package com.elvin.expense_analyzer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

/**
 * @author Elvin Shrestha on 2/7/2020
 */
public class Base64Utils {

    /**
     * Convert Base64 image string to Bitmap image.
     *
     * @param imageString Encoded image string.
     * @return Bitmap image.
     */
    public static Bitmap toImage(String imageString) {
        String ignore = "base64,";
        String image = imageString.substring(imageString.indexOf(ignore) + ignore.length());
        byte[] imageBytes = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

}
