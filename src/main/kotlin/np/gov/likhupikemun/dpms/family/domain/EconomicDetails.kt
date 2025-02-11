package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.Embeddable
import java.math.BigDecimal

@Embeddable
class EconomicDetails(
    var monthlyIncome: BigDecimal,
    var hasEmployedMembers: Boolean = false,
    var numberOfEmployedMembers: Int = 0,
    var receivesSocialSecurity: Boolean = false,
    var hasBankAccount: Boolean = false,
    var hasLoans: Boolean = false
)
