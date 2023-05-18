package com.azy.locktools.entity;

public class LockInfo {
    //锁的id,蓝牙名称,mac地址,连接密钥
    protected Integer id;
    protected String name;
    protected String doorBluetoothName;
    protected String doorBluetoothMac;
    protected String doorBluetoothKey;

    //楼栋id,name
    protected Integer buildId;
    protected String buildName;

    //宿舍id,宿舍号,别名
    protected Integer dormId;
    protected String dormNumber;
    protected String lockAlias;

    public LockInfo() {
    }

    public LockInfo(String name, String doorBluetoothName, String doorBluetoothMac, String doorBluetoothKey) {
        this.name = name;
        this.doorBluetoothName = doorBluetoothName;
        this.doorBluetoothMac = doorBluetoothMac;
        this.doorBluetoothKey = doorBluetoothKey;
    }

    public LockInfo(Integer id, String name, String doorBluetoothName, String doorBluetoothMac, String doorBluetoothKey, Integer buildId, String buildName, Integer dormId, String dormNumber, String lockAlias) {
        this.id = id;
        this.name = name;
        this.doorBluetoothName = doorBluetoothName;
        this.doorBluetoothMac = doorBluetoothMac;
        this.doorBluetoothKey = doorBluetoothKey;
        this.buildId = buildId;
        this.buildName = buildName;
        this.dormId = dormId;
        this.dormNumber = dormNumber;
        this.lockAlias = lockAlias;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoorBluetoothName() {
        return doorBluetoothName;
    }

    public void setDoorBluetoothName(String doorBluetoothName) {
        this.doorBluetoothName = doorBluetoothName;
    }

    public String getDoorBluetoothMac() {
        return doorBluetoothMac;
    }

    public void setDoorBluetoothMac(String doorBluetoothMac) {
        this.doorBluetoothMac = doorBluetoothMac;
    }

    public String getDoorBluetoothKey() {
        return doorBluetoothKey;
    }

    public void setDoorBluetoothKey(String doorBluetoothKey) {
        this.doorBluetoothKey = doorBluetoothKey;
    }

    public Integer getBuildId() {
        return buildId;
    }

    public void setBuildId(Integer buildId) {
        this.buildId = buildId;
    }

    public String getBuildName() {
        return buildName;
    }

    public void setBuildName(String buildName) {
        this.buildName = buildName;
    }

    public Integer getDormId() {
        return dormId;
    }

    public void setDormId(Integer dormId) {
        this.dormId = dormId;
    }

    public String getDormNumber() {
        return dormNumber;
    }

    public void setDormNumber(String dormNumber) {
        this.dormNumber = dormNumber;
    }

    public String getLockAlias() {
        return lockAlias;
    }

    public void setLockAlias(String lockAlias) {
        this.lockAlias = lockAlias;
    }
}