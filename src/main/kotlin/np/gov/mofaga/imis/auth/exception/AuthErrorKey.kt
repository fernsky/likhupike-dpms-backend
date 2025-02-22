package np.gov.mofaga.imis.auth.exception

enum class AuthErrorKey(
    val key: String,
) {
    // Authentication
    EMAIL_EXISTS("auth.error.email.exists"),
    INVALID_CREDENTIALS("auth.error.invalid.credentials"),
    USER_NOT_FOUND("auth.error.user.not.found"),
    USER_NOT_APPROVED("auth.error.user.not.approved"),
    TOKEN_EXPIRED("auth.error.token.expired"),
    INVALID_TOKEN("auth.error.token.invalid"),
    PASSWORD_RESET_TOKEN("auth.error.password.reset.token"),
    PASSWORD_RESET_LIMIT("auth.error.password.reset.limit"),
    INVALID_OFFICE_POST_WARD_COMBINATION("auth.error.office.post.ward.combination"),

    // User Management
    USER_APPROVAL("user.error.approval"),
    WARD_USER_CREATION("user.error.ward.user.creation"),
    WARD_ASSIGNMENT("user.error.ward.assignment"),
    ROLE_ASSIGNMENT("user.error.role.assignment"),
    USER_DEACTIVATION("user.error.deactivation"),
    OFFICE_POST("user.error.office.post"),
    PROFILE_UPDATE("user.error.profile.update"),
    PROFILE_PICTURE("user.error.profile.picture"),
    USER_DELETION("user.error.deletion"),

    // Search
    SEARCH_CRITERIA("search.error.criteria"),
    SORT_FIELD("search.error.sort.field"),
    DATE_RANGE("search.error.date.range"),
}
