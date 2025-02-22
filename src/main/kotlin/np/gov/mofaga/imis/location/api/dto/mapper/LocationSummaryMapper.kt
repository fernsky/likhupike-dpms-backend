package np.gov.mofaga.imis.location.api.dto.mapper

import np.gov.mofaga.imis.location.api.dto.response.DistrictSummaryResponse
import np.gov.mofaga.imis.location.api.dto.response.MunicipalitySummaryResponse
import np.gov.mofaga.imis.location.api.dto.response.ProvinceSummaryResponse
import np.gov.mofaga.imis.location.domain.District
import np.gov.mofaga.imis.location.domain.Municipality
import np.gov.mofaga.imis.location.domain.Province

interface LocationSummaryMapper {
    fun toDistrictSummary(district: District): DistrictSummaryResponse

    fun toProvinceSummary(province: Province): ProvinceSummaryResponse

    fun toMunicipalitySummary(municipality: Municipality): MunicipalitySummaryResponse
}
