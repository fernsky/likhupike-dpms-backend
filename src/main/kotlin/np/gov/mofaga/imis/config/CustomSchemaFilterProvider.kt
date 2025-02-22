package np.gov.mofaga.imis.config

import org.hibernate.boot.model.relational.Namespace
import org.hibernate.boot.model.relational.Sequence
import org.hibernate.mapping.Table
import org.hibernate.tool.schema.spi.SchemaFilter
import org.hibernate.tool.schema.spi.SchemaFilterProvider

class CustomSchemaFilterProvider : SchemaFilterProvider {
    override fun getCreateFilter(): SchemaFilter = CustomSchemaFilter()

    override fun getDropFilter(): SchemaFilter = CustomSchemaFilter()

    override fun getMigrateFilter(): SchemaFilter = CustomSchemaFilter()

    override fun getValidateFilter(): SchemaFilter = CustomSchemaFilter()

    override fun getTruncatorFilter(): SchemaFilter = CustomSchemaFilter() // Added missing method
}

class CustomSchemaFilter : SchemaFilter {
    override fun includeNamespace(namespace: Namespace): Boolean {
        // Only include the public schema
        return namespace.name.schema?.toString() == "public" // Fixed comparison
    }

    override fun includeTable(table: Table): Boolean {
        // Exclude Liquibase and Spring Session tables from Hibernate management
        val excludedTables =
            setOf(
                "databasechangelog",
                "databasechangeloglock",
                "spring_session",
                "spring_session_attributes",
            )
        return !excludedTables.contains(table.name.lowercase())
    }

    override fun includeSequence(sequence: Sequence): Boolean = true
}
