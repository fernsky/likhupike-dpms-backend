package np.gov.mofaga.imis.test

import np.gov.mofaga.imis.auth.domain.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import javax.sql.DataSource

fun loginAs(user: User) {
    val authentication = UsernamePasswordAuthenticationToken(user, null, user.authorities)
    SecurityContextHolder.getContext().authentication = authentication
}

fun clearDatabase(dataSource: DataSource) {
    val connection = dataSource.connection
    val sql = """
        DELETE FROM municipalities;
        DELETE FROM districts;
        DELETE FROM provinces;
    """.trimIndent()
    connection.createStatement().execute(sql)
    connection.close()
}
