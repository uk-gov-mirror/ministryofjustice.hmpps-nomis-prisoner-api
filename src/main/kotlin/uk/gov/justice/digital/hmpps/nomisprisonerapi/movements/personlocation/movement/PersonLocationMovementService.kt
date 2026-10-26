package uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation.movement

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PersonLocationMovementService {
  fun getPersonLocationMovement(offenderNo: String, bookingId: Long, sequence: Int): PersonLocationMovementDetail = TODO("Implemented in the next PR")
}
