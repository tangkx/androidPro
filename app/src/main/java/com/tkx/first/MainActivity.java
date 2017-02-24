package com.tkx.first;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class MainActivity extends Activity implements ListFragment.OnConnectionListener,
        View.OnClickListener, OtherFragment.OnCommandListener{

    public ImageView img_jump;
    public FragmentManager fmanage = getFragmentManager();
    public ListFragment lf;
    public OtherFragment of;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void initView(){
        img_jump = (ImageView) findViewById(R.id.jump_to_edit);
        img_jump.setOnClickListener(this);
        lf = (ListFragment) fmanage.findFragmentById(R.id.flist);
        of = (OtherFragment) fmanage.findFragmentById(R.id.fother);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.jump_to_edit:
                Intent in_jump = new Intent();
                in_jump.setClass(MainActivity.this,EditActivity.class);
                startActivityForResult(in_jump,1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){

            case RESULT_OK:
                Bundle bundle = data.getExtras();
                List<String> arr = (List<String>) bundle.get("mac_arr");
                for(int i = 0; i < arr.size(); i++){
                    Log.d("reslut:",arr.get(i));
//                    Toast.makeText(this,arr.get(i),Toast.LENGTH_SHORT).show();
                }

                lf.reflshList(arr);

                break;
        }
    }

    @Override
    public void onInitAddr() {
        lf.resetList();
    }

    @Override
    public void startAutoCommand() {
        lf.startAutoSimulate();
    }

    @Override
    public void onCommandChange(String command) {

        Log.d("tags","onCommandChange");
        of.setCommandText(command);

    }
}
