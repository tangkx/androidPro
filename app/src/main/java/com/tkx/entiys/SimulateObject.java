package com.tkx.entiys;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/27
 */

public class SimulateObject {

    public static String R0_VAL = "00";
    public static String R1_VAL = "00";;
    public static String R2_VAL = "00";;
    public static String R3_VAL = "00";;
    public static String R4_VAL = "00";;
    public static String R5_VAL = "00";;
    public static String ACCOUNT_VAL = "00";;
    public static String COMMAND_VAL = "0000";;

    public static String getR0Val() {
        return R0_VAL;
    }

    public static void setR0Val(String r0Val) {
        R0_VAL = r0Val;
    }

    public static String getR1Val() {
        return R1_VAL;
    }

    public static void setR1Val(String r1Val) {
        R1_VAL = r1Val;
    }

    public static String getR2Val() {
        return R2_VAL;
    }

    public static void setR2Val(String r2Val) {
        R2_VAL = r2Val;
    }

    public static String getR3Val() {
        return R3_VAL;
    }

    public static void setR3Val(String r3Val) {
        R3_VAL = r3Val;
    }

    public static String getR4Val() {
        return R4_VAL;
    }

    public static void setR4Val(String r4Val) {
        R4_VAL = r4Val;
    }

    public static String getR5Val() {
        return R5_VAL;
    }

    public static void setR5Val(String r5Val) {
        R5_VAL = r5Val;
    }

    public static String getAccountVal() {
        return ACCOUNT_VAL;
    }

    public static void setAccountVal(String accountVal) {
        ACCOUNT_VAL = accountVal;
    }

    public static String getCommandVal() {
        return COMMAND_VAL;
    }

    public static void setCommandVal(String commandVal) {
        COMMAND_VAL = commandVal;
    }

    public static void setRegisterVal(String reg, String val){
        switch (reg){

            case "0":
                setR0Val(val);
                break;
            case "1":
                setR1Val(val);
                break;
            case "2":
                setR2Val(val);
                break;
            case "3":
                setR3Val(val);
                break;
            case "4":
                setR4Val(val);
                break;
            case "5":
                setR5Val(val);
                break;
        }
    }

    public static String getRegisterVal(String register){
        String val = "";
        switch (register){

            case "0":
                val = SimulateObject.getR0Val();
                break;
            case "1":
                val = SimulateObject.getR1Val();
                break;
            case "2":
                val = SimulateObject.getR2Val();
                break;
            case "3":
                val = SimulateObject.getR3Val();
                break;
            case "4":
                val = SimulateObject.getR4Val();
                break;
            case "5":
                val = SimulateObject.getR5Val();
                break;
        }

        return val;
    }
}
