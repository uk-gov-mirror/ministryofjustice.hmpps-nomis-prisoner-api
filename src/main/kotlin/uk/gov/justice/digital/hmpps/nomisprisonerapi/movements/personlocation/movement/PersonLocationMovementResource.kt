package uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation.movement

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.nomisprisonerapi.config.ErrorResponse

@PreAuthorize("hasRole('ROLE_NOMIS_PRISONER_API__SYNCHRONISATION__RW')")
@RestController
@Validated
@RequestMapping("/movements/{offenderNo}/person-locations/movement", produces = [MediaType.APPLICATION_JSON_VALUE])
class PersonLocationMovementResource(
  private val service: PersonLocationMovementService,
) {
  @GetMapping("/{bookingId}/{movementSeq}")
  @Operation(
    summary = "Get a single movement for an offender",
    description = "Get a single person location movement for an offender by booking ID and movement sequence. Requires role NOMIS_PRISONER_API__SYNCHRONISATION__RW",
    responses = [
      ApiResponse(
        responseCode = "200",
        description = "Movement returned",
      ),
      ApiResponse(
        responseCode = "401",
        description = "Unauthorized to access this endpoint",
        content = [
          Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class)),
        ],
      ),
      ApiResponse(
        responseCode = "403",
        description = "Forbidden, requires role NOMIS_PRISONER_API__SYNCHRONISATION__RW",
        content = [
          Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class)),
        ],
      ),
      ApiResponse(
        responseCode = "404",
        description = "Offender, booking or movement not found, or the booking does not belong to the offender",
        content = [
          Content(mediaType = "application/json", schema = Schema(implementation = ErrorResponse::class)),
        ],
      ),
    ],
  )
  fun getPersonLocationMovement(
    @Schema(description = "Offender number (NOMS ID)", example = "A1234BC") @PathVariable offenderNo: String,
    @Schema(description = "Booking ID", example = "123") @PathVariable bookingId: Long,
    @Schema(description = "Movement Sequence", example = "1") @PathVariable movementSeq: Int,
  ): PersonLocationMovementDetail = service.getPersonLocationMovement(offenderNo, bookingId, movementSeq)
}
