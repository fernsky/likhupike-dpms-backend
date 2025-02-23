package np.gov.mofaga.imis.location.api.dto.response

import org.geojson.GeoJsonObject

data class GeoJsonResponse<T>(
    val type: String = "Feature",
    val geometry: GeoJsonObject?,
    val properties: T,
)
