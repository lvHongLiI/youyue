package com.youyue.manage_cms_client.mq;

import com.youyue.manage_cms_client.service.PageService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageConsumer {
    @Autowired
    private PageService pageService;

    @RabbitListener(queues={"${youyue.mq.queue}"})
    public void  sendPostPageHtml(String pageId){
        pageService.savePageToServerPath(pageId);
    }
}
