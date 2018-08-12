package me.echodev.resizer.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import java.io.File;
import java.io.IOException;

/**
 * Created by K.K. Ho on 3/9/2017.
 */

public class ImageUtils {
    public static File getScaledImage(
            int targetLength,
            int quality, Bitmap.CompressFormat compressFormat,
            String outputDirPath,
            String outputFilename,
            File sourceImage
    ) throws IOException {
        File directory = new File(outputDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Prepare the new file name and path
        String outputFilePath = FileUtils.getOutputFilePath(compressFormat, outputDirPath, outputFilename, sourceImage);

        // Write the resized image to the new file
        Bitmap scaledBitmap = getScaledBitmap(targetLength, sourceImage);
        FileUtils.writeBitmapToFile(scaledBitmap, compressFormat, quality, outputFilePath);

        return new File(outputFilePath);
    }

    public static Bitmap getScaledBitmap(int targetLength, File sourceImage) {
        Size originalSize = obtainBitmapBounds(sourceImage);
        Size targetSize = calculateTargetSize(originalSize, targetLength);
        int rotate = 0;

        try {
            ExifInterface exifReader = new ExifInterface(sourceImage.getAbsolutePath());
            int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return prepareBitmap(sourceImage, rotate, originalSize, targetSize);
    }

    private static Size obtainBitmapBounds(File sourceImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourceImage.getAbsolutePath(), options);

        return new Size(options.outWidth, options.outHeight);
    }

    private static Size calculateTargetSize(Size size, int targetLength) {
        float aspectRatio = (float) size.getWidth() / size.getHeight();
        if (targetLength > size.getWidth() && targetLength > size.getHeight()) {
            return new Size(size);
        } else if (size.getWidth() > size.getHeight()) {
            return new Size(
                    targetLength,
                    Math.round(targetLength / aspectRatio)
            );
        } else {
            aspectRatio = 1 / aspectRatio;
            return new Size(
                    Math.round(targetLength / aspectRatio),
                    targetLength
            );
        }
    }

    private static Bitmap prepareBitmap(File sourceImage, int rotation, Size originalSize, Size targetSize) {
        Bitmap bitmap = null;
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) Math.floor(originalSize.sizeRatio(targetSize));
            bitmap = BitmapFactory.decodeFile(sourceImage.getAbsolutePath(), options);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, targetSize.getWidth(), targetSize.getHeight(), matrix, true);
        } catch (Exception e) {

        }
        return bitmap;
    }
}
