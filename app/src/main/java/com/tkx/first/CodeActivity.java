package com.tkx.first;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.guoqi.iosdialog.IOSDialog;
import com.tkx.keyboard.DBHelper;
import com.tkx.utils.FileUtils;
import com.tkx.utils.HelpDialog;
import com.tkx.utils.Htools;
import com.tkx.utils.MachineTools;
import com.tkx.utils.Mtools;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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
    private EditText mac_linenumber, ase_linenumber;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ImageView img_open_nav, img_back, img_load, com_hlep, mac_del, ase_del;
    private DBHelper db;
    private SharedPreferences share;
    private SharedPreferences.Editor editor;
    private ArrayAdapter<String> adapter;
    private HashMap<String, String> files;
    private String helpText = "";
    private String commandText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editlayout);

        initView();


    }

    @TargetApi(Build.VERSION_CODES.M)
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

        e_mac.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() != 0) {
                    String str = "";
                    for (int i = 0; i < e_mac.getLineCount(); i++) {
                        str = str + (i + 1) + "\n";
                    }
                    mac_linenumber.setText(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        e_mac.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                mac_linenumber.scrollTo(scrollX, scrollY);
            }
        });

        e_ase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    String str = "";
                    for (int i = 0; i < e_ase.getLineCount(); i++) {
                        str = str + (i + 1) + "\n";
                    }
                    ase_linenumber.setText(str);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        e_ase.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                ase_linenumber.scrollTo(scrollX, scrollY);
            }
        });

        mac_linenumber = (EditText) findViewById(R.id.mac_linenumber);
        mac_linenumber.setFocusable(false);
        mac_linenumber.setEnabled(false);

        ase_linenumber = (EditText) findViewById(R.id.ase_linenumber);
        ase_linenumber.setFocusable(false);
        ase_linenumber.setEnabled(false);

        img_open_nav = (ImageView) findViewById(R.id.open_nav_view);
        img_open_nav.setOnClickListener(this);
        img_back = (ImageView) findViewById(R.id.back_front);
        img_back.setOnClickListener(this);
        img_load = (ImageView) findViewById(R.id.load_code);
        img_load.setOnClickListener(this);
        com_hlep = (ImageView) findViewById(R.id.com_hlep);
        com_hlep.setOnClickListener(this);
        mac_del = (ImageView) findViewById(R.id.mac_del);
        mac_del.setOnClickListener(this);
        ase_del = (ImageView) findViewById(R.id.ase_del);
        ase_del.setOnClickListener(this);

        int flag = db.query();
        if (flag == 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FileUtils.initPackageFiles();
                }
            }).start();
            db.update(1);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                helpText = getHelpFile("help.txt");
                commandText = getHelpFile("command.txt");
            }
        }).start();

        String macPro = getIntent().getStringExtra("macPro");
        String asePro = getIntent().getStringExtra("asePro");
        int macline = getIntent().getIntExtra("macline", 1);
        int aseline = getIntent().getIntExtra("aseline", 1);
        Log.d("222222222", "" + macline);
        e_mac.setText(macPro);
        e_ase.setText(asePro);

        for (int i = 0; i < macline; i++) {
            mac_linenumber.append("" + (i + 1) + "\n");
        }

        for (int i = 0; i < aseline; i++) {
            ase_linenumber.append("" + (i + 1) + "\n");
        }
