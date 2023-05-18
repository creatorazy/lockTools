package com.azy.locktools.entity;

public class PasswordInfo {

    private Integer cycleType;
    private Integer keyboardPwdType;
    private String keyboardPwd;
    private String newKeyboardPwd;
    private long startDate;
    private long endDate;

    public Integer getCycleType() {
        return cycleType;
    }

    public void setCycleType(Integer cycleType) {
        this.cycleType = cycleType;
    }

    public Integer getKeyboardPwdType() {
        return keyboardPwdType;
    }

    public void setKeyboardPwdType(Integer keyboardPwdType) {
        this.keyboardPwdType = keyboardPwdType;
    }

    public String getKeyboardPwd() {
        return keyboardPwd;
    }

    public void setKeyboardPwd(String keyboardPwd) {
        this.keyboardPwd = keyboardPwd;
    }

    public String getNewKeyboardPwd() {
        return newKeyboardPwd;
    }

    public void setNewKeyboardPwd(String newKeyboardPwd) {
        this.newKeyboardPwd = newKeyboardPwd;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
