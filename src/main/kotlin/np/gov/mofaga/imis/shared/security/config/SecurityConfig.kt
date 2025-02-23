package np.gov.mofaga.imis.shared.security.config

import np.gov.mofaga.imis.shared.config.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val jwtAuthenticationFilter: JwtAuthenticationFilter,
    private val authenticationProvider: AuthenticationProvider,
    private val authenticationEntryPoint: CustomAuthenticationEntryPoint,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntryPoint)
            }.authorizeHttpRequests {
                it
                    // Public endpoints - no authentication required
                    .requestMatchers(
                        "/api/v1/auth/**",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/**",
                        "/error",
                        "/webjars/**",
                    ).permitAll()
                    // Public GET endpoints for location data
                    .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/provinces/**",
                        "/api/v1/districts/**",
                        "/api/v1/municipalities/**",
                        "/api/v1/wards/**"
                    ).permitAll()
                    // Super Admin only endpoints
                    .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/provinces/**",
                        "/api/v1/districts/**",
                        "/api/v1/municipalities/**"
                    ).hasRole("SUPER_ADMIN")
                    .requestMatchers(
                        HttpMethod.PUT,
                        "/api/v1/provinces/**",
                        "/api/v1/districts/**",
                        "/api/v1/municipalities/**"
                    ).hasRole("SUPER_ADMIN")
                    // Municipality Admin and Super Admin endpoints
                    .requestMatchers(
                        HttpMethod.PUT,
                        "/api/v1/wards/**"
                    ).hasAnyRole("MUNICIPALITY_ADMIN", "SUPER_ADMIN")
                    // Default rule - require authentication for any other endpoint
                    .anyRequest()
                    .authenticated()
            }.sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }.authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("*")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
        configuration.allowedHeaders = listOf("*")
        configuration.exposedHeaders = listOf("Authorization")
        configuration.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }
}
