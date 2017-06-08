package com.tkx.utils;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


//汇编转换成机器码
public class Htools {

    String REGEX_VALID_DATA = "^([0-9a-fA-F]{1,2})$";
    String REGEX_MEM_UNIT = "^\\[([0-9a-fA-F]{1,2})\\]$";
    String REGEX_REGISTER = "[rR][0-5]$";
    String REGEX_SHLNUM = "^(0[0-9a-fA-F]|[0-9a-fA-F])$";
    private Context context;

    public Htools(Context context) {
        this.context = context;
    }

    public List<String> ASEtoMAC(String[] str) {

        List<String> reslut = new ArrayList<String>();
        List<Integer> JMP_FLAG = new ArrayList<>();
        Map<String, String> Tags = new HashMap<>();

        for (int i = 0; i < str.length; i++) {
            String res = "";
            String code = "";
            String[] comarr = {};
            String[] arr = {};

            if (str[i].matches("[^@]+:[^@]+")) {
                String tag = str[i].substring(0, str[i].indexOf(":")).toUpperCase();
                if(Tags.containsKey(tag.trim())){
                    MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":存在重复的标志位");
                    return null;
                }

                String addr = Integer.toHexString(i * 2).toUpperCase();
                if (addr.matches("^[0-9a-fA-F]$")) {
                    addr = "0" + addr;
                }
                Tags.put(tag.trim(), addr);

                str[i] = str[i].substring(str[i].indexOf(":") + 1);
            }

            if(str[i].toString().isEmpty()){
                reslut.add("");
                continue;
            }

            comarr = str[i].trim().split("\\s+");
            if(comarr.length != 2 && !upperStr(comarr[0]).equals("HALT")){
                MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                return null;
            }

            if(comarr.length != 1 && upperStr(comarr[0]).equals("HALT")){
                MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                return null;
            }

            //arr = str[i].split("[\\s+][\\s*,\\s*]*");

            switch (upperStr(comarr[0].toString().trim())) {

                case "LOAD":

                    arr = comarr[1].trim().split("\\s*,\\s*");
                    if (arr.length != 2) {
                        //System.out.println("arr's length :" + arr.length);
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }

                    String register = "";
                    if (!checkRegister(arr[0])) {

                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    } else {
                        register = arr[0].substring(arr[0].length() - 1);
                    }

                    if (arr[1].matches(REGEX_MEM_UNIT)) {

                        code = "1";
                        String num = arr[1].replace("[", "");
                        num = num.replace("]", "");
                        res = code + register + num;

                        reslut.add(res);

                    } else {

                        code = "2";
                        if (isNumeric(arr[1])) {

                            res = code + register + arr[1];
                            reslut.add(res);
                        } else {
                            MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数范围00~ff");
                            return null;
                        }
                    }
                    break;

                case "STORE":

                    arr = comarr[1].trim().split("\\s*,\\s*");
                    code = "3";
                    if (checkRegister(arr[0])) {
                        if(arr[1].matches(REGEX_MEM_UNIT)){
                            register = arr[0].substring(arr[0].length() - 1);
                            String s = "";
                            s = arr[1].replace("[", "");
                            s = s.replace("]", "");
                            res = code + register + s;
                            reslut.add(res);
                        }else{
                            MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":内存地址错误");
                            return null;
                        }

                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;

                case "MOV":

                    arr = comarr[1].trim().split("\\s*,\\s*");
                    code = "4";
                    if (arr.length != 2) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }
                    if (checkRegister(arr[0]) && checkRegister(arr[1])) {
                        String reg1 = arr[0].substring(arr[0].length() - 1);
                        String reg2 = arr[1].substring(arr[1].length() - 1);
                        res = code + "0" + reg1 + reg2;
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "ADD":

                    arr = comarr[1].trim().split("\\s*,\\s*");
                    if (arr.length != 3) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }
                    code = "5";
                    if (checkRegister(arr[0]) && checkRegister(arr[1])
                            && checkRegister(arr[2])) {
                        String reg1 = arr[0].substring(arr[0].length() - 1);
                        String reg2 = arr[1].substring(arr[1].length() - 1);
                        String reg3 = arr[2].substring(arr[2].length() - 1);
                        res = code + reg1 + reg2 + reg3;
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "SHL":

                    arr = comarr[1].trim().split("\\s*,\\s*");
                    code = "6";
                    if (arr.length != 2) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }

                    if (checkRegister(arr[0]) && !overSHL(arr[1])) {
                        String reg1 = arr[0].substring(arr[0].length() - 1);
                        String rnum = arr[1].substring(arr[1].length() - 1);
                        res = code + reg1 + "0" + rnum;
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5,立即数范围0-f");
                        return null;
                    }
                    break;
                case "NOT":

                    arr = comarr[1].trim().split("\\s*,\\s*");
                    code = "7";
                    if (arr.length != 1) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }
                    if (checkRegister(arr[0])) {
                        String reg1 = arr[0].substring(arr[0].length() - 1);
                        res = code + reg1 + "00";
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }

