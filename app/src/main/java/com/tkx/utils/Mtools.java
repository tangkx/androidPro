package com.tkx.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class Mtools {

	String REGEX_REGISTER = "^[0-5]$";
	String REGEX_VALID_DATA = "^([0-9a-fA-F]{1,2})$";
	String REGEX_SHL_DATA = "^(0[0-9a-fA-F])$";
	private Context context;

	public Mtools(Context context){
		this.context = context;
	}

	public List<String> MACtoASE(String[] list) {

		List<String> reslut = new ArrayList<String>();
		String command = "";
		String code = "";
		String reg1 = "";
		String reg2 = "";
		String data = "";
		for (int i = 0; i < list.length; i++) {
			command = list[i];
			if (command.length() != 4) {

				MachineTools.showMessageDialog(context,"错误行" + i + ":机器代码一行之能输入一条4位的指令");
				return null;
			} else {
				code = getOneStr(command);
				switch (code) {
				case "1":
					reg1 = getTwoStr(command);
					data = getLastTwoStr(command);
					if(reg1.matches(REGEX_REGISTER) && data.matches(REGEX_VALID_DATA)){
						reslut.add("LOAD R"+reg1+",["+data+"]");
					}else{

						MachineTools.showMessageDialog(context,"错误行" + i + ":寄存器范围0~5");
						return null;
					}
					break;
				case "2":
					reg1 = getTwoStr(command);
					data = getLastTwoStr(command);
					if(reg1.matches(REGEX_REGISTER) && data.matches(REGEX_VALID_DATA)){
						reslut.add("LOAD R"+reg1+","+data);
					}else{

						MachineTools.showMessageDialog(context,"错误行" + i + ":寄存器范围0~5");
						return null;
					}
					break;
				case "3":
					reg1 = getTwoStr(command);
					data = getLastTwoStr(command);
					if(reg1.matches(REGEX_REGISTER) && data.matches(REGEX_VALID_DATA)){
						reslut.add("STORE R"+reg1+",["+data+"]");
					}else{

						MachineTools.showMessageDialog(context,"错误行" + i + ":寄存器范围0~5");
						return null;
					}
					break;
				case "4":
					String flag = getTwoStr(command);
					if(!flag.equals("0")){

						MachineTools.showMessageDialog(context,"错误行" + i + ":指令错误");
						return null;
					}else{
						reg1 = getThreeStr(command);
						reg2 = getFourStr(command);
						if(reg1.matches(REGEX_REGISTER) && reg2.matches(REGEX_REGISTER)){
							reslut.add("MOV R"+reg1+",R"+reg2);
						}else{

							MachineTools.showMessageDialog(context,"错误行" + i + ":寄存器范围0~5");
							return null;
						}
					}
					
					break;
				case "5":
					String reg = getTwoStr(command);
					reg1 = getThreeStr(command);
					reg2 = getFourStr(command);
					if(reg1.matches(REGEX_REGISTER) && reg2.matches(REGEX_REGISTER) && reg.matches(REGEX_REGISTER)){
						reslut.add("ADD R"+reg+",R"+reg1+",R"+reg2);
					}else{

						MachineTools.showMessageDialog(context,"错误行" + i + ":寄存器范围0~5");
						return null;
					}
					break;
				case "6":
					reg1 = getTwoStr(command);
					data = getLastTwoStr(command);
					if(reg1.matches(REGEX_REGISTER)){
						if(data.matches(REGEX_SHL_DATA)){
							reslut.add("SHL R"+reg1+","+data);
						}else{

							MachineTools.showMessageDialog(context,"错误行" + i + ":立即数错误");
							return null;
						}
					}else{

						MachineTools.showMessageDialog(context,"错误行" + i + ":寄存器范围0~5");
						return null;
					}
					break;
				case "7":
					reg1 = getTwoStr(command);
					data = getLastTwoStr(command);
					if(reg1.matches(REGEX_REGISTER)){
						if(data.matches("^00$")){
							reslut.add("NOT R"+reg1);
						}else{

							MachineTools.showMessageDialog(context,"错误行" + i + ":操作数过多");
						}
					}else{

						MachineTools.showMessageDialog(context,"错误行" + i + ":寄存器范围0~5");
						return null;
					}
					break;
				case "8":
					reg1 = getTwoStr(command);
					data = getLastTwoStr(command);
					if(reg1.matches(REGEX_REGISTER)){
						if(data.matches(REGEX_VALID_DATA)){
							reslut.add("JMP R"+reg1+","+data);
						}else{

							MachineTools.showMessageDialog(context,"错误行" + i + ":立即数错误");
							return null;
						}
					}else{

						MachineTools.showMessageDialog(context,"错误行" + i + ":寄存器范围0~5");
						return null;
					}
					break;
				case "9":
					data = command.substring(1, command.length());
					if(data.matches("^000$")){
						reslut.add("HALT");
					}else {

						MachineTools.showMessageDialog(context,"错误行" + i + ":操作数过多");
						return null;
					}
					break;

				default:

					MachineTools.showMessageDialog(context,"错误行" + i + ":无法识别的指令");

					if(1 == 1){
						return null;
					}
					break;
				}
			}
		}
		return reslut;
	}

	public boolean checkRegister(String r) {

		if (r.matches(REGEX_REGISTER)) {
			return true;
		} else {
			return false;
		}
	}

	public boolean checkData(String data) {

		if (data.matches(REGEX_VALID_DATA)) {
			return true;
		} else {
			return false;
		}
	}

	public String getOneStr(String str) {

		return str.substring(0, 1);
	}

	public String getTwoStr(String str) {

		return str.substring(1, 2);
	}

	public String getThreeStr(String str) {

		return str.substring(2, 3);
	}

	public String getFourStr(String str) {

		return str.substring(3, 4);
	}

	public String getLastTwoStr(String str) {

		return str.substring(2, 4);
	}

}
