package com.tkx.entiys;

/**
 * Created by tkx.
 * e-mail is 993296096@qq.com
 * time is 2017/2/24
 */

public class MachineError {

    private String errorInfo;
    private int errorRow;

    public MachineError(){}

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public int getErrorRow() {
        return errorRow;
    }

    public void setErrorRow(int errorRow) {
        this.errorRow = errorRow;
    }
}
