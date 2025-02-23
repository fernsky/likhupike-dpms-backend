package np.gov.mofaga.imis.shared.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import np.gov.mofaga.imis.shared.validation.ValidCoordinateList
import org.hibernate.validator.constraints.Range

typealias Coordinate = Array<Double>

// Extension functions for easier conversion
fun Coordinate.toLngLat() = Pair(this[1], this[0])

fun Coordinate.toJtsCoordinate() =
    org.locationtech.jts.geom
        .Coordinate(this[1], this[0])

data class GeometryRequest(
    @field:NotEmpty(message = "Type is required")
    val type: String,
    @field:Valid
    @field:ValidCoordinateList
    val coordinates: List<Coordinate> = emptyList(),
    @field:Valid
    @field:ValidCoordinateList
    val lines: List<List<Coordinate>>? = null,
    @field:Valid
    @field:ValidCoordinateList
    val polygons: List<List<Coordinate>>? = null,
    @field:Valid
    @field:ValidCoordinateList
    val multiPolygons: List<List<List<Coordinate>>>? = null,
    @field:Range(min = 0, message = "Radius must be positive")
    val radius: Double? = null,
    @field:Valid
    @field:ValidCoordinateList
    val holes: List<List<Coordinate>>? = null,
    @field:Range(min = 4326, max = 4326, message = "Only SRID 4326 (WGS84) is supported")
    val srid: Int = 4326,
) {
    init {
        require(type.lowercase() in SUPPORTED_TYPES) { "Invalid geometry type: $type" }
    }

    companion object {
        val SUPPORTED_TYPES =
            setOf(
                "point",
                "multipoint",
                "linestring",
                "multilinestring",
                "polygon",
                "multipolygon",
                "circle",
                "rectangle",
                "collection",
            )
    }
}
