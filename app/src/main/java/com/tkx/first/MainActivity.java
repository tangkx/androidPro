package com.tkx.first;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.tkx.entiys.Register;
import com.tkx.entiys.SimulateData;
import com.tkx.entiys.SimulateObject;
import com.tkx.utils.CountRegister;
import com.tkx.utils.MachineTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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

        myPermission();

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
                in_jump.setClass(MainActivity.this, EditActivity.class);
                startActivityForResult(in_jump, 1);
                break;

            case R.id.btn_init:
                resetList();
                resetRegister();

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
                for (int i = 0; i < arr.size(); i++) {
                    Log.d("reslut:", arr.get(i));
                }

                reflshList(arr);
                resetRegister();
                initCommandVal("00");
                message = handler.obtainMessage();
                message.what = 6;
                handler.sendMessage(message);

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
                    e_account.setBackground(getResources().getDrawable(R.drawable.textshape));
                    e_command.setBackground(getResources().getDrawable(R.drawable.textselectshape));
                    break;
                case 5:
                    e_command.setBackground(getResources().getDrawable(R.drawable.textshape));
                    break;
                case 6:

                    e_account.setText(SimulateObject.getAccountVal());
                    e_command.setText(SimulateObject.getCommandVal());
                    e_r0.setText(SimulateObject.getR0Val());
                    e_r1.setText(SimulateObject.getR1Val());
                    e_r2.setText(SimulateObject.getR2Val());
                    e_r3.setText(SimulateObject.getR3Val());
                    e_r4.setText(SimulateObject.getR4Val());
                    e_r5.setText(SimulateObject.getR5Val());

                    String account = SimulateObject.getAccountVal();
                    int index = transformAcoountToIndex(account);
                    lAdapter.setAnimationItem(index);
                    lAdapter.setAnimationItem(index+1);


                    break;
                case 7:
                    e_account.setBackground(getResources().getDrawable(R.drawable.textshape));
