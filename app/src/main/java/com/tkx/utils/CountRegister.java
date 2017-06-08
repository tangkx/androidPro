package com.tkx.utils;

import com.tkx.entiys.SimulateObject;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/27
 */

public class CountRegister {

    public static void loadCommand(String register, String value){

        SimulateObject.setRegisterVal(register, value);
    }

    public static String storeCommand(String register){
        String value = SimulateObject.getRegisterVal(register);
        return value;
    }


    public static int addCommand(String r1, String r2, String r3){
        String val = "";

        String val2 = SimulateObject.getRegisterVal(r2);
        String val3 = SimulateObject.getRegisterVal(r3);
        int hax1 = Integer.parseInt(val2,16);
        int hax2 = Integer.parseInt(val3,16);

        int hax = hax1 + hax2;

        if(hax > 255){

            SimulateObject.setRegisterVal(r1, "0"+Integer.toHexString(hax-256));
            return -1;
        }else if (hax <= 15){

            SimulateObject.setRegisterVal(r1, "0"+Integer.toHexString(hax));
        }else{
            SimulateObject.setRegisterVal(r1, Integer.toHexString(hax));
        }
        return 0;
    }

    public static void moveCommand(String r1, String r2){

        String val = SimulateObject.getRegisterVal(r1);
        SimulateObject.setRegisterVal(r2, val);
    }

    public static void shlCommand(String r, int x){

        String val = SimulateObject.getRegisterVal(r);
        int hax1 = Integer.parseInt(val,16);
        hax1 = hax1 << x;
        if(hax1 <= 15){
            val = "0"+ Integer.toHexString(hax1);
        }else if(hax1 > 255){
            val = "00";
        }else{
            val = Integer.toHexString(hax1);
        }

        SimulateObject.setRegisterVal(r,val);
    }

    public static void notCommand(String r){

        String val = SimulateObject.getRegisterVal(r);
        int hexval = Integer.parseInt(val,16);
        hexval = ~hexval;
        String data = Integer.toHexString(hexval);
        data = data.substring(data.length()-2,data.length());
        SimulateObject.setRegisterVal(r,data);


    }

    public static int jmpCommand(String reg, String value){
        String regVal = SimulateObject.getRegisterVal(reg);
        String regVal1 = SimulateObject.getR0Val();
        if(regVal.equals(regVal1)){
            SimulateObject.setAccountVal(value);
            return 1;
        }

        return 0;
    }

    public static void xchgCommand(String r, String s){
        String regVal = SimulateObject.getRegisterVal(r);
        String regVal1 = SimulateObject.getRegisterVal(s);
        SimulateObject.setRegisterVal(r,regVal1);
        SimulateObject.setRegisterVal(s,regVal);
    }


}
