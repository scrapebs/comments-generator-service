package com.sinkovdenis.commentsgeneratorservice.queue;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisMessagePublisher implements MessagePublisher {

    Logger logger = LoggerFactory.getLogger(RedisMessagePublisher.class);

    private RedisTemplate<String, Object> redisTemplate;
    private ChannelTopic topic;

    @Autowired
    public RedisMessagePublisher(RedisTemplate<String, Object> redisTemplate, @Qualifier("topicResponse") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(JSONObject message) {
        redisTemplate.convertAndSend(topic.getTopic(), message.toString());
        logger.debug("Message was published");
    }
}
