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
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/21
 */

public class ListFragment extends Fragment {

    private ListView mList;
    private ListAdapter lAdapter;
    private OnConnectionListener onConnection;
    private List<SimulateData> simList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.listlayout, container, false);
        initView(view);
        return view;
    }

    public void initView(View view) {
        simList = initData();
        lAdapter = new ListAdapter(getContext(), simList);
        mList = (ListView) view.findViewById(R.id.mlist);
        mList.setAdapter(lAdapter);
        mList.setSelection(0);
        //       mList.setSelection(0);
//        SimulateData data = (SimulateData) mList.getSelectedItem();
//        Log.d("simuResult :",data.getAddr()+data.getNumber());
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int i = 0;
//                while(true){
//
//                }
//
//            }
//        }).start();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            onConnection = (OnConnectionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnConnectionListener");
        }
    }

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

    /**
     * 自动模拟机器代码执行
     */
    public void startAutoSimulate() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    int i = 0;
                    Log.d("reslut:", "in startAutoSimulate");
                    Log.d("reslut:", ""+i);
                    while(i < 30){
                        Log.d("reslut:","in while");
                        String command = getNumforAddr(i);
                        Log.d("reslut:", command);
                        onConnection.onCommandChange(command);
                        Log.d("reslut:", "onConnection");
                        i = i + 2;
                        Log.d("reslut:", ""+i);
                        Thread.sleep(3000);
                        mList.setSelection(i);
                        Log.d("reslut::", "setSelection");
                    }

                }catch (Exception e){

                }

            }
        }).start();

    }

    /**
     * activity回调接口
     */
    public interface OnConnectionListener {

        public void onCommandChange(String command);
    }

}
