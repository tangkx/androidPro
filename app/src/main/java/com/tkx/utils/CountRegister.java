package com.tkx.utils;

import com.tkx.entiys.SimulateObject;
import com.tkx.first.SimulateData;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/27
 */

public class CountRegister {

    public static void loadCommand(String register, String value){
        switch (register){

            case "0":
                SimulateObject.setR0Val(value);
                break;
            case "1":
                SimulateObject.setR1Val(value);
                break;
            case "2":
                SimulateObject.setR2Val(value);
                break;
            case "3":
                SimulateObject.setR3Val(value);
                break;
            case "4":
                SimulateObject.setR4Val(value);
                break;
            case "5":
                SimulateObject.setR5Val(value);
                break;
        }
    }

    public static int addCommand(String r1, String r2, String r3){
        String val = "";

        String val2 = SimulateObject.getRegisterVal(r2);
        String val3 = SimulateObject.getRegisterVal(r3);
        int hax1 = Integer.parseInt(val2,16);
        int hax2 = Integer.parseInt(val3,16);

        int hax = hax1 + hax2;

        if(hax > 0xff){
            SimulateObject.setRegisterVal(r1, "ff");
            return -1;
        }else{
            SimulateObject.setRegisterVal(r1, Integer.toHexString(hax));
            return 0;
        }
    }

}
