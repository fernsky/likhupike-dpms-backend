package np.gov.likhupikemun.dpms.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test") // Add this line to exclude from test profile
class RedisConfig {
    @Value("\${spring.data.redis.host:localhost}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port:6379}")
    private lateinit var port: String

    @Value("\${spring.data.redis.password:#{null}}")
    private var password: String? = null

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        val address = "redis://$host:$port"
        config
            .useSingleServer()
            .setAddress(address)
            .apply {
                password?.let { setPassword(it) }
            }.setConnectTimeout(10000)
            .setTimeout(10000)
        return Redisson.create(config)
    }
}
