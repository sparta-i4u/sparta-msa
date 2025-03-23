package com.i4u.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		/* 설정 구성 우선 진행
		 * Redis를 이용해 Spring Cache 사용 시, Redis 관련 설정을 모아둠 */

		RedisCacheConfiguration configuration = RedisCacheConfiguration
			.defaultCacheConfig()
			// null 캐싱 여부
			.disableCachingNullValues()
			// 기본 캐시 유지 시간 (Time To Live)
			.entryTtl(Duration.ofSeconds(120))
			// 캐시 구분 접두사
			.computePrefixWith(CacheKeyPrefix.simple())
			// 캐시에 저장하는 값 직렬화/역직렬화 할 방법
			.serializeValuesWith(
				RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.java())
			);

		return RedisCacheManager
			.builder(redisConnectionFactory)
			.cacheDefaults(configuration)
			.build();
	}

}

