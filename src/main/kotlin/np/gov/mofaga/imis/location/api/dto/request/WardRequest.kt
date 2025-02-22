package np.gov.mofaga.imis.location.api.dto.request

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.util.*

data class CreateWardRequest(
    @field:NotNull(message = "Municipality Code is required")
    val municipalityCode: String,
    @field:NotNull(message = "Ward number is required")
    @field:Min(1, message = "Ward number must be at least 1")
    @field:Max(33, message = "Ward number cannot exceed 33")
    val wardNumber: Int,
    @field:Positive(message = "Area must be positive")
    @field:Digits(integer = 8, fraction = 2, message = "Area must have at most 8 digits and 2 decimal places")
    val area: BigDecimal?,
    @field:PositiveOrZero(message = "Population must not be negative")
    val population: Long?,
    @field:DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
    val latitude: BigDecimal?,
    @field:DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
    val longitude: BigDecimal?,
    @field:Size(max = 100, message = "Office location must not exceed 100 characters")
    val officeLocation: String?,
    @field:Size(max = 100, message = "Office location in Nepali must not exceed 100 characters")
    val officeLocationNepali: String?,
)

data class UpdateWardRequest(
    @field:Positive(message = "Area must be positive")
    @field:Digits(integer = 8, fraction = 2, message = "Area must have at most 8 digits and 2 decimal places")
    val area: BigDecimal?,
    @field:PositiveOrZero(message = "Population must not be negative")
    val population: Long?,
    @field:DecimalMin(value = "-90", message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90", message = "Latitude must be between -90 and 90")
    val latitude: BigDecimal?,
    @field:DecimalMin(value = "-180", message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180", message = "Longitude must be between -180 and 180")
    val longitude: BigDecimal?,
    @field:Size(max = 100, message = "Office location must not exceed 100 characters")
    val officeLocation: String?,
    @field:Size(max = 100, message = "Office location in Nepali must not exceed 100 characters")
    val officeLocationNepali: String?,
)
