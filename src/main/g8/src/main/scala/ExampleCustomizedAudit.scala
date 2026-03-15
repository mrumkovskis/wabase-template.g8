package $package_name$

import org.apache.pekko.util.ByteString
import org.wabase.CborOrJsonAnyValueDecoder

class ExampleCustomizedAudit extends org.wabase.audit.Audit {
  override protected def auditRecordToMapForSaveToDatabase(record: ByteString): Map[String, Any] = {
    val decoded       = decodeAuditRecord(record)
    val decodedAsMap  = CborOrJsonAnyValueDecoder.decodeToMap(record)
    decodedAsMap ++ Map(
      "request_method" -> decoded.request.method,   // Customized audit column example
    )
  }
}
