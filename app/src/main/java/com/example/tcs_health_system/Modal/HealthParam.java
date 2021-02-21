package com.example.tcs_health_system.Modal;

public class HealthParam {
    public String HParaName;
    public  int value;

    public HealthParam(String HParaName, int value){
        this.HParaName=HParaName;
        this.value=value;
    }

    public String getHParaName() {
        return HParaName;
    }

    public void setHParaName(String HParaName) {
        this.HParaName = HParaName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
