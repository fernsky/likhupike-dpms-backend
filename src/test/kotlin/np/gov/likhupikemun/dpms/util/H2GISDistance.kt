package np.gov.likhupikemun.dpms.util

import org.locationtech.jts.geom.Geometry
import kotlin.math.*

object H2GISDistance {
    private const val EARTH_RADIUS = 6371008.7714 // Earth's mean radius in meters

    @JvmStatic
    fun distanceSphere(
        geom1: Geometry,
        geom2: Geometry,
    ): Double {
        val point1 = geom1.centroid
        val point2 = geom2.centroid

        val lon1 = Math.toRadians(point1.x)
        val lat1 = Math.toRadians(point1.y)
        val lon2 = Math.toRadians(point2.x)
        val lat2 = Math.toRadians(point2.y)

        val dlon = lon2 - lon1
        val dlat = lat2 - lat1

        val a = sin(dlat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dlon / 2).pow(2)
        val c = 2 * asin(sqrt(a))

        return EARTH_RADIUS * c
    }
}