//                    index = transformAcoountToIndex(accountNum);
//                    lAdapter.animationCheck(index);
//                    lAdapter.animationCheck(index+1);

                    break;

                case 8:
                    index = transformAcoountToIndex(SimulateObject.getAccountVal());
                    lAdapter.setAnimationItem(index);
                    lAdapter.setAnimationItem(index+1);
                    break;

                case 9:
                    Toast.makeText(MainActivity.this, "寄存器溢出已自动截断", Toast.LENGTH_LONG).show();
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
                String addr = tranformData(i) + tranformData(j);
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

            simList.get(i).setNumber(arr.get(i));

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

            }catch (Exception e){

            }

        }
    };

    /**
     * 单步模拟机器代码执行
     */
    public void StartSim(){

//        simAnimation();
//        timer.schedule(task,1000);
        new Thread(animationRun).start();

        accountNum = SimulateObject.getAccountVal();
        int index = transformAcoountToIndex(accountNum);
        String firstCom = getCurrCommand(index);
        String lastCom = getCurrCommand(index+1);
        String code = firstCom.substring(0,1);
        String reg = "";
        String reg1 = "";
        String reg2 = "";

        switch (code){

            case "1":
                reg = firstCom.substring(1,2);
                index = transformAcoountToIndex(lastCom);
                lastCom = getCurrCommand(index);
                CountRegister.loadCommand(reg,lastCom);
                updateAccountNum(accountNum);

                break;
            case "2":
                reg = firstCom.substring(1,2);
                CountRegister.loadCommand(reg,lastCom);
                updateAccountNum(accountNum);

                break;
            case "3":
                reg = firstCom.substring(1,2);
                String value = CountRegister.storeCommand(reg);
                index = transformAcoountToIndex(accountNum);
                setCurrCommand(index, value);
                updateAccountNum(accountNum);

                break;
            case "4":
                reg1 = lastCom.substring(0,1);
                reg2 = lastCom.substring(1,2);
                CountRegister.moveCommand(reg1,reg2);
                updateAccountNum(accountNum);

                break;
            case "5":
                reg = firstCom.substring(1,2);
                reg1 = lastCom.substring(0,1);
                reg2 = lastCom.substring(1,2);
                int flag = CountRegister.addCommand(reg, reg1, reg2);
                if(flag == -1){
                    Toast.makeText(this, "寄存器溢出已自动截断", Toast.LENGTH_LONG).show();
                }
                updateAccountNum(accountNum);

                break;
            case "6":
                reg = firstCom.substring(1,2);
                int shlVal = Integer.parseInt(lastCom);
                CountRegister.shlCommand(reg, shlVal);
                updateAccountNum(accountNum);

                break;
            case "7":
                reg = firstCom.substring(1,2);
                CountRegister.notCommand(reg);
                updateAccountNum(accountNum);

                break;
            case "8":
                reg = firstCom.substring(1,2);
                CountRegister.jmpCommand(reg, lastCom);
                updateAccountNum(accountNum);

                break;
            case "9":
                MachineTools.showMessageDialog(this,"一段完整的程序运行完成");
                updateAccountNum("00");

                break;
            default:
                MachineTools.showMessageDialog(this,"非法指令");
                break;
        }




    }

    /**
     *  更新计数器数值
     * @param accountNum
     */
    public void updateAccountNum(String accountNum){

        int account = Integer.parseInt(accountNum);
        account = account + 2;
        if(account <= 15){
            accountNum = "0"+ Integer.toHexString(account);
        }else{
            accountNum = Integer.toHexString(account);
        }

        String firstCom = getCurrCommand(account);
        String lastCom = getCurrCommand(account+1);
        SimulateObject.setCommandVal(firstCom+lastCom);
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
            SimulateObject.setCommandVal(firstCom+lastCom);
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

            do {

                try{

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


                    accountNum = SimulateObject.getAccountVal();
                    int index = transformAcoountToIndex(accountNum);
                    String firstCom = getCurrCommand(index);
                    String lastCom = getCurrCommand(index+1);
                    code = firstCom.substring(0,1);
                    reg = "";
                    reg1 = "";
                    reg2 = "";


                    switch (code){

                        case "1":
                            reg = firstCom.substring(1,2);
                            index = transformAcoountToIndex(lastCom);
                            lastCom = getCurrCommand(index);
                            CountRegister.loadCommand(reg,lastCom);
                            updateAccountNum(accountNum);

                            break;
                        case "2":
                            reg = firstCom.substring(1,2);
                            CountRegister.loadCommand(reg,lastCom);
                            updateAccountNum(accountNum);

                            break;
                        case "3":
                            reg = firstCom.substring(1,2);
                            String value = CountRegister.storeCommand(reg);
                            index = transformAcoountToIndex(accountNum);
                            setCurrCommand(index, value);
                            updateAccountNum(accountNum);

                            break;
                        case "4":
                            reg1 = lastCom.substring(0,1);
                            reg2 = lastCom.substring(1,2);
                            CountRegister.moveCommand(reg1,reg2);
                            updateAccountNum(accountNum);

                            break;
                        case "5":
                            reg = firstCom.substring(1,2);
                            reg1 = lastCom.substring(0,1);
                            reg2 = lastCom.substring(1,2);
                            int flag = CountRegister.addCommand(reg, reg1, reg2);
                            if(flag == -1){
                                message = handler.obtainMessage();
                                message.what = 9;
                                handler.sendMessage(message);
                            }
                            updateAccountNum(accountNum);

                            break;
                        case "6":
                            reg = firstCom.substring(1,2);
                            int shlVal = Integer.parseInt(lastCom);
                            CountRegister.shlCommand(reg, shlVal);
                            updateAccountNum(accountNum);

                            break;
                        case "7":
                            reg = firstCom.substring(1,2);
                            CountRegister.notCommand(reg);
                            updateAccountNum(accountNum);

                            break;
                        case "8":
                            reg = firstCom.substring(1,2);
                            CountRegister.jmpCommand(reg, lastCom);
                            updateAccountNum(accountNum);

                            break;
                    }

                    Thread.sleep(1000);


                }catch (Exception e){

                }



            }while (code.matches("^[1-8]$"));

            Thread.interrupted();
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
