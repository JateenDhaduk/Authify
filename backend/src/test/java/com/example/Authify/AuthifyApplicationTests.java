package com.example.Authify;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.lettuce.core.RedisClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
	"spring.autoconfigure.exclude=" +
	"org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration," +
	"org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration," +
	"org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration"
})
class AuthifyApplicationTests {

	@MockitoBean
	private RedisClient redisClient;

	@MockitoBean
	private ProxyManager<byte[]> proxyManager;

	@MockitoBean
	private RedisConnectionFactory redisConnectionFactory;

	@MockitoBean
	private RedisTemplate<String, String> redisTemplate;

	@Test
	void contextLoads() {
	}

}
