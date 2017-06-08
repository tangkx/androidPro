package com.tkx.utils;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.tkx.entiys.ProgramEntiys;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/3/9
 */

public class FileUtils {

    public static final String MAC_FILE_DIR = Environment.getExternalStorageDirectory().
            getAbsolutePath().toString()+ "/AAVcomputerFiles"+"/macFiles";
    public static final String ASE_FILE_DIR = Environment.getExternalStorageDirectory().
            getAbsolutePath().toString()+ "/AAVcomputerFiles"+"/aseFiles";
    public static  boolean INIT_FLAG = false;

    public static boolean isInitFlag() {
        return INIT_FLAG;
    }

    public static void setInitFlag(boolean initFlag) {
        INIT_FLAG = initFlag;
    }

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
        File myRootDir = new File(rootPath + "/AAVcomputerFiles");
        myRootDir.mkdirs();
        File aseRootDir = new File(myRootDir, "/aseFiles");
        aseRootDir.mkdirs();
        File macRootDir = new File(myRootDir, "/macFiles");
        macRootDir.mkdirs();
    }

    public static void initPackageFiles() {

        try {

            initDirs();
            outputFile(ProgramEntiys.ASE_PROGRAM_1,"ase_program_1",0);
            outputFile(ProgramEntiys.ASE_PROGRAM_2,"ase_program_2",0);
            outputFile(ProgramEntiys.MAC_PROGRAM_1,"mac_program_1",1);
            outputFile(ProgramEntiys.MAC_PROGRAM_2,"mac_program_2",1);
            outputFile(ProgramEntiys.MAC_PROGRAM_3,"mac_program_3",1);

        } catch (Exception e) {
            Log.d("result:", e.toString());
        }
    }

    public static List<String> getFilesForPath(String path){

        List<String> files = new ArrayList<>();
        try{
            File file = new File(path);
            InputStream inputStream = new FileInputStream(file);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputreader);
            String line;
            while((line = reader.readLine()) != null){
                files.add(line);
            }

            inputStream.close();
            inputreader.close();
            reader.close();
        }catch (Exception e){

        }

        return files;

    }

    public static boolean checkFileReapt(String name,String path){

        boolean flag = false;
        File file = new File(path);
        if(!file.exists()){
            initDirs();
        }

        File[] files = file.listFiles();
        for(int i = 0; i < files.length; i++){
            File f = files[i];
            String key = f.getName();
            if(key.equals(name+".txt")){
                flag = true;
                return flag;
            }
        }

        return flag;
    }

    public static String outputFile(String[] files, String filename, int flag) {

        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath().toString();
        OutputStream outputStream;
        File file = null;
        try {

            if (flag == 0) {

                rootPath = rootPath + "/AAVcomputerFiles/aseFiles";
                file = new File(rootPath, filename + ".txt");
                outputStream = new FileOutputStream(file,false);

                for (int i = 0; i < files.length; i++) {
                    outputStream.write(files[i].getBytes());
                    outputStream.write("\r\n".getBytes());
                }

                outputStream.flush();
                outputStream.close();

            } else {

                rootPath = rootPath + "/AAVcomputerFiles/macFiles";
                file = new File(rootPath, filename + ".txt");

                outputStream = new FileOutputStream(file,false);

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

        return file.getAbsolutePath().toString();

    }
}
