package com.tkx.first;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.tkx.keyboard.DBHelper;
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
    public ImageView img_jum;
    private DBHelper db;
    private SharedPreferences share;
    private SharedPreferences.Editor editor;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.codelayout);

        initView();
    }

    public void initView() {

        //myPermission();
        share = getSharedPreferences("Program",MODE_PRIVATE);
        db = new DBHelper(EditActivity.this);

        e_mac = (EditText) findViewById(R.id.edit_mac_code);
        e_asse = (EditText) findViewById(R.id.edit_asse_code);
//
//        e_mac.setShowSoftInputOnFocus(false);
//        e_asse.setShowSoftInputOnFocus(false);
//
//
//        e_mac.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                new KeyboardUtil(EditActivity.this,EditActivity.this,e_mac).showKeyboard();
//                return false;
//            }
//        });
//
//        e_asse.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                new KeyboardUtil(EditActivity.this,EditActivity.this,e_asse).showKeyboard();
//                return false;
//            }
//        });

        e_mac.setFilters(new InputFilter[]{macInputFilter});
        e_asse.setFilters(new InputFilter[]{aseInputFilter});


        btn_open_mac = (Button) findViewById(R.id.open_mac_code);
        btn_new_mac = (Button) findViewById(R.id.new_mac_code);
        btn_save_mac = (Button) findViewById(R.id.save_mac_code);
        btn_chg_mac = (Button) findViewById(R.id.change_mac_code);

        btn_open_asse = (Button) findViewById(R.id.open_asse_code);
        btn_new_asse = (Button) findViewById(R.id.new_asse_code);
        btn_save_asse = (Button) findViewById(R.id.save_asse_code);
        btn_chg_asse = (Button) findViewById(R.id.change_asse_code);

        img_jum = (ImageView) findViewById(R.id.jump_to_load);

        btn_open_mac.setOnClickListener(this);
        btn_new_mac.setOnClickListener(this);
        btn_save_mac.setOnClickListener(this);
        btn_chg_mac.setOnClickListener(this);
        btn_open_asse.setOnClickListener(this);
        btn_new_asse.setOnClickListener(this);
        btn_save_asse.setOnClickListener(this);
        btn_chg_asse.setOnClickListener(this);
        img_jum.setOnClickListener(this);

        int flag = db.query();
        if(flag == 0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUtils.initPackageFiles();
                }
            }).start();
            db.update(1);
            Log.d("result:","ok");
        }

        String macPro = getIntent().getStringExtra("macPro");
        String asePro = getIntent().getStringExtra("asePro");
        Log.d("Pro:",macPro);
        Log.d("Pro:",asePro);
        e_mac.setText(macPro);
        e_asse.setText(asePro);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.open_mac_code:
                Intent macfileintent = new Intent(Intent.ACTION_GET_CONTENT);
                macfileintent.setType("text/plain");
                startActivityForResult(macfileintent, 2);
                break;
            case R.id.new_mac_code:
                Intent codeIntent = new Intent();
                codeIntent.setClass(EditActivity.this,CodeActivity.class);
                startActivity(codeIntent);
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
            case R.id.jump_to_load:

                editor = share.edit();
                String macPro = e_mac.getText().toString().trim();
                String asePro = e_asse.getText().toString().trim();
                Log.d("Program",macPro);
                Log.d("Program",asePro);

                editor.putString("macPro",macPro);
                editor.putString("asePro",asePro);
                editor.commit();

                macArr = getMacCommand();
                if(macArr != null){
                    List<String> macLoad = loadResult(macArr);
                    Intent intent = new Intent();
                    intent.putExtra("mac_arr", (Serializable) macLoad);
                    this.setResult(RESULT_OK, intent);
                    this.finish();
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
        List<String> reslut = new ArrayList<>();
        if (resultCode == RESULT_OK) {

            if(requestCode == 1){
                try {

                    BufferedReader breader = FileUtils.getFileBufferedReader(data.getData());

                    String str = "";
                    while ((str = breader.readLine()) != null) {

                        reslut.add(str);
                    }

                    setAseData(reslut);

                } catch (Exception e) {
                    Log.d("result:", e.toString());
                }
            }else if(requestCode == 2){
                try {

                    BufferedReader breader = FileUtils.getFileBufferedReader(data.getData());

                    String str = "";
                    while ((str = breader.readLine()) != null) {

                        reslut.add(str);
                    }

                    setMacData(reslut);

                } catch (Exception e) {
                    Log.d("result:", e.toString());
                }
            }

        }
    }


    /**
     * 检验机器码是否合格，返回要加载的数据
     * @param macCom
     * @return
     */
    public List<String> loadResult(String[] macCom){

        List<String> res = new ArrayList<>();
        for(int i = 0; i < macCom.length; i++){

            if(macCom[i].length() != 4){
                MachineTools.showMessageDialog(this,"错误行"+i+"机器指令为4位");
                return null;
            }

            String code = macCom[i].substring(0,1);

            switch (code){

                case "1":
                    if(macCom[i].matches("^(1[0-5][0-9a-fA-F]{2})$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "2":
                    if(macCom[i].matches("^(2[0-5][0-9a-fA-F]{2})$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "3":
                    if(macCom[i].matches("^(3[0-5][0-9a-fA-F]{2})$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "4":
                    if(macCom[i].matches("^(40[0-5]{2})$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"寄存器范围0~5");
                        return null;
                    }
                    break;
                case "5":
                    if(macCom[i].matches("^(5[0-5]{3})$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"寄存器范围0~5");
                        return null;
                    }
                    break;
                case "6":
                    if(macCom[i].matches("^(6[0-5]0[0-9a-fA-F])$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"寄存器范围0~5,立即数范围0~f");
                        return null;
                    }
                    break;
                case "7":
                    if(macCom[i].matches("^(7[0-5]00)$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"寄存器范围0~5");
                        return null;
                    }
                    break;
                case "8":
                    if(macCom[i].matches("^(8[0-5][0-9a-fA-F]{2})$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "9":
                    if(macCom[i].matches("^(9000)$")){
                        res.add(macCom[i].substring(0,2));
                        res.add(macCom[i].substring(2,4));
                    }else {
                        MachineTools.showMessageDialog(this,"错误行"+i+"停机指令错误");
                        return null;
                    }
                    break;
                default:
                    MachineTools.showMessageDialog(this,"错误行"+i+"未知指令错误");
                    if(1 == 1){
                        return null;
                    }
                    break;
            }
        }

        return res;
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
                Log.d("result:",list.get(i));
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

    //机器码文本字符过滤
    InputFilter macInputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            String regex = "^[0-9a-fA-F\\r\\n]+$";

            if(!(source.toString().matches(regex))){
                return "";
            }
            return null;
        }
    };

    //汇编程序文本字符过滤
    InputFilter aseInputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            String regex = "^[0-9a-zA-Z\\r\\n\\[,\\]:\\d\\s]+$";

            if(!(source.toString().matches(regex))){
                return "";
            }
            return null;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if(event.getAction() == KeyEvent.ACTION_DOWN){
            if(keyCode == KeyEvent.KEYCODE_BACK){

                if((e_mac.getText().toString().equals("")) &&(e_asse.getText().toString().equals(""))){
                    finish();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("是否要保存文件");
                    builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            flag = true;
                        }
                    }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    builder.show();
                }

                return flag;

            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
