package com.ashvinprajapati.skillconnect.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UriToFileUtil {

    public static File getFileFromUri(Context context, Uri uri) {
        File file = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            String fileName = getFileName(context, uri);

            InputStream inputStream = contentResolver.openInputStream(uri);
            file = new File(context.getCacheDir(), fileName);
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[8192];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }

    private static String getFileName(Context context, Uri uri) {
        String result = "temp_file_" + System.currentTimeMillis();
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        } else {
            result = uri.getLastPathSegment();
        }
        return result;
    }
}
