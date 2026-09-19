package com.foodrescue.config;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

/**
 * Redis cache configuration.
 *
 * We use JSON serialization so cached objects are readable in Redis CLI.
 * Default TTL is 10 minutes — override per cache if needed.
 *
 * IMPORTANT: If Redis is unavailable, set spring.cache.type=simple in
 * application.yml and the app will use a simple in-memory cache instead.
 */
@Configuration
public class RedisConfig implements CachingConfigurer {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))  // Cache entries expire after 10 minutes
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * If Redis is down, log the error but don't crash the application.
     * The request will just hit the database directly.
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception,
                                             org.springframework.cache.Cache cache,
                                             Object key) {
                org.slf4j.LoggerFactory.getLogger(RedisConfig.class)
                        .warn("Redis cache GET error for key '{}': {}", key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception,
                                             org.springframework.cache.Cache cache,
                                             Object key, Object value) {
                org.slf4j.LoggerFactory.getLogger(RedisConfig.class)
                        .warn("Redis cache PUT error for key '{}': {}", key, exception.getMessage());
            }
        };
    }
}
