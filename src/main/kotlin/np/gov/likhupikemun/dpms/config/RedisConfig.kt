package np.gov.likhupikemun.dpms.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port}")
    private lateinit var port: String

    @Value("\${spring.data.redis.password}")
    private lateinit var password: String

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        config
            .useSingleServer()
            .setAddress("redis://$host:$port")
            .setPassword(password)
            .setConnectTimeout(10000)
            .setTimeout(10000)
        return Redisson.create(config)
    }
}
