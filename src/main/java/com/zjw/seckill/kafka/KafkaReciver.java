package com.zjw.seckill.kafka;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zjw.seckill.bean.SeckillOrder;
import com.zjw.seckill.bean.User;
import com.zjw.seckill.redis.SeckillMessage;
import com.zjw.seckill.service.GoodsService;
import com.zjw.seckill.service.OrderService;
import com.zjw.seckill.service.SeckillService;
import com.zjw.seckill.vo.GoodsVo;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;


/**
 * @Author:Alex
 * @Date: 2022/12/9 21:40
 */
@Service
public class KafkaReciver {
    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    SeckillService seckillService;
    private static Logger log = LoggerFactory.getLogger(KafkaReciver.class);
    private Gson gson = new GsonBuilder().create();

    @KafkaListener(topics = "seckill")
    public void receive(ConsumerRecord<String, String> record) throws Exception {

        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        // Object -> String
        String message = (String) kafkaMessage.get();
        log.info("receive message:"+message);
        // 反序列化
        SeckillMessage m = gson.fromJson((String) message, SeckillMessage.class);
        User user = m.getUser();
        long goodsId = m.getGoodsId();

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goodsVo.getStockCount();
        if(stock <= 0){
            return;
        }
        //判断重复秒杀
        SeckillOrder order = orderService.getOrderByUserIdGoodsId(user.getId(), goodsId);
        if(order != null) {
            return;
        }
        //减库存 下订单 写入秒杀订单
        seckillService.seckill(user, goodsVo);
    }
}
