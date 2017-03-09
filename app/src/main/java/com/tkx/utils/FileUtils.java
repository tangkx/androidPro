package com.tkx.utils;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/3/9
 */

public class FileUtils {

    public static BufferedReader getFileBufferedReader(Uri data){

        try {

            String path = data.getPath();
            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader breader = new BufferedReader(reader);

            return breader;

        } catch (Exception e) {
            Log.d("aaa",e.toString());
        }

        return null;
    }
}
