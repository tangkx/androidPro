package com.tkx.first;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class OtherFragment extends Fragment implements View.OnClickListener{

    private EditText e_r0, e_r1, e_r2, e_r3, e_r4, e_r5, e_account, e_command;
    private Button btn_init, btn_action, btn_auto_action;
    private OnCommandListener clistener;
    public String s_command, s_count;
    public Activity activity;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.otherlayout, container, false);
        initView(view);

        return view;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{

            clistener = (OnCommandListener) activity;
            this.activity = activity;

        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement OnConnectionListener");
        }
    }

    public void initView(View view){

        e_r0 = (EditText) view.findViewById(R.id.edit_r0);
        e_r1 = (EditText) view.findViewById(R.id.edit_r1);
        e_r2 = (EditText) view.findViewById(R.id.edit_r2);
        e_r3 = (EditText) view.findViewById(R.id.edit_r3);
        e_r4 = (EditText) view.findViewById(R.id.edit_r4);
        e_r5 = (EditText) view.findViewById(R.id.edit_r5);
        e_account = (EditText) view.findViewById(R.id.edit_account);
        e_command = (EditText) view.findViewById(R.id.edit_command);

        btn_init = (Button) view.findViewById(R.id.btn_init);
        btn_action = (Button) view.findViewById(R.id.btn_action);
        btn_auto_action = (Button) view.findViewById(R.id.btn_auto_action);
        btn_init.setOnClickListener(this);
        btn_action.setOnClickListener(this);
        btn_auto_action.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_init:
                clistener.onInitAddr();
                e_command.setText("0000");
                e_account.setText("00");
                Toast.makeText(getContext(),"init successful",Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_action:
                Toast.makeText(getContext(),"action successful",Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_auto_action:
                clistener.startAutoCommand();
                Toast.makeText(getContext(),"auto action successful",Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * activity回调接口
     */
    public interface OnCommandListener{

        public void onInitAddr();
        public void startAutoCommand();
    }

    public void setCommandText(String command){
        s_command = command;
        Log.d("reslut:","CommandRigster"+command);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{

                    Log.d("reslut:","CommandRigsterRunnable");
                    e_command.setBackgroundColor(activity.getResources().getColor(R.color.colorselect));
                    Thread.sleep(100);
                    e_command.setText(s_command);
                    Thread.sleep(500);
                    e_command.setBackground(activity.getResources().getDrawable(R.drawable.textshape));
                }catch (Exception e){


                }

            }
        });

    }

    public void setCountText(String count){
        s_count = count;
        Log.d("reslut:","setCountText"+s_count);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{

                    Log.d("reslut:","CommandRigsterRunnable");
                    e_account.setBackgroundColor(activity.getResources().getColor(R.color.colorselect));
                    Thread.sleep(100);
                    e_account.setText(s_count);
                    Thread.sleep(500);
                    e_account.setBackground(activity.getResources().getDrawable(R.drawable.textshape));
                }catch (Exception e){

                }

            }
        });
    }
}
