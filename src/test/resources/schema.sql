-- Initialize H2GIS functions
CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";

CALL H2GIS_SPATIAL ();

-- Add custom ST_DISTANCE_SPHERE implementation
CREATE ALIAS IF NOT EXISTS ST_DISTANCE_SPHERE FOR "np.gov.mofaga.imis.util.H2GISDistance.distanceSphere";

-- Add custom POINT implementation
CREATE ALIAS IF NOT EXISTS POINT FOR "np.gov.mofaga.imis.util.H2GISGeometry.point";