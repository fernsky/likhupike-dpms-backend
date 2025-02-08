package np.gov.likhupikemun.dpms.shared.aspect

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Aspect
@Component
class AuditLogAspect {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Around(
        "@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)",
    )
    fun auditLog(joinPoint: ProceedingJoinPoint): Any {
        val startTime = LocalDateTime.now()
        val className = joinPoint.signature.declaringTypeName
        val methodName = joinPoint.signature.name

        logger.info("Started: $className.$methodName at $startTime")

        try {
            val result = joinPoint.proceed()
            val endTime = LocalDateTime.now()
            val duration = ChronoUnit.MILLIS.between(startTime, endTime)

            logger.info("Completed: $className.$methodName in ${duration}ms")
            return result
        } catch (e: Exception) {
            logger.error("Failed: $className.$methodName - ${e.message}")
            throw e
        }
    }
}
