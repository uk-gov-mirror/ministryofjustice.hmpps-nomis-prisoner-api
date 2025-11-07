package uk.gov.justice.digital.hmpps.nomisprisonerapi.audit

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.sql.Connection

interface AuditConnectionHandler {
  fun applyAuditModule(conn: Connection, module: String)
}

@Component
@Profile("!oracle")
class H2AuditConnectionHandler : AuditConnectionHandler {
  override fun applyAuditModule(conn: Connection, module: String) {
    println("H2AuditConnectionHandler: applied audit module '$module'")
  }
}

@Component
@Profile("oracle")
class OracleAuditConnectionHandler : AuditConnectionHandler {

  override fun applyAuditModule(conn: Connection, module: String) {
    conn.prepareCall("{call OMS_OWNER.nomis_context.set_context(?, ?)}").use { stmt ->
      stmt.setString(1, "app.audit_module")
      stmt.setString(2, module)
      stmt.execute()
    }
  }
}
