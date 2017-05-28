package com.tkx.first;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.tkx.entiys.Register;
import com.tkx.entiys.SimulateData;
import com.tkx.entiys.SimulateObject;
import com.tkx.keyboard.DBHelper;
import com.tkx.utils.CountRegister;
import com.tkx.utils.FileUtils;
import com.tkx.utils.MachineTools;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView img_jump;
    private ListView mList;
    private ListAdapter lAdapter;
    private List<SimulateData> simList;
    private EditText e_r0, e_r1, e_r2, e_r3, e_r4, e_r5, e_account, e_command;
    private Button btn_init, btn_action, btn_auto_action;
    private Message message;
    private String accountNum;
    private boolean init_flag, action_flag;
    private DBHelper db;
    private SharedPreferences sharePre;
    private  SharedPreferences.Editor editor;
    private MyBroadcast broadcast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();

    }

    /**
     * 初始化布局
     */
    public void initView() {

        broadcast = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.broadcast");
        registerReceiver(broadcast,filter);

        myPermission();
        db = new DBHelper(MainActivity.this);

        sharePre = getSharedPreferences("Program",MODE_PRIVATE);
        editor = sharePre.edit();
        editor.putString("macPro","");
        editor.putString("asePro","");
        editor.commit();

        img_jump = (ImageView) findViewById(R.id.jump_to_edit);
        img_jump.setOnClickListener(this);

        e_r0 = (EditText) findViewById(R.id.edit_r0);
        e_r1 = (EditText) findViewById(R.id.edit_r1);
        e_r2 = (EditText) findViewById(R.id.edit_r2);
        e_r3 = (EditText) findViewById(R.id.edit_r3);
        e_r4 = (EditText) findViewById(R.id.edit_r4);
        e_r5 = (EditText) findViewById(R.id.edit_r5);
        e_account = (EditText) findViewById(R.id.edit_account);
        e_command = (EditText) findViewById(R.id.edit_command);


        btn_init = (Button) findViewById(R.id.btn_init);
        btn_action = (Button) findViewById(R.id.btn_action);
        btn_auto_action = (Button) findViewById(R.id.btn_auto_action);
        btn_init.setOnClickListener(this);
        btn_action.setOnClickListener(this);
        btn_auto_action.setOnClickListener(this);

        simList = initData();
        lAdapter = new ListAdapter(this, simList);
        mList = (ListView) findViewById(R.id.mlist);
        mList.addHeaderView(LayoutInflater.from(this).inflate(R.layout.listhead,null));

//        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
////                parent.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
//                Log.e("result:","LongClick");
//                lAdapter.setAnimationItem(position);
//                lAdapter.setAnimationItem(position+1);
//            }
//        });

//        mList.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                int width = v.getWidth();
//                int heigh = v.getHeight();
//
//                return false;
//            }
//        });

        mList.setAdapter(lAdapter);

        lAdapter.setAnimationItem(0);
        lAdapter.setAnimationItem(1);

        init_flag = true;
        action_flag = true;





    }


    /**
     * 按键响应事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.jump_to_edit:
                Intent in_jump = new Intent();
                String macText = sharePre.getString("macPro","");
                String aseText = sharePre.getString("asePro","");

                in_jump.putExtra("macPro",macText);
                in_jump.putExtra("asePro",aseText);
                in_jump.setClass(MainActivity.this, CodeActivity.class);
                startActivityForResult(in_jump, 1);
                break;

            case R.id.btn_init:
                resetList();
                resetRegister();
                mList.setSelection(0);
                FileUtils.setInitFlag(false);
                break;

            case R.id.btn_action:
                StartSim();
                break;

            case R.id.btn_auto_action:
                Thread auto = new Thread(new AutoStartSim());
                auto.start();
                break;
        }
    }

    /**
     * activity回调函数
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        message = handler.obtainMessage();
        switch (resultCode) {

            case RESULT_OK:
                Bundle bundle = data.getExtras();
                List<String> arr = (List<String>) bundle.get("mac_arr");

                reflshList(arr);
                resetRegister();
                initCommandVal("00");
                e_command.setText(SimulateObject.getCommandVal().toUpperCase());
//                message = handler.obtainMessage();
//                message.what = 6;
//                handler.sendMessage(message);

                break;
        }
    }

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg){

            Register r;
            String text;
            switch (msg.what) {
                case 0:
                    Log.d("reslut:","CASE0");
                    r = (Register) msg.obj;
                    e_r0.setText(r.getR0_Value());
                    break;
                case 1:
                    Log.d("reslut:","CASE1");
                    r = (Register) msg.obj;
                    e_r1.setText(r.getR1_Value());
                    break;
                case 2:
                    Log.d("reslut:","CASE2");
                    r = (Register) msg.obj;
                    e_r2.setText(r.getR2_Value());
                    break;
                case 3:
                    e_account.setBackground(getResources().getDrawable(R.drawable.textselectshape));
                    break;
                case 4:
                    lAdapter.animationCheck(-1);

                    //e_account.setBackground(getResources().getDrawable(R.drawable.textshape));
                    e_command.setBackground(getResources().getDrawable(R.drawable.textselectshape));
                    break;
                case 5:
                    e_command.setBackground(getResources().getDrawable(R.drawable.textshape));
                    break;
                case 6:

                    e_account.setText(SimulateObject.getAccountVal().toUpperCase());
                    e_command.setText(SimulateObject.getCommandVal().toUpperCase());
                    e_r0.setText(SimulateObject.getR0Val().toUpperCase());
                    e_r1.setText(SimulateObject.getR1Val().toUpperCase());
                    e_r2.setText(SimulateObject.getR2Val().toUpperCase());
                    e_r3.setText(SimulateObject.getR3Val().toUpperCase());
                    e_r4.setText(SimulateObject.getR4Val().toUpperCase());
                    e_r5.setText(SimulateObject.getR5Val().toUpperCase());

//                    String account = SimulateObject.getAccountVal();
//                    int index = transformAcoountToIndex(account);
//                    lAdapter.setAnimationItem(index);
//                    lAdapter.setAnimationItem(index+1);


                    break;
                case 7:

                    int in = transformAcoountToIndex(accountNum);
                    e_account.setBackground(getResources().getDrawable(R.drawable.textshape));
                    lAdapter.setAnimationItem(-1);
                    lAdapter.animationCheck(in);
                    lAdapter.animationCheck(in+1);
                    Log.d("result",""+in);

                    break;

                case 8:
                    int index = transformAcoountToIndex(SimulateObject.getAccountVal());
                    lAdapter.setAnimationItem(index);
                    lAdapter.setAnimationItem(index+1);
                    mList.setSelection(index/2);
                    break;

                case 9:
                    Toast.makeText(MainActivity.this, "寄存器溢出已自动截断", Toast.LENGTH_LONG).show();
                    break;

                case 10:
                    MachineTools.showMessageDialog(MainActivity.this,"一段完整的程序执行完成");
                    break;
                case 11:
                    MachineTools.showMessageDialog(MainActivity.this,"非法指令");
                    break;
                case 12:
                    //btn_init.setClickable(false);
                    btn_init.setEnabled(false);
                    btn_action.setEnabled(false);
                    btn_auto_action.setEnabled(false);
                    break;
                case 13:
                    btn_init.setEnabled(true);
                    btn_action.setEnabled(true);
                    btn_auto_action.setEnabled(true);
                    break;
                case 14:
                    MachineTools.showMessageDialog(MainActivity.this,"机器指令存在错误");
                    break;

            }
        }
    };



    /**
     * 获得初始化数据
     *
     * @return
     */
    public List<SimulateData> initData() {

        SimulateData sd;
        List<SimulateData> sdList = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            for (int j = 0; j <= 15; j++) {

                sd = new SimulateData();
                String addr = tranformData(i).toUpperCase() + tranformData(j).toUpperCase();
                sd.setAddr(addr);
                sd.setNumber("00");
                sd.setFlag(0);
                sdList.add(sd);
            }
        }

        return sdList;
    }

    /**
     * 转换数据
     *
     * @param num
     * @return
     */
    private String tranformData(int num) {

        return Integer.toHexString(num);
    }

    /**
     * 刷新物理地址中的值
     *
     * @param arr
     */
    public void reflshList(List<String> arr) {

        for(int i = 0; i < arr.size(); i++){

            simList.get(i).setNumber(arr.get(i).toUpperCase());

        }
        lAdapter.notifyDataSetChanged();
    }

    /**
     * 重置物理地址中的值
     */
    public void resetList() {
        simList = initData();
        lAdapter.refresh(simList);
    }

    /**
     * 重置寄存器的值
     */
    public void resetRegister(){

        SimulateObject.setR0Val("00");
        SimulateObject.setR1Val("00");
        SimulateObject.setR2Val("00");
        SimulateObject.setR3Val("00");
        SimulateObject.setR4Val("00");
        SimulateObject.setR5Val("00");
        SimulateObject.setAccountVal("00");
        SimulateObject.setCommandVal("0000");

        e_r0.setText(SimulateObject.getR0Val());
        e_r1.setText(SimulateObject.getR1Val());
        e_r2.setText(SimulateObject.getR2Val());
        e_r3.setText(SimulateObject.getR3Val());
        e_r4.setText(SimulateObject.getR4Val());
        e_r5.setText(SimulateObject.getR5Val());

        e_account.setText(SimulateObject.getAccountVal());
        e_command.setText(SimulateObject.getCommandVal());

        lAdapter.setAnimationItem(0);
        lAdapter.setAnimationItem(1);


    }

    /**
     * 获取当前指令
     *
     * @return 当前指令
     */
    public String getCurrCommand(int index) {

        String firstAddr = SimulateObject.getAccountVal();
      //  MachineTools.showMessageDialog(this,"firstAddr:"+firstAddr);
        SimulateData object = (SimulateData)lAdapter.getItem(index);
        String addr = object.getAddr();
       // MachineTools.showMessageDialog(this,"addr:"+addr);
        String firstCommand =object.getNumber();
       // MachineTools.showMessageDialog(this,"firstCommand:"+firstCommand);

        String res = firstCommand;

        return res;
    }

    public void setCurrCommand(int index, String value){

        ((SimulateData)lAdapter.getItem(index)).setNumber(value);
        lAdapter.notifyDataSetChanged();

    }

    public int transformAcoountToIndex(String addr){

        int index = Integer.parseInt(addr,16);
        return index;
    }



    Runnable animationRun = new Runnable() {
        @Override
        public void run() {
            try{

                message = handler.obtainMessage();
                message.what = 12;
                handler.sendMessage(message);

                //计数器动画
                message = handler.obtainMessage();
                message.what = 3;
                handler.sendMessage(message);
                Thread.sleep(500);


                message = handler.obtainMessage();
                message.what = 7;
                handler.sendMessage(message);
                Thread.sleep(500);

                //指令寄存器动画
                message = handler.obtainMessage();
                message.what = 4;
                handler.sendMessage(message);
                Thread.sleep(500);

                message = handler.obtainMessage();
                message.what = 5;
                handler.sendMessage(message);

                //刷新寄存其中的值
                message = handler.obtainMessage();
                message.what = 6;
                handler.sendMessage(message);

                message = handler.obtainMessage();
                message.what = 8;
                handler.sendMessage(message);

                message = handler.obtainMessage();
                message.what = 13;
                handler.sendMessage(message);

            }catch (Exception e){

            }

        }
    };



    /**
     * 单步模拟机器代码执行
     */
    public void StartSim(){

        new Thread(animationRun).start();

        accountNum = SimulateObject.getAccountVal();
        int index = transformAcoountToIndex(accountNum);
        String firstCom = getCurrCommand(index);
        String lastCom = getCurrCommand(index+1);
        String Com = firstCom+lastCom;
        String code = firstCom.substring(0,1);
        String reg = "";
        String reg1 = "";
        String reg2 = "";

        switch (code){

            case "1":
                if(Com.matches("^(1[0-5][0-9a-fA-F]{2})$")){
                    reg = firstCom.substring(1,2);
                    index = transformAcoountToIndex(lastCom);
                    lastCom = getCurrCommand(index);
                    CountRegister.loadCommand(reg,lastCom);
                    updateAccountNum(accountNum);
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }

                break;
            case "2":
                if(Com.matches("^(2[0-5][0-9a-fA-F]{2})$")) {
                    reg = firstCom.substring(1, 2);
                    CountRegister.loadCommand(reg, lastCom);
                    updateAccountNum(accountNum);
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }

                break;
            case "3":
                if(Com.matches("^(3[0-5][0-9a-fA-F]{2})$")) {
                    reg = firstCom.substring(1, 2);
                    String value = CountRegister.storeCommand(reg);
                    index = transformAcoountToIndex(accountNum);
                    setCurrCommand(index, value);
                    updateAccountNum(accountNum);
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }

                break;
            case "4":
                if(Com.matches("^(40[0-5]{2})$")) {
                    reg1 = lastCom.substring(0, 1);
                    reg2 = lastCom.substring(1, 2);
                    CountRegister.moveCommand(reg1, reg2);
                    updateAccountNum(accountNum);
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }


                break;
            case "5":
                if(Com.matches("^(5[0-5]{3})$")) {
                    reg = firstCom.substring(1, 2);
                    reg1 = lastCom.substring(0, 1);
                    reg2 = lastCom.substring(1, 2);
                    int flag = CountRegister.addCommand(reg, reg1, reg2);
                    if (flag == -1) {
                        Toast.makeText(this, "寄存器溢出已自动截断", Toast.LENGTH_LONG).show();
                    }
                    updateAccountNum(accountNum);
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }

                break;
            case "6":
                if(Com.matches("^(6[0-5]0[0-9a-fA-F])$")) {
                    reg = firstCom.substring(1, 2);
                    int shlVal = Integer.parseInt(lastCom);
                    CountRegister.shlCommand(reg, shlVal);
                    updateAccountNum(accountNum);
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }

                break;
            case "7":
                if(Com.matches("^(7[0-5]00)$")) {
                    reg = firstCom.substring(1, 2);
                    CountRegister.notCommand(reg);
                    updateAccountNum(accountNum);
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }

                break;
            case "8":
                if(Com.matches("^(8[0-5][0-9a-fA-F]{2})$")) {
                    reg = firstCom.substring(1, 2);
                    int i = CountRegister.jmpCommand(reg, lastCom);
                    if (i == 0) {

                        updateAccountNum(accountNum);

                    } else {
                        Log.d("result:", "跳转");
                        initCommandVal(SimulateObject.getAccountVal());
                    }
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }

                break;
            case "9":
                if(Com.matches("^(9000)$")) {
                    message = handler.obtainMessage();
                    message.what = 10;
                    handler.sendMessage(message);
                    updateAccountNum("-2");
                }else{
                    message = handler.obtainMessage();
                    message.what = 14;
                    handler.sendMessage(message);
                }

                break;
            default:
//                MachineTools.showMessageDialog(this,"非法指令");
                message = handler.obtainMessage();
                message.what = 11;
                handler.sendMessage(message);
                break;
        }


    }

    /**
     * 自动模拟机器代码执行
     */

    class AutoStartSim implements Runnable {
        @Override
        public void run() {



            String code = "0";
            String reg = "";
            String reg1 = "";
            String reg2 = "";
            String Com = "";
            boolean Runflag = true;

            //设置初始化和单步执行不可用
            message = handler.obtainMessage();
            message.what = 12;
            handler.sendMessage(message);

            do {

                try{

                    accountNum = SimulateObject.getAccountVal();
                    int index = transformAcoountToIndex(accountNum);

                    message = handler.obtainMessage();
                    message.what = 3;
                    handler.sendMessage(message);
                    Thread.sleep(500);

                    message = handler.obtainMessage();
                    message.what = 7;
                    handler.sendMessage(message);
                    Thread.sleep(500);

                    message = handler.obtainMessage();
                    message.what = 4;
                    handler.sendMessage(message);
                    Thread.sleep(500);

                    message = handler.obtainMessage();
                    message.what = 5;
                    handler.sendMessage(message);

                    message = handler.obtainMessage();
                    message.what = 6;
                    handler.sendMessage(message);

                    message = handler.obtainMessage();
                    message.what = 8;
                    handler.sendMessage(message);


                    String firstCom = getCurrCommand(index);
                    String lastCom = getCurrCommand(index+1);
                    Com = firstCom+lastCom;
                    code = firstCom.substring(0,1);
                    reg = "";
                    reg1 = "";
                    reg2 = "";


                    switch (code){

                        case "1":
                            if(Com.matches("^(1[0-5][0-9a-fA-F]{2})$")) {
                                reg = firstCom.substring(1, 2);
                                index = transformAcoountToIndex(lastCom);
                                lastCom = getCurrCommand(index);
                                CountRegister.loadCommand(reg, lastCom);
                                updateAccountNum(accountNum);
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }

                            break;
                        case "2":
                            if(Com.matches("^(2[0-5][0-9a-fA-F]{2})$")) {
                                reg = firstCom.substring(1, 2);
                                CountRegister.loadCommand(reg, lastCom);
                                updateAccountNum(accountNum);
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }

                            break;
                        case "3":
                            if(Com.matches("^(3[0-5][0-9a-fA-F]{2})$")) {
                                reg = firstCom.substring(1, 2);
                                String value = CountRegister.storeCommand(reg);
                                index = transformAcoountToIndex(accountNum);
                                setCurrCommand(index, value);
                                updateAccountNum(accountNum);
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }

                            break;
                        case "4":
                            if(Com.matches("^(40[0-5]{2})$")) {
                                reg1 = lastCom.substring(0, 1);
                                reg2 = lastCom.substring(1, 2);
                                CountRegister.moveCommand(reg1, reg2);
                                updateAccountNum(accountNum);
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }

                            break;
                        case "5":
                            if(Com.matches("^(5[0-5]{3})$")) {
                                reg = firstCom.substring(1, 2);
                                reg1 = lastCom.substring(0, 1);
                                reg2 = lastCom.substring(1, 2);
                                int flag = CountRegister.addCommand(reg, reg1, reg2);
                                if (flag == -1) {
                                    message = handler.obtainMessage();
                                    message.what = 9;
                                    handler.sendMessage(message);
                                }
                                updateAccountNum(accountNum);
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }

                            break;
                        case "6":
                            if(Com.matches("^(6[0-5]0[0-9a-fA-F])$")) {
                                reg = firstCom.substring(1, 2);
                                int shlVal = Integer.parseInt(lastCom);
                                CountRegister.shlCommand(reg, shlVal);
                                updateAccountNum(accountNum);
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }

                            break;
                        case "7":
                            if(Com.matches("^(7[0-5]00)$")) {
                                reg = firstCom.substring(1, 2);
                                CountRegister.notCommand(reg);
                                updateAccountNum(accountNum);
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }

                            break;
                        case "8":
                            if(Com.matches("^(8[0-5][0-9a-fA-F]{2})$")) {
                                reg = firstCom.substring(1, 2);
                                int i = CountRegister.jmpCommand(reg, lastCom);
                                if (i == 0) {

                                    updateAccountNum(accountNum);

                                } else {
                                    Log.d("result:", "跳转");
                                    initCommandVal(SimulateObject.getAccountVal());
                                }
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }
                            break;
                        case "9":
                            if(Com.matches("^(9000)$")) {
                                message = handler.obtainMessage();
                                message.what = 10;
                                handler.sendMessage(message);
                                Runflag = false;
                            }else{
                                message = handler.obtainMessage();
                                message.what = 14;
                                handler.sendMessage(message);
                                Runflag = false;
                            }
                            break;
                        default:
                            message = handler.obtainMessage();
                            message.what = 11;
                            handler.sendMessage(message);
                            Runflag = false;
                            break;
                    }

                    Thread.sleep(500);


                }catch (Exception e){

                }



            }while (Runflag);

//            if(code.equals("9")){
//                if(Com.matches("^(9000)$")) {
//                    message = handler.obtainMessage();
//                    message.what = 10;
//                    handler.sendMessage(message);
//                }
//            }else{
//                message = handler.obtainMessage();
//                message.what = 11;
//                handler.sendMessage(message);
//            }

            //设置初始化和单步执行可用
            message = handler.obtainMessage();
            message.what = 13;
            handler.sendMessage(message);

            Thread.interrupted();
        }
    }


    /**
     *  更新计数器数值
     * @param accountNum
     */
    public void updateAccountNum(String accountNum){

        int account = Integer.parseInt(accountNum,16);
        account = account + 2;
        if(account <= 15){
            accountNum = "0"+ Integer.toHexString(account);
        }else{
            accountNum = Integer.toHexString(account);
        }

        String firstCom = getCurrCommand(account);
        String lastCom = getCurrCommand(account+1);
        SimulateObject.setCommandVal((firstCom+lastCom));
        SimulateObject.setAccountVal(accountNum);

    }

    /**
     * 更新指令寄存器数值
     * @param accountNum
     */
    public void initCommandVal(String accountNum){

        if(accountNum != null){

            int index = transformAcoountToIndex(accountNum);
            String firstCom = getCurrCommand(index);
            String lastCom = getCurrCommand(index+1);
            SimulateObject.setCommandVal((firstCom+lastCom));
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

    public class MyBroadcast extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            int position = intent.getIntExtra("position",0);
            if(position <= 15){
                accountNum = "0"+ Integer.toHexString(position);
            }else{
                accountNum = Integer.toHexString(position);
            }

            String firstCom = getCurrCommand(position);
            String lastCom = getCurrCommand(position+1);
            SimulateObject.setCommandVal((firstCom+lastCom));
            SimulateObject.setAccountVal(accountNum);

            e_account.setText(SimulateObject.getAccountVal().toUpperCase());
            e_command.setText(SimulateObject.getCommandVal().toUpperCase());

            lAdapter.setAnimationItem(position);
            lAdapter.setAnimationItem(position+1);
        }
    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(broadcast);
        super.onDestroy();
    }
}
