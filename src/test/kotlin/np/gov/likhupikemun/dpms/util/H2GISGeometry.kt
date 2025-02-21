package np.gov.likhupikemun.dpms.util

import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel

object H2GISGeometry {
    private val geometryFactory = GeometryFactory(PrecisionModel(), 4326) // 4326 is for WGS84

    @JvmStatic
    fun point(
        x: Double,
        y: Double,
    ): Point = geometryFactory.createPoint(Coordinate(x, y))
}
