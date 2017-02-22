package com.tkx.first;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class ListFragment extends Fragment{

    private ListView mList;
    private ListAdapter lAdapter;
    private OnConnectionListener onConnection;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        lAdapter = new ListAdapter(getContext(), initData());
        View view = inflater.inflate(R.layout.listlayout, container, false);
        mList = (ListView) view.findViewById(R.id.mlist);
        mList.setAdapter(lAdapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{

            onConnection = (OnConnectionListener) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+"must implement OnConnectionListener");
        }
    }

    private List<SimulateData> initData(){

        SimulateData sd;
        List<SimulateData> sdList = new ArrayList<>();
        for(int i = 0 ; i <= 15; i++){
            for(int j = 0 ; j <= 15; j++){

                sd = new SimulateData();
                String addr = tranformData(i)+tranformData(j);
                //Log.d("addr:", addr);
                sd.setAddr(addr);
                sd.setNumber("00");
                sdList.add(sd);
            }
        }

        return sdList;
    }

    /**
     * 转换数据
     * @param num
     * @return
     */
    private String tranformData(int num){

        String res = "";
        switch (num){

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
                res = ""+num;
                break;

        }

        return res;
    }

    /**
     * activity回调接口
     */
    public interface OnConnectionListener{

        public void onCommandChange(String command);
    }
}
