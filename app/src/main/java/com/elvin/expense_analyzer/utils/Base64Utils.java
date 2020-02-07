package com.elvin.expense_analyzer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

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

    /**
     * Convert Bitmap to Base64 string.
     *
     * @param bitmap Bitmap image.
     * @return Image string.
     */
    public static String toImageString(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] image = stream.toByteArray();
        StringBuilder builder = new StringBuilder("data:image/jpg;base64,");
        builder.append(Base64.encodeToString(image, Base64.DEFAULT));
        return builder.toString();
    }

}
