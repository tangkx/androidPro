package com.tkx.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        Map<String, String> Tags = new HashMap<>();

        for (int i = 0; i < str.length; i++) {
            String res = "";
            String code = "";
            String[] arr = {};
            if (str[i].matches("[^@]+:[^@]+")) {
                String tag = str[i].substring(0, str[i].indexOf(":"));
                String addr = Integer.toHexString(i * 2);
                if (addr.matches("^[0-9a-fA-F]$")) {
                    addr = "0" + addr;
                }
                Tags.put(tag, addr);
                //System.out.println(tag + Tags.get(tag));
                str[i] = str[i].substring(str[i].indexOf(":") + 1);
            }

            arr = str[i].split("\\s+|,");

            switch (upperStr(arr[0])) {

                case "LOAD":

                    if (arr.length != 3) {
                        //System.out.println("arr's length :" + arr.length);
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }

                    String register = "";
                    if (!checkRegister(arr[1])) {

                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    } else {
                        register = arr[1].substring(arr[1].length() - 1);
                    }

                    if (arr[2].matches(REGEX_MEM_UNIT)) {

                        code = "1";
                        String num = arr[2].replace("[", "");
                        num = num.replace("]", "");
                        res = code + register + num;

                        reslut.add(res);

                    } else {

                        code = "2";
                        if (isNumeric(arr[2])) {

                            res = code + register + arr[2];
                            reslut.add(res);
                        } else {
                            MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数范围00~ff");
                            return null;
                        }
                    }
                    break;

                case "STORE":

                    code = "3";
                    if (checkRegister(arr[1]) && arr[2].matches(REGEX_MEM_UNIT)) {
                        register = arr[1].substring(arr[1].length() - 1);
                        String s = arr[2].replace("[", "");
                        s = arr[2].replace("]", "");
                        res = code + register + s;
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5或操作数有误");
                    }
                    break;

                case "MOV":

                    code = "4";
                    if (arr.length != 3) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }
                    if (checkRegister(arr[1]) && checkRegister(arr[2])) {
                        String reg1 = arr[1].substring(arr[1].length() - 1);
                        String reg2 = arr[2].substring(arr[2].length() - 1);
                        res = code + "0" + reg1 + reg2;
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "ADD":

                    if (arr.length != 4) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }
                    code = "5";
                    if (checkRegister(arr[1]) && checkRegister(arr[2])
                            && checkRegister(arr[3])) {
                        String reg1 = arr[1].substring(arr[1].length() - 1);
                        String reg2 = arr[2].substring(arr[2].length() - 1);
                        String reg3 = arr[3].substring(arr[3].length() - 1);
                        res = code + reg1 + reg2 + reg3;
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "SHL":

                    code = "6";
                    if (arr.length != 3) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }

                    if (checkRegister(arr[1]) && !overSHL(arr[2])) {
                        String reg1 = arr[1].substring(arr[1].length() - 1);
                        String rnum = arr[2].substring(arr[2].length() - 1);
                        res = code + reg1 + "0" + rnum;
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5,立即数范围0-f");
                        return null;
                    }
                    break;
                case "NOT":

                    code = "7";
                    if (arr.length != 2) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }
                    if (checkRegister(arr[1])) {
                        String reg1 = arr[1].substring(arr[1].length() - 1);
                        res = code + reg1 + "00";
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }

                    break;
                case "JMP":

                    code = "8";
                    if (arr.length != 3) {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":操作数不符合");
                        return null;
                    }

                    if (checkRegister(arr[1])) {
                        String reg1 = arr[1].substring(arr[1].length() - 1);
                        String tag = arr[2];

                        res = code + reg1 + tag;
                        reslut.add(res);
                    } else {
                        MachineTools.showMessageDialog(context, "错误行" + (i+1) + ":寄存器范围0~5");
                        return null;
                    }
                    break;
                case "HALT":

                    if (arr.length != 1) {
                        MachineTools.showMessageDialog(context, "错误行" + i + ":操作数不符合");
                        return null;
                    }
                    code = "9";
                    res = code + "000";
                    reslut.add(res);
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
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            for (int i = 0; i < reslut.size(); i++) {

                String command = reslut.get(i);

                command = command.replace(key, value);
                reslut.set(i, command);
                //System.out.println("success");
            }
        }

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
