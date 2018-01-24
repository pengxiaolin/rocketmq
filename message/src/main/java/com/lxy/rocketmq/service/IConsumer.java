package com.lxy.rocketmq.service;

import org.apache.rocketmq.common.message.MessageExt;

public interface IConsumer {
    /**
     * 消费端解析消息
     *
     * @param msg
     */
    void handlerMessage(MessageExt msg);

}
