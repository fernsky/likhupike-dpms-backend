package np.gov.likhupikemun.dpms.family.domain

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.annotations.Comment
import java.math.BigDecimal

@Embeddable
class EconomicDetails {
    @Column(name = "monthly_income", precision = 12)
    @PositiveOrZero
    @Comment("Monthly income of the family in NPR")
    var monthlyIncome: BigDecimal? = null

    @Column(name = "has_employed_members")
    @Comment("Indicates if any family members are employed")
    var hasEmployedMembers: Boolean = false

    @Min(0)
    @Column(name = "number_of_employed_members")
    @Comment("Number of employed family members")
    var numberOfEmployedMembers: Int = 0

    @Column(name = "receives_social_security")
    @Comment("Indicates if family receives any social security benefits")
    var receivesSocialSecurity: Boolean = false

    @Column(name = "has_bank_account")
    @Comment("Indicates if family has a bank account")
    var hasBankAccount: Boolean = false

    @Column(name = "has_loans")
    @Comment("Indicates if family has any outstanding loans")
    var hasLoans: Boolean = false

    @Column(name = "loan_amount", precision = 12)
    @PositiveOrZero
    @Comment("Total outstanding loan amount in NPR")
    var loanAmount: BigDecimal? = null

    @Column(name = "primary_income_source", length = 100)
    @Comment("Primary source of family income")
    var primaryIncomeSource: String? = null
}
