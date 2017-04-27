package com.tkx.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.LoginFilter;
import android.util.Log;

import com.tkx.entiys.ProgramEntiys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/3/9
 */

public class FileUtils {

    public static BufferedReader getFileBufferedReader(Uri data) {

        try {

            String path = data.getPath();
            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(inputStream);
            BufferedReader breader = new BufferedReader(reader);

            return breader;

        } catch (Exception e) {
            Log.d("aaa", e.toString());
        }

        return null;
    }

    public static void initDirs() {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        File myRootDir = new File(rootPath + "/AAVcomputerTestFiles");
        myRootDir.mkdirs();
        File aseRootDir = new File(myRootDir, "/aseFiles");
        aseRootDir.mkdirs();
        File macRootDir = new File(myRootDir, "/macFiles");
        macRootDir.mkdirs();
    }

    public static void initPackageFiles() {

//        String aseArr =
//                "LOAD R0,01\r\n" +
//                        "LOAD R1,FF\r\n" +
//                        "LOAD R2,02\r\n" +
//                        "LABEL1:ADD R3,R1,R0\r\n" +
//                        "JMP R3,LABEL2\r\n" +
//                        "ADD R4,R1,R2\r\n" +
//                        "JMP R4,LABEL2\r\n" +
//                        "SHL R0,01\r\n" +
//                        "JMP R2,LABEL2\r\n" +
//                        "SHL R0,08\r\n" +
//                        "NOT R0\r\n" +
//                        "JMP R1,LABEL1\r\n" +
//                        "LABEL2:HALT";
//
//        String macArr =
//                "2007\r\n" +
//                        "2101\r\n" +
//                        "2202\r\n" +
//                        "2300\r\n" +
//                        "5221\r\n" +
//                        "5331\r\n" +
//                        "5232\r\n" +
//                        "8212\r\n" +
//                        "8008\r\n" +
//                        "9000\r\n";
        try {

            initDirs();
            outputFile(ProgramEntiys.ASE_PROGRAM_1,"ase_program_1",0);
            outputFile(ProgramEntiys.MAC_PROGRAM_1,"mac_program_1",1);

        } catch (Exception e) {
            Log.d("result:", e.toString());
        }
    }

    public static int outputFile(String[] files, String filename, int flag) {

        try {

            String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
            OutputStream outputStream;
            File file;
            if (flag == 0) {

                rootPath = rootPath + "/AAVcomputerTestFiles/aseFiles";
                file = new File(rootPath, filename + ".txt");
                if (file.exists()) {
                    return -1;
                } else {
                    file.createNewFile();
                }
                outputStream = new FileOutputStream(file);

                for (int i = 0; i < files.length; i++) {
                    outputStream.write(files[i].getBytes());
                    outputStream.write("\r\n".getBytes());
                }

                outputStream.flush();
                outputStream.close();

            } else {

                rootPath = rootPath + "/AAVcomputerTestFiles/macFiles";
                file = new File(rootPath, filename + ".txt");

                if (file.exists()) {
                    return -1;
                } else {
                    file.createNewFile();
                }
                outputStream = new FileOutputStream(file);

                for (int i = 0; i < files.length; i++) {
                    outputStream.write(files[i].getBytes());
                    outputStream.write("\r\n".getBytes());
                }

                outputStream.flush();
                outputStream.close();
            }

        } catch (Exception e) {
            Log.d("result:", e.toString());
        }

        return 0;

    }
}
