package np.gov.mofaga.imis.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration

@Configuration
@EntityScan(basePackages = ["np.gov.mofaga.imis"])
class EntityScanConfig
