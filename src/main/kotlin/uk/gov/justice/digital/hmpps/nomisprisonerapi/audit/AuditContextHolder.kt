package uk.gov.justice.digital.hmpps.nomisprisonerapi.audit

object AuditContextHolder {
  private val threadLocal = ThreadLocal<String>()

  fun set(module: String) = threadLocal.set(module)
  fun get(): String = threadLocal.get() ?: DEFAULT_AUDIT_MODULE
  fun clear() = threadLocal.remove()
}
