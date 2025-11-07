package uk.gov.justice.digital.hmpps.nomisprisonerapi.audit

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class AuditAnnotationAspect {

  @Around("@annotation(audit)")
  fun aroundAudit(joinPoint: ProceedingJoinPoint, audit: Audit): Any? {
    try {
      AuditContextHolder.set(audit.auditModule)
      return joinPoint.proceed()
    } finally {
      AuditContextHolder.clear()
    }
  }
}
