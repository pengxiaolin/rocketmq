package com.lxy.rocketmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pengXiaoLin
 * @date 2018-01-22 14:43
 **/
public class ExampleRequestConsumer extends AbstractConsumer {
    private static Logger logger = LoggerFactory.getLogger(ExampleRequestConsumer.class);

    @Override
    public void execute(MQEntity entity) {
        logger.info(entity.getClass().getName());
        logger.info("消费者:接受到消息! 参数={}", entity.toString());
    }

}
