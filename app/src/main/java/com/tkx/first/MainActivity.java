package com.tkx.first;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tkx.entiys.Register;
import com.tkx.entiys.SimulateObject;

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
        mList.setSelection(0);

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
                e_command.setText("0000");
                e_account.setText("00");
                break;

            case R.id.btn_action:
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

        switch (resultCode) {

            case RESULT_OK:
                Bundle bundle = data.getExtras();
                List<String> arr = (List<String>) bundle.get("mac_arr");
                for (int i = 0; i < arr.size(); i++) {
                    Log.d("reslut:", arr.get(i));
                }

                reflshList(arr);

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
                    Log.d("reslut:","CASE3");
                    r = (Register) msg.obj;
                    e_r3.setText(r.getR3_Value());
                    break;
                case 4:
                    Log.d("reslut:","CASE4");
                    r = (Register) msg.obj;
                    e_r4.setText(r.getR4_Value());
                    break;
                case 5:
                    Log.d("reslut:","CASE5");
                    r = (Register) msg.obj;
                    e_r5.setText(r.getR5_Value());
                    break;
                case 6:
                    Log.d("reslut:","CASE6");
                    text = (String) msg.obj;

                    e_account.setText(SimulateObject.getAccountVal());
                    e_command.setText(SimulateObject.getCommandVal());
                    e_account.setBackground(getResources().getDrawable(R.drawable.textshape));

                    break;
                case 7:
                    Log.d("reslut:","CASE7");
                    text = (String) msg.obj;
                    e_command.setText(text);
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

        String res = "";
        switch (num) {

            case 10:
                res = "a";
                break;
            case 11:
                res = "b";
                break;
            case 12:
                res = "c";
                break;
            case 13:
                res = "d";
                break;
            case 14:
                res = "e";
                break;
            case 15:
                res = "f";
                break;
            default:
                res = "" + num;
                break;

        }

        return res;
    }

    /**
     * 刷新物理地址中的值
     *
     * @param arr
     */
    public void reflshList(List<String> arr) {

        for (int i = 0; i < arr.size(); i++) {
            simList.get(i).setNumber(arr.get(i));
        }
        lAdapter.notifyDataSetChanged();
    }

    /**
     * 重置物理地址中的值
     */
    public void resetList() {
        for (int i = 0; i < simList.size(); i++) {
            simList.get(i).setNumber("00");
        }
        lAdapter.notifyDataSetChanged();
    }

    /**
     * 获取选中地址值
     *
     * @return 地址值
     */
    public String getNumforAddr(int i) {

        String res = "";

        SimulateData data1 = (SimulateData) mList.getItemAtPosition(i);
        SimulateData data2 = (SimulateData) mList.getItemAtPosition(i + 1);

        res = data1.getNumber() + data2.getNumber();
        return res;
    }

    public String getCurrentAddr(int i) {
        String res = "";
        SimulateData data1 = (SimulateData) mList.getItemAtPosition(i);
        res = data1.getAddr();
        return res;
    }

    /**
     * 自动模拟机器代码执行
     */

    class AutoStartSim implements Runnable {
        @Override
        public void run() {
            Message message;
            try {
                int i = 0;
                Log.d("reslut:", "in startAutoSimulate");
                Log.d("reslut:", "" + i);
                while (true) {
                    message = Message.obtain();
                    Log.d("reslut:", "in while");
                    String command = getNumforAddr(i);
                    String count = getCurrentAddr(i);
                    Log.d("reslut:", command);
                    SimulateObject.setAccountVal(count);
                    SimulateObject.setCommandVal(command);
                    message.what = 6;
                    MainActivity.this.handler.sendMessage(message);

//                    Thread.sleep(500);
//
//                    message.what = 7;
//                    message.obj = command;
//                    MainActivity.this.handler.sendMessage(message);

                    Log.d("reslut:", "onConnection");
                    i = i + 2;
                    Log.d("reslut:", "" + i);
                    Thread.sleep(2000);
                    mList.setSelection(i);

                    Log.d("reslut::", "setSelection");

                    if (i >= 30) {
                        break;
                    }
                }

            } catch (Exception e) {

            }

        }
    }

}
