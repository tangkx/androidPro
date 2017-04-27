package com.tkx.first;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tkx.keyboard.KeyboardUtil;
import com.tkx.utils.FileUtils;
import com.tkx.utils.Htools;
import com.tkx.utils.MachineTools;
import com.tkx.utils.Mtools;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/23
 */

public class EditActivity extends Activity implements View.OnClickListener {

    public EditText e_mac, e_asse;
    public Button btn_open_mac, btn_new_mac, btn_save_mac, btn_chg_mac,
            btn_open_asse, btn_new_asse, btn_save_asse, btn_chg_asse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.codelayout);

        initView();
    }

    public void initView() {

        myPermission();

        e_mac = (EditText) findViewById(R.id.edit_mac_code);
        e_asse = (EditText) findViewById(R.id.edit_asse_code);
        e_mac.requestFocus();
        e_asse.requestFocus();

//        e_mac.setInputType(InputType.TYPE_NULL);
//        e_asse.setInputType(InputType.TYPE_NULL);

        e_mac.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardUtil(EditActivity.this,EditActivity.this,e_mac).showKeyboard();
                return false;
            }
        });

        e_asse.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardUtil(EditActivity.this,EditActivity.this,e_asse).showKeyboard();
                return false;
            }
        });


        btn_open_mac = (Button) findViewById(R.id.open_mac_code);
        btn_new_mac = (Button) findViewById(R.id.new_mac_code);
        btn_save_mac = (Button) findViewById(R.id.save_mac_code);
        btn_chg_mac = (Button) findViewById(R.id.change_mac_code);

        btn_open_asse = (Button) findViewById(R.id.open_asse_code);
        btn_new_asse = (Button) findViewById(R.id.new_asse_code);
        btn_save_asse = (Button) findViewById(R.id.save_asse_code);
        btn_chg_asse = (Button) findViewById(R.id.change_asse_code);

        btn_open_mac.setOnClickListener(this);
        btn_new_mac.setOnClickListener(this);
        btn_save_mac.setOnClickListener(this);
        btn_chg_mac.setOnClickListener(this);
        btn_open_asse.setOnClickListener(this);
        btn_new_asse.setOnClickListener(this);
        btn_save_asse.setOnClickListener(this);
        btn_chg_asse.setOnClickListener(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                FileUtils.initPackageFiles();
            }
        }).start();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.open_mac_code:
                List<String> arr = new ArrayList<>();
                arr.add("20");
                arr.add("ff");
                arr.add("21");
                arr.add("1a");
                arr.add("50");
                arr.add("01");
                Intent intent = new Intent();
                intent.putExtra("mac_arr", (Serializable) arr);
                this.setResult(RESULT_OK, intent);
                this.finish();
                break;
            case R.id.new_mac_code:
                MachineTools.showMessageDialog(this,"机器码测试测试");
                break;
            case R.id.save_mac_code:
                String[] macSaveArr = getMacCommand();
                if(macSaveArr != null){
                    showAlertDialog(macSaveArr,1);
                }
                break;
            case R.id.change_mac_code:
                Mtools mt = new Mtools(this);
                String[] macArr = getMacCommand();
                if(macArr != null){
                    List<String> AseList = mt.MACtoASE(macArr);
                    setAseData(AseList);
                }

                break;
            case R.id.open_asse_code:

                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("text/plain");
                startActivityForResult(fileintent, 1);
                break;
            case R.id.new_asse_code:
                myPermission();

                MachineTools.showMessageDialog(this,"汇编程序测试");
                break;
            case R.id.save_asse_code:
                String[] aseArr = getAseCommand();
                if(aseArr != null){
                    showAlertDialog(aseArr,0);
                }

                break;
            case R.id.change_asse_code:
                Htools ht = new Htools(this);
                String[] AseStr = getAseCommand();
                if(AseStr != null){
                    List<String> MacList = ht.ASEtoMAC(AseStr);
                    setMacData(MacList);
                }
                break;
        }

    }

    public void showAlertDialog(final String[] arr, final int f){

        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("保存文件").setView(input).setNegativeButton("取消", null);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String textname = input.getText().toString().trim();
                if(textname.isEmpty()){
                    MachineTools.showMessageDialog(EditActivity.this,"请输入文件名");
                }else{
                    int re = FileUtils.outputFile(arr,textname,f);
                    if(re == -1){
                        MachineTools.showMessageDialog(EditActivity.this,"文件名已存在");
                    }else{
                        MachineTools.showMessageDialog(EditActivity.this,"保存文件成功");
                    }
                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        myPermission();
        Log.d("aaa", "oko");
        if (resultCode == RESULT_OK) {
            Log.d("aaa", "RESULT_OK");
            try {

                BufferedReader breader = FileUtils.getFileBufferedReader(data.getData());

                String str = "";
                while ((str = breader.readLine()) != null) {
                    Log.d("aaa", str);
                }


            } catch (Exception e) {
                Log.d("aaa", e.toString());
            }

        }
    }

    /**
     * 按回车符号分割字符串
     * @param sliptStr
     * @return
     */
    public String[] splitProgram(String sliptStr){
        String []arr = sliptStr.split("\\n");
        return arr;
    }

    /**
     *
     * @return
     */
    public String[] getMacCommand(){
        String []arr = {};
        String MacText = e_mac.getText().toString().trim();
        if(MacText.isEmpty()){
            MachineTools.showMessageDialog(this,"机器代码为空");
            return null;
        }else{
            arr = splitProgram(MacText);
        }

        return arr;
    }

    public String [] getAseCommand(){

        String []arr = {};
        String MacText = e_asse.getText().toString().trim();
        if(MacText.isEmpty()){
            MachineTools.showMessageDialog(this,"汇编代码为空");
            return null;
        }else{
            arr = splitProgram(MacText);
        }

        return arr;
    }

    public void setAseData(List<String> list){
        e_asse.setText("");
        if(list != null){
            for (int i = 0; i < list.size(); i++){
                e_asse.append(list.get(i)+"\n");
            }
        }

    }

    public void setMacData(List<String> list){
        e_mac.setText("");
        if(list != null){
            for (int i = 0; i < list.size(); i++){
                e_mac.append(list.get(i)+"\n");
            }
        }

    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public void myPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
