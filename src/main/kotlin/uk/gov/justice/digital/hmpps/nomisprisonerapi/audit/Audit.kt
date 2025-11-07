package uk.gov.justice.digital.hmpps.nomisprisonerapi.audit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Audit(val auditModule: String = DEFAULT_AUDIT_MODULE)

const val DEFAULT_AUDIT_MODULE = "DPS_SYNCHRONISATION"
