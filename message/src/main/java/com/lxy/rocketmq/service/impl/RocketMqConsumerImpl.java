package com.lxy.rocketmq.service.impl;

import com.lxy.rocketmq.AbstractConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pengXiaoLin
 * @date 2018-01-22 14:29
 **/
public class RocketMqConsumerImpl implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(RocketMqConsumerImpl.class);

    private DefaultMQPushConsumer consumer;

    private String namesrvAddr;

    private String consumerGroup;

    private String messageModel;

    private String messageListener;

    private Map<String, AbstractConsumer> handlermap = new HashMap<String, AbstractConsumer>();

    private void initializingMessageSelector() throws InterruptedException, MQClientException {
        consumer = new DefaultMQPushConsumer();
        if (this.consumerGroup != null && this.consumerGroup.trim().length() > 0) {
            consumer.setConsumerGroup(this.consumerGroup);
            logger.debug("set consumer group " + this.consumerGroup);
        }
        consumer.setNamesrvAddr(this.namesrvAddr);
        consumer.setConsumeMessageBatchMaxSize(1);
        logger.debug("set consumer name server address " + this.namesrvAddr);
        logger.debug("set consumer message batch max size " + 1);

        if ("BROADCASTING".equals(messageModel)) {
            consumer.setMessageModel(MessageModel.BROADCASTING);
            logger.debug("set consumer message model BROADCASTING");
        } else if ("CLUSTERING".equals(messageModel)) {
            consumer.setMessageModel(MessageModel.CLUSTERING);
            logger.debug("set consumer message model CLUSTERING");
        } else {
            logger.debug("set consumer message model should be BROADCASTING or CLUSTERING");
            throw new RuntimeException("set consumer message model should be BROADCASTING or CLUSTERING");
        }
        /**
         * 订阅指定topic下所有消息<br>
         * 注意：一个consumer对象可以订阅多个topic
         */
        if (handlermap != null && !handlermap.isEmpty()) {
            for (String topic : handlermap.keySet()) {
                if (StringUtils.isEmpty(handlermap.get(topic).getTags())) {
                    // 如果没有写明tags 就消费所有的
                    consumer.subscribe(topic, "*");
                    logger.debug("consumer subscribe topic " + topic + " *");
                } else {
                    // 格式 "TagA || TagC || TagD"
                    consumer.subscribe(topic, handlermap.get(topic).getTags());
                    logger.debug("consumer subscribe topic " + topic + " Tags " + handlermap.get(topic).getTags());
                }
            }
        } else {
            logger.debug("you should provide at least one message handler.");
            throw new RuntimeException("you should provide at least one message handler.");
        }
        /**
         * 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费<br>
         * 如果非第一次启动，那么按照上次消费的位置继续消费
         */
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        if ("CONCURRENTLY".equals(messageListener)) {
            consumer.registerMessageListener(new MessageListenerConcurrently() {

                /**
                 * 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
                 */
                public ConsumeConcurrentlyStatus consumeMessage(final List<MessageExt> msgs, final ConsumeConcurrentlyContext context) {
                    try {
                        if (msgs != null && !msgs.isEmpty()) {
                            for (MessageExt msg : msgs) {
                                logger.debug(String.format("start consum message: message:id:%s topic:%s tags:%s ", msg.getMsgId(), msg.getTopic(), msg.getTags()));
                                AbstractConsumer handler = handlermap.get(msg.getTopic());
                                if (handler != null) {
                                    handler.handlerMessage(msg);
                                }
                                logger.debug(String.format("consume message success! message:id:%s topic:%s tags:%s ", msg.getMsgId(), msg.getTopic(), msg.getTags()));
                            }
                        }
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } catch (Exception e) {
                        logger.error("consume message error!", e);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
            });
        } else if ("ORDERLY".equals(messageListener)) {
            consumer.registerMessageListener(new MessageListenerOrderly() {

                /**
                 * 默认msgs里只有一条消息，可以通过设置consumeMessageBatchMaxSize参数来批量接收消息
                 */
                public ConsumeOrderlyStatus consumeMessage(final List<MessageExt> msgs, final ConsumeOrderlyContext context) {
                    try {
                        if (msgs != null && !msgs.isEmpty()) {
                            for (MessageExt msg : msgs) {
                                logger.debug(String.format("start consum message: message:id:%s topic:%s tags:%s message:%s", msg.getMsgId(), msg.getTopic(), msg.getTags(), new String(msg.getBody())));
                                AbstractConsumer handler = handlermap.get(msg.getTopic());
                                if (handler != null) {
                                    handler.handlerMessage(msg);
                                }
                                logger.debug(String.format("consume message success! message:id:%s topic:%s tags:%s message:%s", msg.getMsgId(), msg.getTopic(), msg.getTags(), new String(msg.getBody())));
                            }
                        }
                        return ConsumeOrderlyStatus.SUCCESS;
                    } catch (Exception e) {
                        logger.error("consume message error!", e);
                        return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                    }
                }
            });
        }

        /**
         * Consumer对象在使用之前必须要调用start初始化，初始化一次即可<br>
         */
        consumer.start();
        logger.debug("consumer start successd!");
    }

    public void afterPropertiesSet() throws Exception {
        initializingMessageSelector();
    }

    public void destroy() throws Exception {
        if (consumer != null) {
            consumer.shutdown();
            logger.debug("consumer shutdown!");
        }
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public void setConsumerGroup(String consumerGroup) {
        this.consumerGroup = consumerGroup;
    }

    public void setMessageModel(String messageModel) {
        this.messageModel = messageModel;
    }

    public void setMessageListener(String messageListener) {
        this.messageListener = messageListener;
    }

    public void setHandlermap(Map<String, AbstractConsumer> handlermap) {
        this.handlermap = handlermap;
    }
}
