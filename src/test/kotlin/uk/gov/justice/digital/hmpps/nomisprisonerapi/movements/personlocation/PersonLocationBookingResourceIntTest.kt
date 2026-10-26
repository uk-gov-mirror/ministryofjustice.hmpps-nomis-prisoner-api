package uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.within
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.test.web.reactive.server.WebTestClient
import uk.gov.justice.digital.hmpps.nomisprisonerapi.helpers.roundToNearestSecond
import uk.gov.justice.digital.hmpps.nomisprisonerapi.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.nomisprisonerapi.integration.expectBodyResponse
import uk.gov.justice.digital.hmpps.nomisprisonerapi.jpa.Offender
import uk.gov.justice.digital.hmpps.nomisprisonerapi.jpa.OffenderBooking
import uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation.booking.PersonLocationBooking
import uk.gov.justice.digital.hmpps.nomisprisonerapi.movements.personlocation.offender.BookingStatus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PersonLocationBookingResourceIntTest : IntegrationTestBase() {

  private val offenderNo = "B7463BB"
  private val now = LocalDateTime.now().roundToNearestSecond()
  private lateinit var offender: Offender
  private lateinit var booking: OffenderBooking
  private lateinit var oldBooking: OffenderBooking

  @AfterEach
  fun tearDown() {
    repository.deleteOffenders()
  }

  @Nested
  @DisplayName("GET /movements/{offenderNo}/person-locations/booking/{bookingId}")
  inner class GetPersonLocationBooking {

    @BeforeEach
    fun setUp() {
      nomisDataBuilder.build {
        offender = offender(nomsId = offenderNo) {
          booking = booking(bookingBeginDate = now.minusDays(2))
          oldBooking = booking(bookingBeginDate = now.minusDays(10)) {
            release(date = now.minusDays(5))
          }
        }
      }
    }

    @Nested
    inner class HappyPath {
      @Test
      fun `should get booking details`() {
        webTestClient.getPersonLocationBookingOk(bookingId = booking.bookingId).apply {
          assertThat(bookingId).isEqualTo(booking.bookingId)
          assertThat(bookingNumber).isEqualTo(booking.bookNumber)
          assertThat(activeBooking).isTrue
          assertThat(latestBooking).isTrue
          assertThat(bookingStatus).isEqualTo(BookingStatus.OPEN)
          assertThat(bookingBeginTime).isEqualTo(now.minusDays(2))
          assertThat(bookingEndTime).isNull()
          assertThat(audit.createUsername).isEqualTo("SA")
          assertThat(audit.createDatetime).isCloseTo(now, within(50, ChronoUnit.MINUTES))
        }
      }

      @Test
      fun `should get a previous booking`() {
        webTestClient.getPersonLocationBookingOk(bookingId = oldBooking.bookingId).apply {
          assertThat(bookingId).isEqualTo(oldBooking.bookingId)
          assertThat(activeBooking).isFalse
          assertThat(latestBooking).isFalse
          assertThat(bookingBeginTime).isEqualTo(now.minusDays(10))
          assertThat(bookingEndTime).isEqualTo(now.minusDays(5).truncatedTo(ChronoUnit.DAYS))
        }
      }
    }

    @Nested
    inner class Validation {
      @Test
      fun `should return not found for unknown offender`() {
        webTestClient.getPersonLocationBooking(offenderNo = "A9999ZZ")
          .expectStatus().isNotFound
      }

      @Test
      fun `should return not found for unknown booking`() {
        webTestClient.getPersonLocationBooking(bookingId = 999999)
          .expectStatus().isNotFound
      }

      @Test
      fun `should return not found for a booking belonging to another offender`() {
        lateinit var otherBooking: OffenderBooking
        nomisDataBuilder.build {
          offender(nomsId = "A1234TT") {
            otherBooking = booking()
          }
        }
        webTestClient.getPersonLocationBooking(bookingId = otherBooking.bookingId)
          .expectStatus().isNotFound
      }
    }

    @Nested
    inner class Security {
      @Test
      fun `should return unauthorised for missing token`() {
        webTestClient.get()
          .uri("/movements/A1234BC/person-locations/booking/12345")
          .exchange()
          .expectStatus().isUnauthorized
      }

      @Test
      fun `should return forbidden for missing role`() {
        webTestClient.get()
          .uri("/movements/A1234BC/person-locations/booking/12345")
          .headers(setAuthorisation())
          .exchange()
          .expectStatus().isForbidden
      }

      @Test
      fun `should return forbidden for wrong role`() {
        webTestClient.get()
          .uri("/movements/A1234BC/person-locations/booking/12345")
          .headers(setAuthorisation("ROLE_INVALID"))
          .exchange()
          .expectStatus().isForbidden
      }
    }
  }

  private fun WebTestClient.getPersonLocationBooking(offenderNo: String = this@PersonLocationBookingResourceIntTest.offenderNo, bookingId: Long = booking.bookingId) = get()
    .uri("/movements/$offenderNo/person-locations/booking/$bookingId")
    .headers(setAuthorisation(roles = listOf("ROLE_NOMIS_PRISONER_API__SYNCHRONISATION__RW")))
    .exchange()

  private fun WebTestClient.getPersonLocationBookingOk(bookingId: Long) = getPersonLocationBooking(bookingId = bookingId)
    .expectStatus().isOk
    .expectBodyResponse<PersonLocationBooking>()
}
