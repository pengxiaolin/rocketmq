package com.lxy.rocketmq;

import com.lxy.rocketmq.service.IConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pengXiaoLin
 * @date 2018-01-22 14:33
 **/
public abstract class AbstractConsumer implements IConsumer {

    protected Logger logger = LoggerFactory.getLogger(AbstractConsumer.class);

    private String classTypeName = MQEntity.class.getName();

    private String Tags;
    public void handlerMessage(MessageExt msg) {
        try {
            MQEntity entity = doStart(msg);
            entity.setMqId(msg.getMsgId());
            execute(entity);
            doEnd(entity);
        } catch (Exception e) {
            logger.error("处理mq消息异常。", e);
        }
    }

    /**
     * 解析mq消息前置处理
     *
     * @param msg
     * @throws ClassNotFoundException
     */
    protected MQEntity doStart(MessageExt msg) throws ClassNotFoundException {
        Class<? extends MQEntity> clazz = (Class<? extends MQEntity>) Class.forName(classTypeName);
        return SerializableUtil.parse(msg.getBody(), clazz);
    }

    /**
     * 解析mq消息后置处理
     *
     * @param entity
     */
    protected void doEnd(MQEntity entity) {

    }

    /**
     * 解析mq消息 MessageExt
     *
     * @param entity
     */
    public abstract void execute(MQEntity entity);

    public void setClassTypeName(String classTypeName) {
        this.classTypeName = classTypeName;
    }

    public String getTags() {
        return Tags;
    }

    public void setTags(String tags) {
        Tags = tags;
    }
}