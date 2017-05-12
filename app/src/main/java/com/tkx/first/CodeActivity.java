package com.tkx.first;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.tkx.keyboard.DBHelper;
import com.tkx.utils.FileUtils;
import com.tkx.utils.Htools;
import com.tkx.utils.MachineTools;
import com.tkx.utils.Mtools;

import java.io.BufferedReader;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by tkx on 2017/5/8.
 */

public class CodeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private EditText e_mac, e_ase;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView img_open_nav, img_back;
    private DBHelper db;
    private SharedPreferences share;
    private SharedPreferences.Editor editor;
    boolean flag = false;
    private  ArrayAdapter<String> adapter;
    private HashMap<String, String> files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editlayout);

        initView();

    }

    public void initView() {

        share = getSharedPreferences("Program", MODE_PRIVATE);
        db = new DBHelper(CodeActivity.this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        e_mac = (EditText) findViewById(R.id.mac_edit_code);
        e_ase = (EditText) findViewById(R.id.ase_edit_code);

        e_mac.setFilters(new InputFilter[]{macInputFilter});
        e_ase.setFilters(new InputFilter[]{aseInputFilter});

        img_open_nav = (ImageView) findViewById(R.id.open_nav_view);
        img_open_nav.setOnClickListener(this);
        img_back = (ImageView) findViewById(R.id.back_front);
        img_back.setOnClickListener(this);

        int flag = db.query();
        if (flag == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUtils.initPackageFiles();
                }
            }).start();
            db.update(1);
            Log.d("result:", "ok");
        }

        String macPro = getIntent().getStringExtra("macPro");
        String asePro = getIntent().getStringExtra("asePro");
        Log.d("Pro:", macPro);
        Log.d("Pro:", asePro);
        e_mac.setText(macPro);
        e_ase.setText(asePro);
    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.mac_open) {//打开机器码文件

            files = getCurFiles(FileUtils.MAC_FILE_DIR);
            Iterator iterator = files.entrySet().iterator();
            ArrayList<String> fileList = new ArrayList<>();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                fileList.add((String) entry.getKey());
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.open_file);
            builder.setTitle("打开文件");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String key = adapter.getItem(which);
                    String path = files.get(key);
                    List<String> list = FileUtils.getFilesForPath(path);
                    setMacData(list);
                }
            });

            builder.setPositiveButton("其它", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent macfileintent = new Intent(Intent.ACTION_GET_CONTENT);
                    macfileintent.setType("text/plain");
                    startActivityForResult(macfileintent, 2);
                }
            });

            builder.show();


        } else if (id == R.id.mac_save) {//保存机器文件

            String[] macSaveArr = getMacCommand();
            if (macSaveArr != null) {
                showAlertDialog(macSaveArr, 1,FileUtils.MAC_FILE_DIR);
            }

        } else if (id == R.id.mac_to_ase) {//反汇编

            Mtools mt = new Mtools(this);
            String[] macArr = getMacCommand();
            if (macArr != null) {
                List<String> AseList = mt.MACtoASE(macArr);
                setAseData(AseList);
            }

        } else if (id == R.id.ase_open) {//打开汇编文件

            files = getCurFiles(FileUtils.ASE_FILE_DIR);
            Iterator iterator = files.entrySet().iterator();
            ArrayList<String> fileList = new ArrayList<>();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                fileList.add((String) entry.getKey());
            }
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.open_file);
            builder.setTitle("打开文件");
            builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String key = adapter.getItem(which);
                    String path = files.get(key);
                    List<String> list = FileUtils.getFilesForPath(path);
                    setAseData(list);
                }
            });

            builder.setPositiveButton("其它", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent macfileintent = new Intent(Intent.ACTION_GET_CONTENT);
                    macfileintent.setType("text/plain");
                    startActivityForResult(macfileintent, 1);
                }
            });

            builder.show();

        } else if (id == R.id.ase_save) {//保存汇编文件

            String[] aseArr = getAseCommand();
            if (aseArr != null) {
                showAlertDialog(aseArr, 0, FileUtils.ASE_FILE_DIR);
            }

        } else if (id == R.id.ase_to_mac) {//编译

            Htools ht = new Htools(this);
            String[] AseStr = getAseCommand();
            if (AseStr != null) {
                List<String> MacList = ht.ASEtoMAC(AseStr);
                setMacData(MacList);
            }

        } else if (id == R.id.load_code) {

            editor = share.edit();
            String macPro = e_mac.getText().toString().trim();
            String asePro = e_ase.getText().toString().trim();
            Log.d("Program", macPro);
            Log.d("Program", asePro);

            editor.putString("macPro", macPro);
            editor.putString("asePro", asePro);
            editor.commit();

            String[] macArr = getMacCommand();
            if (macArr != null) {
                List<String> macLoad = loadResult(macArr);
                if(macLoad != null){
                    Intent intent = new Intent();
                    intent.putExtra("mac_arr", (Serializable) macLoad);
                    this.setResult(RESULT_OK, intent);
                    this.finish();
                }

            }

        } else if (id == R.id.help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showAlertDialog(final String[] arr, final int f, final String path) {

        final EditText input = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("保存文件").setView(input).setNegativeButton("取消", null);
        builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                String textname = input.getText().toString().trim();
                if (textname.isEmpty()) {
                    MachineTools.showMessageDialog(CodeActivity.this, "请输入文件名");
                } else {
                    boolean flag = FileUtils.checkFileReapt(textname,path);
                    if(flag){
                        AlertDialog.Builder alert = new AlertDialog.Builder(CodeActivity.this);
                        alert.setTitle("文件已存在，是否覆盖");
                        alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String textname = input.getText().toString().trim();
                                int re = FileUtils.outputFile(arr, textname, f);

                                MachineTools.showMessageDialog(CodeActivity.this, "保存文件成功");

                            }
                        });
                        alert.setNegativeButton("否",null);
                        alert.show();


                    }else {
                        int re = FileUtils.outputFile(arr, textname, f);
                        MachineTools.showMessageDialog(CodeActivity.this, "保存文件成功");

                    }

                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        List<String> reslut = new ArrayList<>();
        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {
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
            } else if (requestCode == 2) {
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

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.open_nav_view:
                drawer.openDrawer(GravityCompat.START);
                break;

            case R.id.back_front:
                editor = share.edit();
                String macPro = e_mac.getText().toString().trim();
                String asePro = e_ase.getText().toString().trim();
                Log.d("Program", macPro);
                Log.d("Program", asePro);

                editor.putString("macPro", macPro);
                editor.putString("asePro", asePro);
                editor.commit();

                finish();
                break;
        }
    }

    /**
     * 检验机器码是否合格，返回要加载的数据
     *
     * @param macCom
     * @return
     */
    public List<String> loadResult(String[] macCom) {

        List<String> res = new ArrayList<>();
        for (int i = 0; i < macCom.length; i++) {

            if (macCom[i].length() != 4) {
                MachineTools.showMessageDialog(this, "错误行" + i + "机器指令为4位");
                return null;
            }

            String code = macCom[i].substring(0, 1);

            switch (code) {

                case "1":
                    if (macCom[i].matches("^(1[0-5][0-9a-fA-F]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "2":
                    if (macCom[i].matches("^(2[0-5][0-9a-fA-F]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "3":
                    if (macCom[i].matches("^(3[0-5][0-9a-fA-F]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "4":
                    if (macCom[i].matches("^(40[0-5]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "寄存器范围0~5");
                        return null;
                    }
                    break;
                case "5":
                    if (macCom[i].matches("^(5[0-5]{3})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "寄存器范围0~5");
                        return null;
                    }
                    break;
                case "6":
                    if (macCom[i].matches("^(6[0-5]0[0-9a-fA-F])$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "寄存器范围0~5,立即数范围0~f");
                        return null;
                    }
                    break;
                case "7":
                    if (macCom[i].matches("^(7[0-5]00)$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "寄存器范围0~5");
                        return null;
                    }
                    break;
                case "8":
                    if (macCom[i].matches("^(8[0-5][0-9a-fA-F]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "9":
                    if (macCom[i].matches("^(9000)$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i+1) + "停机指令错误");
                        return null;
                    }
                    break;
                default:
                    MachineTools.showMessageDialog(this, "错误行" + (i+1) + "未知指令");
                    if (1 == 1) {
                        return null;
                    }
                    break;
            }
        }

        return res;
    }

    /**
     * 按回车符号分割字符串
     *
     * @param sliptStr
     * @return
     */
    public String[] splitProgram(String sliptStr) {
        String[] arr = sliptStr.split("\\n");
        return arr;
    }

    /**
     * @return
     */
    public String[] getMacCommand() {
        String[] arr = {};
        String MacText = e_mac.getText().toString().trim();
        if (MacText.isEmpty()) {
            MachineTools.showMessageDialog(this, "机器代码为空");
            return null;
        } else {
            arr = splitProgram(MacText);
        }

        return arr;
    }

    public String[] getAseCommand() {

        String[] arr = {};
        String MacText = e_ase.getText().toString().trim();
        if (MacText.isEmpty()) {
            MachineTools.showMessageDialog(this, "汇编代码为空");
            return null;
        } else {
            arr = splitProgram(MacText);
        }

        return arr;
    }

    public void setAseData(List<String> list) {
        e_ase.setText("");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Log.d("result:", list.get(i));
                e_ase.append(list.get(i) + "\n");
            }
        }

    }

    public void setMacData(List<String> list) {
        e_mac.setText("");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                e_mac.append(list.get(i) + "\n");
            }
        }

    }

    //机器码文本字符过滤
    InputFilter macInputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            String regex = "^[0-9a-fA-F\\r\\n]+$";

            if (!(source.toString().matches(regex))) {
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

            if (!(source.toString().matches(regex))) {
                return "";
            }
            return null;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {

                editor = share.edit();
                String macPro = e_mac.getText().toString().trim();
                String asePro = e_ase.getText().toString().trim();
                Log.d("Program", macPro);
                Log.d("Program", asePro);

                editor.putString("macPro", macPro);
                editor.putString("asePro", asePro);
                editor.commit();

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public HashMap<String, String> getCurFiles(String path) {

        HashMap<String, String> fileList = new HashMap<>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null) {

            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                fileList.put(f.getName(), f.getPath());

            }
        }

        return fileList;
    }
}