                    break;
                case "JMP":

                    arr = comarr[1].trim().split("\\s*,\\s*");
                    code = "8";
                    if (arr.length != 2) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }

                    if (checkRegister(arr[0])) {
                        String reg1 = arr[0].substring(arr[0].length() - 1);
                        String tag = arr[1].toString().toUpperCase();

                        res = code + reg1 + tag;
                        JMP_FLAG.add(i);
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "HALT":

                    if (arr.length != 0) {
                        MachineTools.showMessageDialog(context, "错误行" + i + ":操作数不符合");
                        return null;
                    }
                    code = "9";
                    res = code + "000";
                    reslut.add(res);
                    break;

                case "XCHG":

                    arr = comarr[1].trim().split("\\s*,\\s*");
                    code = "A";
                    if (arr.length != 2) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }

                    if(checkRegister(arr[0]) && checkRegister(arr[1])){
                        String reg1 = arr[0].substring(arr[0].length() - 1);
                        String reg2 = arr[1].substring(arr[1             ].length() - 1);
                        res = code +"0"+reg1 + reg2;
                        reslut.add(res);
                    }else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                default:
                    MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":未知指令");
                    if (1 == 1) {
                        return null;
                    }
                    break;
            }
        }


        Iterator iterator = Tags.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            String key = (String) entry.getKey().toString().toUpperCase();
            String value = (String) entry.getValue().toString().toUpperCase();

            for (int i = 0; i < reslut.size(); i++) {

                String command = reslut.get(i);

                if(command.matches("^(8[0-5]"+key+")$")){
                    command = command.replace(key, value);
                    reslut.set(i, command);
                    JMP_FLAG.remove((Integer) i);
                }
            }
        }


        if(JMP_FLAG.size() > 0){
            int j = JMP_FLAG.get(0);
            MachineTools.showMessageDialog(context, "错误行" + (j+1) + ":跳转到无用的标志");
            return null;
        }
//


        return reslut;
    }

    public String upperStr(String str) {
        return str.toUpperCase();
    }

    public boolean overflow(String hexnum) {
        boolean res = false;

        int hex = Integer.parseInt(hexnum, 16);
        if (hex < 0x00 || hex > 0xFF) {
            res = true;
        }
        return res;
    }

    public boolean overSHL(String hexnum) {
        boolean res = false;
        if (!hexnum.matches(REGEX_SHLNUM)) {
            res = true;
        }
        return res;
    }

    public boolean checkRegister(String register) {

        boolean res = false;
        if (register.matches(REGEX_REGISTER)) {
            res = true;
        }

        return res;
    }

    public boolean isNumeric(String str) {

        if (str.matches(REGEX_VALID_DATA)) {
            return true;
        }
        return false;
    }
}
