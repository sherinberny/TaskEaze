package com.ads.taskeaze.utils;


import static com.ads.taskeaze.utils.ConstantUtils.FILE_PROVIDER_AUTHORITY;
import static com.ads.taskeaze.utils.ConstantUtils.IMAGE_STORAGE_DIRECTORY_NAME;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;


import com.ads.taskeaze.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by user1 on 04-10-2017.
 */

public class ImageUtility {

    public static String convertImageToBase64(Bitmap bitmap, Context context) {
        try {
            String encodedImage = null;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encodedImage = Base64.encodeToString(byteArray, Base64.NO_WRAP);
            return encodedImage;
        } catch (OutOfMemoryError error) {

            Toast.makeText(context, context.getString(R.string.justErrorCode)+" 10", Toast.LENGTH_SHORT).show();
        }
        return null;

    }
    public static void writeToFile(String data)
    {
        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStorageDirectory();

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "imageMapei2.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
//    public static Bitmap loadResizedBitmap(String filename, int width, int height, boolean exact) {
//        Bitmap bitmap = null;
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(filename, options);
//        if (options.outHeight > 0 && options.outWidth > 0) {
//            options.inJustDecodeBounds = false;
//            options.inSampleSize = 2;
//            while (options.outWidth / options.inSampleSize > width
//                    && options.outHeight / options.inSampleSize > height) {
//                options.inSampleSize++;
//            }
//            options.inSampleSize--;
//
//            bitmap = BitmapFactory.decodeFile(filename, options);
//            if (bitmap != null && exact) {
//                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
//            }
//        }
//        return bitmap;
//    }
    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, height, width, true);
    }

    public static Uri saveImageInCachaFolder(String Fname, Context context) {
        checkAndCreateACACHADirectoryForImages();
        File rootPath = new File(Environment.getExternalStorageDirectory(),IMAGE_STORAGE_DIRECTORY_NAME);
        Fname = Fname + ".jpg";
        File file = new File(rootPath, Fname);
        System.out.println("yyyy--------------" + file.getAbsolutePath());

        //        return Uri.fromFile(file);
        return FileProvider.getUriForFile(context,FILE_PROVIDER_AUTHORITY,file);
    }


    public static boolean checkAndCreateACACHADirectoryForImages() {
        File rootPath = new File(Environment.getExternalStorageDirectory(), IMAGE_STORAGE_DIRECTORY_NAME);
        System.out.println("oooooo--------------r1---"+rootPath.getAbsolutePath());
        if (!rootPath.exists()) {
            rootPath.mkdirs();

            return false;
        } else {
            return true;
        }

    }
}
