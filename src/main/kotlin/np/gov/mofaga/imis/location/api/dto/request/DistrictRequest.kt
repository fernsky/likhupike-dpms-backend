package np.gov.mofaga.imis.location.api.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.*
import np.gov.mofaga.imis.shared.dto.GeometryRequest
import java.math.BigDecimal
import java.util.*

data class CreateDistrictRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    val name: String,
    @field:NotBlank(message = "Nepali name is required")
    @field:Size(max = 100, message = "Nepali name must not exceed 100 characters")
    val nameNepali: String,
    @field:NotBlank(message = "Code is required")
    @field:Pattern(regexp = "^[A-Z0-9_-]{1,36}$", message = "Code must be 1-36 uppercase letters, numbers, dashes or underscores")
    val code: String,
    @field:NotNull(message = "Province Code is required")
    val provinceCode: String,
    @field:Positive(message = "Area must be positive")
    @field:Digits(integer = 8, fraction = 2, message = "Area must have at most 8 digits and 2 decimal places")
    val area: BigDecimal?,
    @field:PositiveOrZero(message = "Population must not be negative")
    val population: Long?,
    @field:Size(max = 50, message = "Headquarter must not exceed 50 characters")
    val headquarter: String?,
    @field:Size(max = 50, message = "Headquarter in Nepali must not exceed 50 characters")
    val headquarterNepali: String?,
    @field:NotNull(message = "Geometry is required")
    @field:Valid
    val geometry: GeometryRequest,
)

data class UpdateDistrictRequest(
    @field:Size(max = 100, message = "Name must not exceed 100 characters")
    val name: String?,
    @field:Size(max = 100, message = "Nepali name must not exceed 100 characters")
    val nameNepali: String?,
    @field:Positive(message = "Area must be positive")
    @field:Digits(integer = 8, fraction = 2, message = "Area must have at most 8 digits and 2 decimal places")
    val area: BigDecimal?,
    @field:PositiveOrZero(message = "Population must not be negative")
    val population: Long?,
    @field:Size(max = 50, message = "Headquarter must not exceed 50 characters")
    val headquarter: String?,
    @field:Size(max = 50, message = "Headquarter in Nepali must not exceed 50 characters")
    val headquarterNepali: String?,
    @field:Valid
    val geometry: GeometryRequest?,
)
