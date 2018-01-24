package com.lxy.rocketmq;

/**
 * @author pengXiaoLin
 * @date 2018-01-22 14:47
 **/
public class ExampleRequest extends MQEntity {

    private String applyNo;

    public String getApplyNo() {
        return applyNo;
    }

    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }
}
