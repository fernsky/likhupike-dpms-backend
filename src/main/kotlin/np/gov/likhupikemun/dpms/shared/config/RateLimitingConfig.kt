package np.gov.likhupikemun.dpms.shared.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RateLimitingConfig {
    fun createNewBucket(): Bucket {
        val limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)))
        return Bucket
            .builder()
            .addLimit(limit)
            .build()
    }
}
