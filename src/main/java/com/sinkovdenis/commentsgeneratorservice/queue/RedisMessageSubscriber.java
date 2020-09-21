package com.sinkovdenis.commentsgeneratorservice.queue;

import com.sinkovdenis.commentsgeneratorservice.service.PrepareAnswerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class RedisMessageSubscriber implements MessageListener {

    Logger logger = LoggerFactory.getLogger(RedisMessageSubscriber.class);


    private PrepareAnswerService prepareAnswerService;

    @Autowired()
    public void RedisMessageSubscriber(PrepareAnswerService prepareAnswerService){
        this.prepareAnswerService = prepareAnswerService;
    }

    @Override
    public void onMessage(final Message message, byte[] pattern) {
        logger.debug("Received message");
        if(message != null) {
            prepareAnswerService.createAnswer(message.toString());
        }
    }
}
