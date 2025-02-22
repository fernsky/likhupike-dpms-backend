package np.gov.mofaga.imis.config

import jakarta.persistence.Entity
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.stereotype.Component

@Component
class EntityScanner {
    fun scanForEntities(basePackage: String): Set<Class<*>> {
        val scanner = ClassPathScanningCandidateComponentProvider(false)
        scanner.addIncludeFilter(AnnotationTypeFilter(Entity::class.java))

        return scanner
            .findCandidateComponents(basePackage)
            .mapNotNull { loadClass(it) }
            .toSet()
    }

    private fun loadClass(beanDefinition: BeanDefinition): Class<*>? =
        try {
            Class.forName(beanDefinition.beanClassName)
        } catch (e: ClassNotFoundException) {
            null
        }
}
