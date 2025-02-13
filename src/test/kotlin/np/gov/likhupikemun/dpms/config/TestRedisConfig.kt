package np.gov.likhupikemun.dpms.config

import org.mockito.kotlin.mock
import org.redisson.api.RedissonClient
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@TestConfiguration(proxyBeanMethods = false)
@AutoConfigureBefore(RedisAutoConfiguration::class)
@Profile("test")
class TestRedisConfig {
    @Bean
    @Primary
    @ConditionalOnMissingBean(RedissonClient::class)
    fun redissonClient(): RedissonClient = mock()

    @Bean
    @Primary
    fun redisConnectionFactory(): RedisConnectionFactory = mock()

    @Bean
    @Primary
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = GenericJackson2JsonRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        template.afterPropertiesSet() // Important: initialize the template
        return template
    }

    @Bean
    @Primary
    fun stringRedisTemplate(): RedisTemplate<String, String> {
        val template = RedisTemplate<String, String>()
        template.connectionFactory = redisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()
        template.hashValueSerializer = StringRedisSerializer()
        template.valueSerializer = StringRedisSerializer()
        template.afterPropertiesSet() // Important: initialize the template
        return template
    }
}
