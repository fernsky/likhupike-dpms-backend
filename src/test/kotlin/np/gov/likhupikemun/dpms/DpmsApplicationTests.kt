package np.gov.likhupikemun.dpms

import np.gov.likhupikemun.dpms.config.TestContainersConfig
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@Import(TestContainersConfig::class)
@ActiveProfiles("test")
class DpmsApplicationTests
