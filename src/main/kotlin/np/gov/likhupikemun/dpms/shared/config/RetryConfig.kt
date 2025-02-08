package np.gov.likhupikemun.dpms.shared.config

import org.springframework.context.annotation.Configuration
import org.springframework.retry.annotation.EnableRetry
import org.springframework.retry.backoff.ExponentialBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate

@Configuration
@EnableRetry
class RetryConfig {
    fun retryTemplate(): RetryTemplate {
        val retryTemplate = RetryTemplate()

        val backOffPolicy = ExponentialBackOffPolicy()
        backOffPolicy.initialInterval = 1000L
        backOffPolicy.multiplier = 2.0
        backOffPolicy.maxInterval = 10000L

        val retryPolicy = SimpleRetryPolicy()
        retryPolicy.maxAttempts = 3

        retryTemplate.setBackOffPolicy(backOffPolicy)
        retryTemplate.setRetryPolicy(retryPolicy)

        return retryTemplate
    }
}
