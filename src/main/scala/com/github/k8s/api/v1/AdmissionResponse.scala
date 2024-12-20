package com.github.k8s.api.v1

import com.github.k8s.api.v1.AdmissionResponse.Base64String
import dev.hnaderi.k8s.utils.{Builder, Decoder, Encoder, ObjectReader, ObjectWriter, Reader}
import io.k8s.apimachinery.pkg.apis.meta.v1.Status

import java.util.Base64

case class AdmissionResponse(
                              uid: Option[String],
                              allowed: Option[Boolean],
                              status: Option[Status],
                              patch: Option[Base64String],
                              patchType: Option[String],
                              auditAnnotations: Option[Map[String, String]],
                              warnings: Option[Seq[String]]
                            )

object AdmissionResponse {
  type Base64String = String

  implicit val admissionResponseEncoder: Encoder[AdmissionResponse] = new Encoder[AdmissionResponse] {
    override def apply[T: Builder](admissionResponse: AdmissionResponse): T = {
      val obj = ObjectWriter[T]()
      obj.write("uid", admissionResponse.uid)
        .write("allowed", admissionResponse.allowed)
        .write("status", admissionResponse.status)
        .write("patch", admissionResponse.patch)
        .write("patchType", admissionResponse.patchType)
        .write("auditAnnotations", admissionResponse.auditAnnotations)
        .write("warnings", admissionResponse.warnings)
        .build
    }
  }

  implicit val admissionResponseDecoder: Decoder[AdmissionResponse] = new Decoder[AdmissionResponse] {
    override def apply[T: Reader](t: T): Either[String, AdmissionResponse] = for {
      obj <- ObjectReader(t)
      uid <- obj.readOpt[String]("uid")
      allowed <- obj.readOpt[Boolean]("allowed")
      status <- obj.readOpt[Status]("status")
      patch <- obj.readOpt[Base64String]("patch")
      patchType <- obj.readOpt[String]("patchType")
      auditAnnotations <- obj.readOpt[Map[String, String]]("auditAnnotations")
      warnings <- obj.readOpt[Seq[String]]("warnings")
    } yield AdmissionResponse(
      uid = uid,
      allowed = allowed,
      status = status,
      patch = patch,
      patchType = patchType,
      auditAnnotations = auditAnnotations,
      warnings = warnings
    )
  }

  case class Patch(op: String, path: String, value: AnyVal) {
    def toBase64String: Base64String = {
      val str =
        s"""
          |[{"op": "$op", "path": "$path", "value": "$value"}]
          |""".stripMargin

      println(str)

      Base64.getEncoder.encodeToString(str.getBytes)
    }
  }
}
