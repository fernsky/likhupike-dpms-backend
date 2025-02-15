package np.gov.likhupikemun.dpms.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@EntityScan(basePackages = ["np.gov.likhupikemun.dpms"])
class EntityScanConfig
