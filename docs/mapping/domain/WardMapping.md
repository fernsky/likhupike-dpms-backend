# Ward Domain Mapping Documentation

## Overview

This document details the mapping logic between Ward domain entities and DTOs in the DPMS system.

## Core Mappings

### Domain to DTO Mappings

### Mapping Flows

1. Creation Flow (Request → Entity)

```
CreateWardRequest → Ward
├── municipalityId       → municipality    (Resolved via Repository)
├── wardNumber          → wardNumber     (Direct mapping)
├── area               → area           (Validated BigDecimal)
├── population         → population     (Validated Long)
├── latitude          → latitude       (Validated coordinate)
├── longitude         → longitude      (Validated coordinate)
├── officeLocation    → officeLocation (Sanitized String)
└── officeLocationNepali → officeLocationNepali (Sanitized String)

// Additional fields set during creation:
- isActive = true
- createdAt = LocalDateTime.now()
- updatedAt = LocalDateTime.now()
```

2. Response Mappings (Entity → DTOs)

#### Basic Response

```
Ward → WardResponse
├── id                  → id                (Direct UUID)
├── wardNumber         → wardNumber       (Direct Int)
├── area              → area             (Formatted BigDecimal)
├── population        → population       (Direct Long)
├── latitude         → latitude         (Formatted coordinate)
├── longitude        → longitude        (Formatted coordinate)
├── officeLocation   → officeLocation   (Direct String)
└── municipality     → municipalitySummary (Via MunicipalityMapper)
```

#### Detailed Response

```
Ward → WardDetailResponse
├── [All WardResponse fields]
└── statistics      → stats
    ├── totalFamilies        (Aggregated count)
    ├── totalPopulation     (Aggregated sum)
    ├── demographicBreakdown (Grouped by category)
    ├── economicStats       (Calculated averages)
    └── infrastructureStats (Aggregated counts)
```

#### Validation Rules

Geographic Data

- Latitude: -90° to +90°, 6 decimal precision
- Longitude: -180° to +180°, 6 decimal precision
- Area: Positive number, max 8 digits, 2 decimal places

Ward Number

- Range: 1-33 (Nepal's municipality ward limit)
- Unique within municipality
- Immutable after creation

Text Fields

- Office Location: Max 100 characters
- Office Location (Nepali): Max 100 characters, UTF-8 encoding

#### Statistics Calculation

Population Density

```
fun calculatePopulationDensity(population: Long, area: BigDecimal): BigDecimal {
    if (area == BigDecimal.ZERO) return BigDecimal.ZERO
    return BigDecimal(population)
        .divide(area, 2, RoundingMode.HALF_UP)
}
```

Demographic Statistics

```
fun calculateDemographicBreakdown(families: List<Family>): Map<String, Long> =
    families
        .groupBy { it.socialCategory }
        .mapValues { it.value.sumOf { family -> family.totalMembers ?: 0 }.toLong() }
```
