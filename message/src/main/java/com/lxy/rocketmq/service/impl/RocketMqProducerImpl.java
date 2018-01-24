package com.lxy.rocketmq.service.impl;

import com.lxy.rocketmq.MQEntity;
import com.lxy.rocketmq.service.IProducer;
import com.lxy.rocketmq.SerializableUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * @author pengXiaoLin
 * @date 2018-01-22 14:26
 **/
public class RocketMqProducerImpl implements IProducer, InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(RocketMqProducerImpl.class);

    private String namesrvAddr;

    private String producerGroup;

    private Boolean retryAnotherBrokerWhenNotStoreOK;

    private DefaultMQProducer producer;


    /**
     * spring 容器初始化所有属性后调用此方法
     */
    public void afterPropertiesSet() throws Exception {
        producer = new DefaultMQProducer();
        producer.setProducerGroup(this.producerGroup);
        producer.setNamesrvAddr(this.namesrvAddr);
        producer.setRetryAnotherBrokerWhenNotStoreOK(this.retryAnotherBrokerWhenNotStoreOK);
//        producer.setVipChannelEnabled(false);
        /*
         * Producer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         * 注意：切记不可以在每次发送消息时，都调用start方法
         */
        producer.start();
        logger.info("[{}:{}] start successd!", producerGroup, namesrvAddr);
    }

    /**
     * 销毁
     */
    public void destroy() throws Exception {
        if (producer != null) {
            logger.info("producer: [{}:{}] end ", producerGroup, namesrvAddr);
            producer.shutdown();
        }

    }


    public void send(String topic, MQEntity entity) {
        String keys = UUID.randomUUID().toString();
        entity.setMqKey(keys);
        String tags;
        if (StringUtils.isEmpty(entity.getTags())) {
            tags = entity.getClass().getName();
        } else {
            tags = entity.getTags();
        }
        logger.info("生产者：发送消息 topic:{},tags:{},keys:{},entity:{}", new Object[]{topic, tags, keys, entity});
        Message msg = new Message(topic, tags, keys,
                SerializableUtil.toByte(entity));
        try {
            SendResult result = producer.send(msg);
            logger.info("消息发送状态:msgId:{},statue:{}", result.getMsgId(), result.getSendStatus());
            System.out.println("消息发送状态:msgId:{}" + result.getMsgId() + ",statue:{}" + result.getSendStatus());
            logger.info("消息发送状态:msgId:{},statue:{}", result.getMsgId(), result.getSendStatus());
        } catch (Exception e) {
            logger.error(keys.concat(":发送消息失败"), e);
            throw new RuntimeException("发送消息失败", e);
        }
    }


    public void send(String topic, MQEntity entity, SendCallback sendCallback) {
        String keys = UUID.randomUUID().toString();
        entity.setMqKey(keys);
        String tags;
        if (StringUtils.isEmpty(entity.getTags())) {
            tags = entity.getClass().getName();
        } else {
            tags = entity.getTags();
        }
        logger.info("生产者：发送消息 topic:{},tags:{},keys:{},entity:{}", new Object[]{topic, tags, keys, entity});
        Message msg = new Message(topic, tags, keys,
                SerializableUtil.toByte(entity));
        try {
            producer.send(msg, sendCallback);
        } catch (Exception e) {
            logger.error(keys.concat(":发送消息失败"), e);
            throw new RuntimeException("发送消息失败", e);
        }
    }


    public void sendOneway(String topic, MQEntity entity) {
        String keys = UUID.randomUUID().toString();
        entity.setMqKey(keys);
        String tags;
        if (StringUtils.isEmpty(entity.getTags())) {
            tags = entity.getClass().getName();
        } else {
            tags = entity.getTags();
        }
        logger.info("生产者：发送消息 topic:{},tags:{},keys:{},entity:{}", new Object[]{topic, tags, keys, entity});
        Message msg = new Message(topic, tags, keys,
                SerializableUtil.toByte(entity));
        try {
            producer.sendOneway(msg);
        } catch (Exception e) {
            logger.error(keys.concat(":发送消息失败"), e);
            throw new RuntimeException("发送消息失败", e);
        }
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }


    public void setProducerGroup(String producerGroup) {
        this.producerGroup = producerGroup;
    }


    public void setProducer(DefaultMQProducer producer) {
        this.producer = producer;
    }


    public void setRetryAnotherBrokerWhenNotStoreOK(
            Boolean retryAnotherBrokerWhenNotStoreOK) {
        this.retryAnotherBrokerWhenNotStoreOK = retryAnotherBrokerWhenNotStoreOK;
    }
}
