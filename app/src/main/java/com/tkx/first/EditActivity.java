package com.tkx.first;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.LoginFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.tkx.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

        e_mac = (EditText) findViewById(R.id.edit_mac_code);
        e_asse = (EditText) findViewById(R.id.edit_asse_code);

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


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.open_mac_code:
                List<String> arr = new ArrayList<>();
                arr.add("20");
                arr.add("05");
                arr.add("21");
                arr.add("06");
                arr.add("50");
                arr.add("01");
                Intent intent = new Intent();
                intent.putExtra("mac_arr", (Serializable) arr);
                this.setResult(RESULT_OK, intent);
                this.finish();
                break;
            case R.id.new_mac_code:
                break;
            case R.id.save_mac_code:
                break;
            case R.id.change_mac_code:
                break;
            case R.id.open_asse_code:

                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("text/plain");
                startActivityForResult(fileintent, 1);
                break;
            case R.id.new_asse_code:
                break;
            case R.id.save_asse_code:
                break;
            case R.id.change_asse_code:
                break;
        }

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
