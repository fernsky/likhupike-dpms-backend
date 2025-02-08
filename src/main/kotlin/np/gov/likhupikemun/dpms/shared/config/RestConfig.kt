package np.gov.likhupikemun.dpms.shared.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.rest.core.config.RepositoryRestConfiguration
import org.springframework.data.rest.core.mapping.RepositoryDetectionStrategy
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry

@Configuration
class RestConfig {
    @Bean
    fun repositoryRestConfigurer(): RepositoryRestConfigurer =
        RepositoryRestConfigurer.withConfig { config: RepositoryRestConfiguration, cors: CorsRegistry ->
            config.setBasePath("/api")
            config.repositoryDetectionStrategy =
                RepositoryDetectionStrategy.RepositoryDetectionStrategies.ANNOTATED

            cors
                .addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .maxAge(3600)
        }
}
