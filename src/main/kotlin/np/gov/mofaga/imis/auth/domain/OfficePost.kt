package np.gov.mofaga.imis.auth.domain

enum class OfficePost(
    val title: String,
) {
    CHIEF_ADMINISTRATIVE_OFFICER("Chief Administrative Officer"),
    MANAGER("Manager"),
    EMPLOYEE("Employee"),
    IT_OFFICER("IT Officer"),
    ADMINISTRATIVE_OFFICER("Administrative Officer"),
    ACCOUNT_OFFICER("Account Officer"),
    ;

    companion object {
        fun fromTitle(title: String): OfficePost? = values().find { it.title.equals(title, ignoreCase = true) }

        fun getAllTitles(): List<String> = values().map { it.title }
    }
}
