package me.echodev.resizer.util;

import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Date;

/**
 * Created by K.K. Ho on 3/9/2017.
 * Modified by Łukasz Kiełczykowski on 15/8/2018.
 */

public class FileUtils {
    public static String getOutputFilePath(Bitmap.CompressFormat compressFormat, String outputDirPath, String outputFilename) {
        String targetFileName;
        String targetFileExtension = "." + compressFormat.name().toLowerCase(Locale.US).replace("jpeg", "jpg");

        if (outputFilename == null) {
            targetFileName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date()) + targetFileExtension;
        } else {
            targetFileName = outputFilename + targetFileExtension;
        }

        return outputDirPath + File.separator + targetFileName;
    }

    public static void writeBitmapToFile(Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality, String filePath) throws IOException {
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
    }

    public static String prepareOutputDirectory(
            Bitmap.CompressFormat compressFormat,
            String outputDirPath,
            String outputFilename
    ) {
        File directory = new File(outputDirPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Prepare the new file name and path
        return FileUtils.getOutputFilePath(compressFormat, outputDirPath, outputFilename);
    }
}
