package np.gov.likhupikemun.dpms.config

import jakarta.annotation.PostConstruct
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.*
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

@TestConfiguration
@Primary
class TestRedisConfig {
    private lateinit var redisContainer: GenericContainer<*>

    @PostConstruct
    fun startContainer() {
        redisContainer =
            GenericContainer(DockerImageName.parse("redis:7.2-alpine"))
                .withExposedPorts(6379)
                .withReuse(true)
                .waitingFor(Wait.forListeningPort())
        redisContainer.start()
    }

    @Bean
    @Primary
    fun redissonClient(): RedissonClient {
        val config = Config()
        val redisAddress = "redis://${redisContainer.host}:${redisContainer.getMappedPort(6379)}"
        println("Test Redis Container Address: $redisAddress")
        config
            .useSingleServer()
            .setAddress(redisAddress)
            .setConnectionMinimumIdleSize(1)
            .setConnectionPoolSize(2)
            .setTimeout(5000)
            .setRetryAttempts(3)
        return Redisson.create(config)
    }

    @Bean
    @Primary
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisConfig =
            RedisStandaloneConfiguration().apply {
                hostName = redisContainer.host
                port = redisContainer.getMappedPort(6379)
            }
        return LettuceConnectionFactory(redisConfig).apply {
            afterPropertiesSet()
        }
    }

    @Bean
    @Primary
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> =
        RedisTemplate<String, String>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = StringRedisSerializer()
            afterPropertiesSet()
        }
}
