package com.likelionsns.final_project.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.database}")
    private int redisDatabase;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        config.setDatabase(redisDatabase);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    public RedisTemplate<?, ?> redisTemplate() {

        // redisTemplate 를 받아와서 set, get, delete 를 사용
        RedisTemplate<byte[], byte[]> redisTemplate = new RedisTemplate<>();
        /*
         * setKeySerializer, setValueSerializer 설정
         * redis-cli 을 통해 직접 데이터를 조회 시 알아볼 수 없는 형태로 출력되는 것을 방지
         */
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        return redisTemplate;
    }
}