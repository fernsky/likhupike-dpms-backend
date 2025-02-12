package np.gov.likhupikemun.dpms.config

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.*
import org.springframework.context.annotation.Profile
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
@Profile("test")
class TestRedisConfig {
    private lateinit var redisContainer: GenericContainer<*>

    @PostConstruct
    fun startContainer() {
        redisContainer =
            GenericContainer(DockerImageName.parse("redis:7.2-alpine"))
                .withReuse(true)
                .withExposedPorts(6379)
                .waitingFor(Wait.forListeningPort())
        redisContainer.start()

        // Set system properties to override application properties
        System.setProperty("spring.data.redis.host", redisContainer.host)
        System.setProperty("spring.data.redis.port", redisContainer.getMappedPort(6379).toString())
        System.setProperty("spring.data.redis.database", "0")
    }

    @PreDestroy
    fun cleanupContainer() {
        if (this::redisContainer.isInitialized) {
            redisContainer.stop()
            // Clean up system properties
            System.clearProperty("spring.redis.host")
            System.clearProperty("spring.redis.port")
            System.clearProperty("spring.redis.database")
        }
    }

    @Bean
    @Primary
    fun redissonClient(): RedissonClient {
        val config = Config()
        val redisAddress = "redis://${redisContainer.host}:${redisContainer.getMappedPort(6379)}"
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
