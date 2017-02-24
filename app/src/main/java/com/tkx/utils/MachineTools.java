package com.tkx.utils;

import com.tkx.entiys.MachineError;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/24
 */

public class MachineTools {

    public static MachineError illegalMachine(List<String> arr){
        MachineError me = new MachineError();
        me.setErrorRow(0);
        if(arr.isEmpty()){
            me.setErrorInfo("传入数据为空");
            me.setErrorRow(-1);
            return me;
        }

        for(int i =0 ; i < arr.size(); i++){
            String mac = arr.get(i);
            if(mac.length() != 4){
                me.setErrorRow(i+1);
                me.setErrorInfo("机器指令只能为4位的指令");
                return me;
            }else if(!mac.substring(1,2).matches("[0-5]")){
                me.setErrorRow(i+1);
                me.setErrorInfo("寄存器只能是0~5");
                return me;
            }
        }
        return me;
    }

    public static List<String> splitMachineCode(List<String> arr){
        List<String> spiltArr = new ArrayList<>();
        for(int i = 0; i < arr.size(); i++){

            String command = arr.get(i);
            spiltArr.add(command.substring(0,2));
            spiltArr.add(command.substring(2,command.length()));
        }
        return spiltArr;
    }

//    public static void main(String[] args){
//        List<String> arr = new ArrayList<>();
//        arr.add("2005");
//        arr.add("2205");
//
//        MachineError me = MachineTools.illegalMachine(arr);
//        if(me.getErrorRow() == 0){
//            System.out.println("Machine is legal");
//            List<String> spl_arr = splitMachineCode(arr);
//            for(int i = 0; i < spl_arr.size(); i++){
//                System.out.println(spl_arr.get(i));
//            }
//        }else{
//            System.out.println("第"+me.getErrorRow()+"行存在错误:"+me.getErrorInfo());
//        }
//
//    }
}
