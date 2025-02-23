package np.gov.mofaga.imis.location.api.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import np.gov.mofaga.imis.location.domain.MunicipalityType
import np.gov.mofaga.imis.shared.dto.GeometryRequest
import java.math.BigDecimal
import java.util.*

@Schema(description = "Request payload for creating a new municipality")
data class CreateMunicipalityRequest(
    @field:NotBlank(message = "Municipality name is required")
    @field:Size(max = 100, message = "Municipality name must not exceed 100 characters")
    @Schema(description = "Name of the municipality in English", example = "Pokhara")
    val name: String,
    @field:NotBlank(message = "Municipality Nepali name is required")
    @field:Size(max = 100, message = "Municipality Nepali name must not exceed 100 characters")
    @Schema(description = "Name of the municipality in Nepali", example = "पोखरा")
    val nameNepali: String,
    @field:NotBlank(message = "Municipality code is required")
    @field:Pattern(regexp = "^[A-Z0-9_-]{1,36}$", message = "Code must be 1-36 uppercase letters, numbers, dashes or underscores")
    @Schema(description = "Unique code for the municipality", example = "PKR001")
    val code: String,
    @field:NotNull(message = "Municipality type is required")
    @Schema(description = "Type of municipality", example = "METROPOLITAN_CITY")
    val type: MunicipalityType,
    @field:DecimalMin(value = "0.0", message = "Area must be positive")
    @field:Digits(integer = 8, fraction = 2, message = "Invalid area format")
    @Schema(description = "Total area in square kilometers", example = "465.23")
    val area: BigDecimal?,
    @field:PositiveOrZero(message = "Population must not be negative")
    @Schema(description = "Total population", example = "450000")
    val population: Long?,
    @field:DecimalMin(value = "-90.0")
    @field:DecimalMax(value = "90.0")
    @Schema(description = "Latitude coordinate", example = "28.209538")
    val latitude: BigDecimal?,
    @field:DecimalMin(value = "-180.0")
    @field:DecimalMax(value = "180.0")
    @Schema(description = "Longitude coordinate", example = "83.985965")
    val longitude: BigDecimal?,
    @field:Min(value = 1, message = "Total wards must be at least 1")
    @field:Max(value = 35, message = "Total wards cannot exceed 35")
    @Schema(description = "Total number of wards", example = "33")
    val totalWards: Int,
    @field:NotNull(message = "District ID is required")
    @Schema(description = "ID of the district this municipality belongs to")
    val districtCode: String,
    @field:NotNull(message = "Geometry is required")
    @field:Valid
    @Schema(description = "Geometry data of the municipality")
    val geometry: GeometryRequest,
)

@Schema(description = "Request payload for updating an existing municipality")
data class UpdateMunicipalityRequest(
    @field:Size(max = 100, message = "Municipality name must not exceed 100 characters")
    @Schema(description = "Name of the municipality in English", example = "Pokhara")
    val name: String?,
    @field:Size(max = 100, message = "Municipality Nepali name must not exceed 100 characters")
    @Schema(description = "Name of the municipality in Nepali", example = "पोखरा")
    val nameNepali: String?,
    @field:DecimalMin(value = "0.0", message = "Area must be positive")
    @field:Digits(integer = 8, fraction = 2, message = "Invalid area format")
    @Schema(description = "Total area in square kilometers", example = "465.23")
    val area: BigDecimal?,
    @field:PositiveOrZero(message = "Population must not be negative")
    @Schema(description = "Total population", example = "450000")
    val population: Long?,
    @field:DecimalMin(value = "-90.0")
    @field:DecimalMax(value = "90.0")
    @Schema(description = "Latitude coordinate", example = "28.209538")
    val latitude: BigDecimal?,
    @field:DecimalMin(value = "-180.0")
    @field:DecimalMax(value = "180.0")
    @Schema(description = "Longitude coordinate", example = "83.985965")
    val longitude: BigDecimal?,
    @field:Min(value = 1, message = "Total wards must be at least 1")
    @field:Max(value = 35, message = "Total wards cannot exceed 35")
    @Schema(description = "Total number of wards", example = "33")
    val totalWards: Int?,
    @field:Valid
    @Schema(description = "Geometry data of the municipality")
    val geometry: GeometryRequest?,
)
