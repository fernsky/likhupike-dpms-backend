-- Initialize H2GIS functions
CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";

CALL H2GIS_SPATIAL ();