//        mac_linenumber.setText(macline);
//        ase_linenumber.setText(aseline);
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

    @Override
    protected void onResume() {
        super.onResume();

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
                showAlertDialog(macSaveArr, 1, FileUtils.MAC_FILE_DIR);
            }

        } else if (id == R.id.mac_to_ase) {//反汇编

            Mtools mt = new Mtools(this);
            String[] macArr = getMacCommand();
            if (macArr != null) {
                List<String> AseList = mt.MACtoASE(macArr);
                if (AseList != null) {
                    setAseData(AseList);
                    final IOSDialog builder = new IOSDialog(this).builder();
                    builder.setMsg(".....反编译成功.....");
                    builder.setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
                    builder.show();
                }

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
                if (MacList != null) {
                    setMacData(MacList);
                    final IOSDialog builder = new IOSDialog(this).builder();
                    builder.setMsg(".....编译成功.....");
                    builder.setNegativeButton("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            builder.dismiss();
                        }
                    });
                    builder.show();
                }

            }

        } else if (id == R.id.help) {
            final HelpDialog dialog = new HelpDialog(this);
            LayoutInflater inflater = getLayoutInflater();
            LinearLayout view = (LinearLayout) inflater.inflate(R.layout.helpdialog, null);
            ImageView cancel = (ImageView) view.findViewById(R.id.help_cancle);
            EditText editText = (EditText) view.findViewById(R.id.help_text);
            editText.setText(helpText);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (dialog != null) {
                        dialog.cancel();
                    }
                }
            });

            dialog.show();
            dialog.setCancelable(false);
            dialog.setContentView(view);


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 保存文件对话框
     *
     * @param arr
     * @param f
     * @param path
     */
    public void showAlertDialog(final String[] arr, final int f, final String path) {

        final EditText input = new EditText(this);
        input.setBackground(getResources().getDrawable(R.drawable.editshape));
        final IOSDialog dialog = new IOSDialog(this).builder();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog.setTitle("保存文件").setView(input).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setPositiveButton("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textname = input.getText().toString().trim();
                if (textname.isEmpty()) {
                    MachineTools.showMessageDialog(CodeActivity.this, "请输入文件名");
                } else {
                    boolean flag = FileUtils.checkFileReapt(textname, path);
                    if (flag) {
                        final IOSDialog alert = new IOSDialog(CodeActivity.this).builder();
                        alert.setTitle("文件已存在，是否覆盖");
                        alert.setPositiveButton("是", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String textname = input.getText().toString().trim();
                                String re = FileUtils.outputFile(arr, textname, f);

                                MachineTools.showMessageDialog(CodeActivity.this, "保存文件成功\n文件路径:" + re);
                            }
                        });
                        alert.setNegativeButton("否", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alert.dismiss();
                            }
                        });
                        alert.show();

                    } else {
                        String re = FileUtils.outputFile(arr, textname, f);
                        MachineTools.showMessageDialog(CodeActivity.this, "保存文件成功\n文件路径:" + re);

                    }

                }
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        List<String> reslut = new ArrayList<>();
        if (resultCode == RESULT_OK) {

            BufferedReader breader = FileUtils.getFileBufferedReader(data.getData());

            if (requestCode == 1) {
                try {

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

                    // BufferedReader breader = FileUtils.getFileBufferedReader(data.getData());

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
            case R.id.load_code:

                if (FileUtils.isInitFlag()) {

                    final IOSDialog dialog = new IOSDialog(this).builder();
                    dialog.setMsg("加载程序会初始化寄存器，是否要执行当前操作");
                    dialog.setNegativeButton("否", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("是", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            message.sendToTarget();
                        }
                    });
                    dialog.show();

                } else {

                    editor = share.edit();
                    macPro = e_mac.getText().toString().trim();
                    asePro = e_ase.getText().toString().trim();
                    int mac_line = e_mac.getLineCount();
                    int ase_line = e_ase.getLineCount();

                    editor.putString("macPro", macPro);
                    editor.putString("asePro", asePro);
                    editor.putInt("macline", mac_line);
                    editor.putInt("aseline", ase_line);
                    editor.commit();

                    String[] macArr = getMacCommand();
                    if (macArr != null) {
                        List<String> macLoad = loadResult(macArr);
                        if (macLoad != null) {
                            Intent intent = new Intent();
                            intent.putExtra("mac_arr", (Serializable) macLoad);
                            this.setResult(RESULT_OK, intent);
                            this.finish();
                        }

                    }
                }

                FileUtils.setInitFlag(true);
                break;
            case R.id.com_hlep:
                final HelpDialog dialog = new HelpDialog(this);
                LayoutInflater inflater = getLayoutInflater();
                LinearLayout view = (LinearLayout) inflater.inflate(R.layout.comdialog, null);
                ImageView cancel = (ImageView) view.findViewById(R.id.com_cancle);
                EditText editText = (EditText) view.findViewById(R.id.com_text);
                editText.setText(commandText);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                    }
                });

                dialog.show();
                dialog.setCancelable(false);
                dialog.setContentView(view);
                break;
            case R.id.mac_del:
                final IOSDialog delmacdialog = new IOSDialog(this).builder();
                delmacdialog.setMsg("确定要清空机器代码吗？");
                delmacdialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delmacdialog.dismiss();
                    }
                }).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mac_linenumber.setText("1");
                        e_mac.setText("");
                    }
                }).show();
                break;
            case R.id.ase_del:
                final IOSDialog delasedialog = new IOSDialog(this).builder();
                delasedialog.setMsg("确定要清空汇编代码吗？");
                delasedialog.setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        delasedialog.dismiss();
                    }
                }).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ase_linenumber.setText("1");
                        e_ase.setText("");
                    }
                }).show();
                break;
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    editor = share.edit();
                    String macPro = e_mac.getText().toString().trim();
                    String asePro = e_ase.getText().toString().trim();
                    int mac_line = e_mac.getLineCount();
                    int ase_line = e_ase.getLineCount();

                    editor.putString("macPro", macPro);
                    editor.putString("asePro", asePro);
                    editor.putInt("macline", mac_line);
                    editor.putInt("aseline", ase_line);
                    editor.commit();

                    String[] macArr = getMacCommand();
                    if (macArr != null) {
                        List<String> macLoad = loadResult(macArr);
                        if (macLoad != null) {
                            Intent intent = new Intent();
                            intent.putExtra("mac_arr", (Serializable) macLoad);
                            CodeActivity.this.setResult(RESULT_OK, intent);
                            CodeActivity.this.finish();
                        }

                    }
                    break;
            }
        }
    };

    /**
     * 检验机器码是否合格，返回要加载的数据
     *
     * @param macCom
     * @return
     */
    public List<String> loadResult(String[] macCom) {

        List<String> res = new ArrayList<>();
        for (int i = 0; i < macCom.length; i++) {
            if(macCom[i].toString().isEmpty()){
                continue;
            }
            if (macCom[i].length() != 4) {
                MachineTools.showMessageDialog(this, "错误行" + i + ":机器指令为4位");
                return null;
            }

            String code = macCom[i].substring(0, 1).toUpperCase();

            switch (code) {

                case "1":
                    if (macCom[i].matches("^(1[0-5][0-9a-fA-F]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "2":
                    if (macCom[i].matches("^(2[0-5][0-9a-fA-F]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "3":
                    if (macCom[i].matches("^(3[0-5][0-9a-fA-F]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "4":
                    if (macCom[i].matches("^(40[0-5]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "5":
                    if (macCom[i].matches("^(5[0-5]{3})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "6":
                    if (macCom[i].matches("^(6[0-5]0[0-9a-fA-F])$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5,立即数范围0~f");
                        return null;
                    }
                    break;
                case "7":
                    if (macCom[i].matches("^(7[0-5]00)$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "8":
                    if (macCom[i].matches("^(8[0-5][0-9a-fA-F]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5,立即数范围00~ff");
                        return null;
                    }
                    break;
                case "9":
                    if (macCom[i].matches("^(9000)$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":停机指令错误");
                        return null;
                    }
                    break;
                case "A":
                    if (macCom[i].matches("^([aA]0[0-5]{2})$")) {
                        res.add(macCom[i].substring(0, 2));
                        res.add(macCom[i].substring(2, 4));
                    } else {
                        MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                default:
                    MachineTools.showMessageDialog(this, "错误行" + (i + 1) + ":未知指令");
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
        if (e_ase.getText().toString().isEmpty()) {

            final IOSDialog iosDialog = new IOSDialog(this).builder();
            iosDialog.setMsg("文件中存在非法字符，无法导入");
            iosDialog.setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iosDialog.dismiss();
                }
            });
            iosDialog.show();
        }

    }

    public void setMacData(List<String> list) {
        e_mac.setText("");
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (!list.get(i).isEmpty()) {
                    e_mac.append(list.get(i) + "\n");
                }

            }
        }

        if (e_mac.getText().toString().isEmpty()) {
            final IOSDialog iosDialog = new IOSDialog(this).builder();
            iosDialog.setMsg("文件中存在非法字符，无法导入");
            iosDialog.setPositiveButton("确定", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    iosDialog.dismiss();
                }
            });
            iosDialog.show();
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
                int mac_line = e_mac.getLineCount();
                int ase_line = e_ase.getLineCount();

                editor.putString("macPro", macPro);
                editor.putString("asePro", asePro);
                editor.putInt("macline", mac_line);
                editor.putInt("aseline", ase_line);
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

    public String getHelpFile(String name) {
        int k = 0;
        String res = "";
        try {

            InputStreamReader inputreader = new InputStreamReader(getResources().getAssets().open(name));
            BufferedReader reader = new BufferedReader(inputreader);
            String line = "";
            while ((line = reader.readLine()) != null) {
                if (k == 0) {
                    res += line;
                } else {
                    res += "\r\n" + line;
                }
                k++;
            }
            reader.close();
            inputreader.close();

        } catch (Exception e) {

        }
        return res;
    }
}
