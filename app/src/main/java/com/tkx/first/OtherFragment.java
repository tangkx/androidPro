package com.tkx.first;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class OtherFragment extends Fragment implements View.OnClickListener{

    private Button btn_init;
    private Button btn_action;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.otherlayout, container, false);
        btn_init = (Button) view.findViewById(R.id.btn_init);
        btn_action = (Button) view.findViewById(R.id.btn_action);
        btn_init.setOnClickListener(this);
        btn_action.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btn_init:
                Toast.makeText(getContext(),"init successful",Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_action:
                Toast.makeText(getContext(),"action successful",Toast.LENGTH_LONG).show();
                break;
        }
    }
}
