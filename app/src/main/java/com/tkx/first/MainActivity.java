package com.tkx.first;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class MainActivity extends Activity implements ListFragment.OnConnectionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

    }

    @Override
    public void onCommandChange(String command) {

    }
}
