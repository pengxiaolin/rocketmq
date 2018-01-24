package com.lxy.rocketmq.service;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pengXiaoLin
 * @date 2018-01-23 17:32
 **/
public class ExampleSendCallback implements SendCallback {

    private static final Logger logger = LoggerFactory.getLogger(ExampleSendCallback.class);

    @Override
    public void onSuccess(SendResult sendResult) {
        logger.info("生产者：回调函数成功" + sendResult.toString());
    }

    @Override
    public void onException(Throwable throwable) {
        logger.info("生产者：回调函数失败:" + throwable.toString());
    }
}
