package np.gov.likhupikemun.dpms.family.api.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.*
import np.gov.likhupikemun.dpms.shared.validation.annotations.ValidGpsCoordinates

data class UpdateFamilyRequest(
    @field:NotBlank
    val headOfFamily: String,
    @field:Min(1)
    val totalMembers: Int,
    @field:Valid
    val waterDetails: WaterDetailsRequest,
    @field:Valid
    val housingDetails: HousingDetailsRequest,
    @field:Valid
    val economicDetails: EconomicDetailsRequest,
    @field:Valid
    val agriculturalDetails: AgriculturalDetailsRequest,
    @field:ValidGpsCoordinates
    val latitude: Double?,
    @field:ValidGpsCoordinates
    val longitude: Double?,
)
