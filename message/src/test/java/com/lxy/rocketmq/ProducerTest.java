package com.lxy.rocketmq;

import com.lxy.rocketmq.service.ExampleSendCallback;
import com.lxy.rocketmq.service.IProducer;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author pengXiaoLin
 * @date 2018-01-22 14:48
 **/
public class ProducerTest {
    private IProducer producer = null;
    private ExampleSendCallback exampleSendCallback = null;

    @Before
    public void befor() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath*:applicationContext.xml");
        producer = (IProducer) context.getBean("producer");
        exampleSendCallback = (ExampleSendCallback) context.getBean("exampleSendCallback");
    }

    @Test
    public void testSendMq() {
        ExampleRequest loanRequest = new ExampleRequest();
        loanRequest.setApplyNo("order_123456789");
        loanRequest.setTags("test1");
        //producer.send("loanRequest", loanRequest);
        producer.send("loanRequest", loanRequest, exampleSendCallback);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
