package uk.gov.justice.digital.hmpps.nomisprisonerapi.audit

import org.springframework.stereotype.Service
import java.sql.Connection

@Service
class AuditModuleService(
  private val handler: AuditConnectionHandler,
) {
  fun applyAuditModule(conn: Connection, module: String) {
    // TODO check if can  just getting module from AuditContextHolder.get()
    handler.applyAuditModule(conn, module)
  }
}
