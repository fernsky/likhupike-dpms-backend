package np.gov.likhupikemun.dpms.family.api.controller

import jakarta.validation.Valid
import np.gov.likhupikemun.dpms.family.api.dto.request.CreateFamilyRequest
import np.gov.likhupikemun.dpms.family.api.dto.request.FamilySearchCriteria
import np.gov.likhupikemun.dpms.family.api.dto.request.UpdateFamilyRequest
import np.gov.likhupikemun.dpms.family.api.dto.response.FamilyResponse
import np.gov.likhupikemun.dpms.family.service.FamilyService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/families")
class FamilyController(
    private val familyService: FamilyService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createFamily(
        @Valid @RequestBody request: CreateFamilyRequest,
    ): FamilyResponse = familyService.createFamily(request)

    @PutMapping("/{id}")
    fun updateFamily(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateFamilyRequest,
    ): FamilyResponse = familyService.updateFamily(id, request)

    @GetMapping("/{id}")
    fun getFamily(
        @PathVariable id: UUID,
    ): FamilyResponse = familyService.getFamily(id)

    @GetMapping
    fun searchFamilies(criteria: FamilySearchCriteria): Page<FamilyResponse> = familyService.searchFamilies(criteria)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteFamily(
        @PathVariable id: UUID,
    ) = familyService.deleteFamily(id)
}
