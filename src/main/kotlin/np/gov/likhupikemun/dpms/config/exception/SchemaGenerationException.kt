package np.gov.likhupikemun.dpms.config.exception

sealed class SchemaGenerationException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    class DatabaseConnectionError(
        cause: Throwable,
    ) : SchemaGenerationException("Failed to connect to database", cause)

    class SchemaWriteError(
        path: String,
        cause: Throwable,
    ) : SchemaGenerationException("Failed to write schema to $path", cause)

    class EntityScanError(
        cause: Throwable,
    ) : SchemaGenerationException("Failed to scan entities", cause)

    class ChangelogUpdateError(
        path: String,
        cause: Throwable,
    ) : SchemaGenerationException("Failed to update changelog at $path", cause)

    class SchemaGenerationError(
        cause: Throwable,
    ) : SchemaGenerationException("Failed to generate schema", cause)
}
