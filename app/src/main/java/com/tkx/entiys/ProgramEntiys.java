package com.tkx.entiys;

/**
 * Created by tkx on 2017/4/26.
 */

public class ProgramEntiys {

    public static String[] ASE_PROGRAM_1 =
            {
                    "LOAD R0,01",
                    "LOAD R1,FF",
                    "LOAD R2,02",
                    "LABEL1:ADD R3,R1,R0",
                    "JMP R3,LABEL2",
                    "ADD R4,R1,R2",
                    "JMP R4,LABEL2",
                    "SHL R0,01",
                    "JMP R2,LABEL2",
                    "SHL R0,08",
                    "NOT R0",
                    "JMP R1,LABEL1",
                    "LABEL2:HALT"};

    public static String[] MAC_PROGRAM_1 =
            {
                    "2007",
                    "2101",
                    "2202",
                    "2300",
                    "5221",
                    "5331",
                    "5232",
                    "8212",
                    "8008",
                    "9000"
            };
}
