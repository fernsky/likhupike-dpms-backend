package np.gov.likhupikemun.dpms.shared.dto

import jakarta.validation.constraints.Min

open class BaseSearchCriteria(
    @field:Min(0)
    open val page: Int = 0,
    @field:Min(1)
    open val pageSize: Int = 20,
)
