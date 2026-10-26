package uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation.movement

import io.swagger.v3.oas.annotations.media.Schema
import uk.gov.justice.digital.hmpps.nomisprisonerapi.data.CodeDescription
import uk.gov.justice.digital.hmpps.nomisprisonerapi.helpers.NomisAudit
import uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation.offender.MovementDirection
import uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation.offender.MovementLocation
import uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation.offender.MovementType
import java.time.LocalDateTime

@Schema(description = "A single person location movement")
data class PersonLocationMovementDetail(
  @Schema(description = "Booking ID")
  val bookingId: Long,

  @Schema(description = "Movement sequence")
  val sequence: Int,

  @Schema(description = "Movement type")
  val movementType: MovementType,

  @Schema(description = "The direction of the movement")
  val directionCode: MovementDirection,

  @Schema(description = "Movement time")
  val movementTime: LocalDateTime,

  @Schema(description = "Movement reason code")
  val movementReason: CodeDescription,

  @Schema(description = "From location")
  val from: MovementLocation?,

  @Schema(description = "To location")
  val to: MovementLocation?,

  @Schema(description = "Schedule ID")
  val scheduleId: Long?,

  @Schema(description = "Comment text")
  val commentText: String?,

  @Schema(description = "Audit data associated with the records")
  val audit: NomisAudit,
)
