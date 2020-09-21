package com.sinkovdenis.commentsgeneratorservice.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinkovdenis.commentsgeneratorservice.queue.RedisMessageSubscriber;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.*;


@Configuration
public class RedisConfig {
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisProperties properties = redisProperties();
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(properties.getHost());
        configuration.setPort(properties.getPort());

        return new JedisConnectionFactory(configuration);
    }

    @Bean
    @Primary
    public RedisProperties redisProperties() {
        return new RedisProperties();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();

        configureSerializers(template);

        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RedisMessageSubscriber redisMessageSubscriber) {
        return new MessageListenerAdapter(redisMessageSubscriber);
    }

    @Bean
    RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory,
                                                 MessageListenerAdapter listenerAdapter) {
        final RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory());
        container.addMessageListener(listenerAdapter, topicSource());
        return container;
    }

    private void configureSerializers(RedisTemplate<String, Object> template) {
        RedisSerializer<String> serializerKey = new StringRedisSerializer();
        template.setKeySerializer(serializerKey);
        template.setHashKeySerializer(serializerKey);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
    }


    @Bean("topicSource")
    ChannelTopic topicSource() {
        return new ChannelTopic("pubsub:source");
    }

    @Bean("topicResponse")
    ChannelTopic topicResponse() {
        return new ChannelTopic("pubsub:response");
    }
}
