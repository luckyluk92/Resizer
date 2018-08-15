package me.echodev.resizer.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.media.ExifInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by K.K. Ho on 3/9/2017.
 * Modified by Łukasz Kiełczykowski on 15/8/2018.
 */

public class ImageUtils {
    public static File getScaledImage(
            int targetLength,
            int quality, Bitmap.CompressFormat compressFormat,
            String outputDirPath,
            String outputFilename,
            File sourceImage
    ) throws IOException {
        String outputFilePath = FileUtils.prepareOutputDirectory(compressFormat, outputDirPath, outputFilename);

        // Write the resized image to the new file
        Bitmap scaledBitmap = getScaledBitmap(targetLength, sourceImage);
        FileUtils.writeBitmapToFile(scaledBitmap, compressFormat, quality, outputFilePath);

        return new File(outputFilePath);
    }

    public static File getScaledImage(
            @NonNull Context context,
            int targetLength,
            int quality, Bitmap.CompressFormat compressFormat,
            String outputDirPath,
            String outputFilename,
            Uri sourceImageUri
    ) throws IOException {
        String outputFilePath = FileUtils.prepareOutputDirectory(compressFormat, outputDirPath, outputFilename);

        // Write the resized image to the new file
        Bitmap scaledBitmap = getScaledBitmap(context, targetLength, sourceImageUri);
        FileUtils.writeBitmapToFile(scaledBitmap, compressFormat, quality, outputFilePath);

        return new File(outputFilePath);
    }

    public static Bitmap getScaledBitmap(int targetLength, File sourceImage) {
        Size originalSize = obtainBitmapBounds(sourceImage);
        Size targetSize = calculateTargetSize(originalSize, targetLength);
        int rotate = getImageRotation(sourceImage);
        return prepareBitmap(sourceImage, rotate, originalSize, targetSize);
    }

    public static Bitmap getScaledBitmap(@NonNull Context context, int targetLength, Uri sourceImage) throws IOException {
        InputStream imageStream;
        Size originalSize = obtainBitmapBounds(imageStream = openUri(context, sourceImage)); imageStream.close();
        Size targetSize = calculateTargetSize(originalSize, targetLength);
        int rotate = getImageRotation(imageStream = openUri(context, sourceImage)); imageStream.close();
        return prepareBitmap(openUri(context, sourceImage), rotate, originalSize, targetSize);
    }

    private static int getImageRotation(File sourceImage) {
        int rotate = 0;
        try {
            ExifInterface exifReader = new ExifInterface(sourceImage.getAbsolutePath());
            rotate = getImageRotation(exifReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private static int getImageRotation(InputStream sourceImage) {
        int rotate = 0;
        try {
            ExifInterface exifReader = new ExifInterface(sourceImage);
            rotate = getImageRotation(exifReader);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    private static int getImageRotation(ExifInterface exifReader) {
        int rotate = 0;
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
        return rotate;
    }

    private static Size obtainBitmapBounds(File sourceImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sourceImage.getAbsolutePath(), options);

        return new Size(options.outWidth, options.outHeight);
    }

    private static Size obtainBitmapBounds(InputStream sourceImage) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(sourceImage, null, options);
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

    private static Bitmap prepareBitmap(InputStream sourceImage, int rotation, Size originalSize, Size targetSize) {
        Bitmap bitmap = null;
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = (int) Math.floor(originalSize.sizeRatio(targetSize));
            bitmap = BitmapFactory.decodeStream(sourceImage, null, options);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, targetSize.getWidth(), targetSize.getHeight(), matrix, true);
            sourceImage.close();
        } catch (Exception e) {

        }
        return bitmap;
    }

    private static InputStream openUri(@NonNull Context context, @NonNull Uri imageUri) throws FileNotFoundException {
        return context.getContentResolver().openInputStream(imageUri);
    }

}
