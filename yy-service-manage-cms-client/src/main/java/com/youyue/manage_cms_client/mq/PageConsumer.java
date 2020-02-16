package com.youyue.manage_cms_client.mq;

import com.alibaba.fastjson.JSON;
import com.youyue.manage_cms_client.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PageConsumer {
    @Autowired
    private PageService pageService;

    @Autowired
    private RedisTemplate redisTemplate;
    @Value("${server.port}")
    private String port;
    @RabbitListener(queues={"${youyue.mq.queue}"})
    public void  sendPostPageHtml(String jsonString){
        //1.将值从rabbitmq中取出来进行转换为map集合
        Map<String,String> map = JSON.parseObject(jsonString, Map.class);
        String pageId = map.get("pageId");
        try {
            pageService.savePageToServerPath(pageId);
            redisTemplate.boundHashOps(pageId).put(port,"发布页面成功！");
        }catch (Exception e){
            e.printStackTrace();
            //失败也发送消息到redis缓存
            redisTemplate.boundHashOps(pageId).put(port,"发布页面失败！");
        }

    }
}
