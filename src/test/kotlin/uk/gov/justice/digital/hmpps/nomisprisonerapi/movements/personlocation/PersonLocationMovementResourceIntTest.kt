package uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.nomisprisonerapi.integration.IntegrationTestBase

class PersonLocationMovementResourceIntTest : IntegrationTestBase() {

  @Nested
  @DisplayName("GET /movements/{offenderNo}/person-locations/movement/{bookingId}/{movementSeq}")
  inner class GetPersonLocationMovement {

    @Nested
    inner class Security {
      @Test
      fun `should return unauthorised for missing token`() {
        webTestClient.get()
          .uri("/movements/A1234BC/person-locations/movement/12345/1")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `should return forbidden for missing role`() {
        webTestClient.get()
          .uri("/movements/A1234BC/person-locations/movement/12345/1")
          .headers(setAuthorisation())
          .exchange()
          .expectStatus().isForbidden
      }

      @Test
      fun `should return forbidden for wrong role`() {
        webTestClient.get()
          .uri("/movements/A1234BC/person-locations/movement/12345/1")
          .headers(setAuthorisation("ROLE_INVALID"))
          .exchange()
          .expectStatus().isForbidden
      }
    }
  }
}
