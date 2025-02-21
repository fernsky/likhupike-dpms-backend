package np.gov.likhupikemun.dpms.location.api.dto.enums

enum class MunicipalitySortField {
    NAME,
    CODE,
    TYPE,
    POPULATION,
    AREA,
    DISTANCE,
    ;

    fun toEntityField(): String =
        when (this) {
            NAME -> "name"
            CODE -> "code"
            TYPE -> "type"
            POPULATION -> "population"
            AREA -> "area"
            DISTANCE -> "distance"
        }
}
