package com.zjw.seckill.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.seckill.redis.SeckillMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author:Alex
 * @Date: 2022/12/9 21:30
 */
@Service
public class KafkaSender {
    private static Logger log = LoggerFactory.getLogger(KafkaSender.class);

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Value("seckill")
    private String kafkaTopic;

    private Gson gson=new GsonBuilder().create();

    public void sendSeckillMessage(SeckillMessage message){
        String msg = gson.toJson(message);
        log.info("send message:"+msg);
        kafkaTemplate.send(kafkaTopic, msg);
    }
}
