package com.github.k8s.api.v1

import dev.hnaderi.k8s.utils.{Builder, Decoder, Encoder, ObjectReader, ObjectWriter, Reader}

case class AdmissionReview(
                            apiVersion: String = "admission.k8s.io/v1",
                            kind: String = "AdmissionReview",
                            request: Option[AdmissionRequest],
                            response: Option[AdmissionResponse]
                          )

object AdmissionReview {
  implicit val admissionReviewEncoder: Encoder[AdmissionReview] = new Encoder[AdmissionReview] {
    override def apply[T: Builder](admissionReview: AdmissionReview): T = {
      val obj = ObjectWriter[T]()
      obj
        .write("apiVersion", admissionReview.apiVersion)
        .write("kind", admissionReview.kind)
        .write("request", admissionReview.request)
        .write("response", admissionReview.response)
        .build
    }
  }

  implicit val admissionReviewDecoder: Decoder[AdmissionReview] = new Decoder[AdmissionReview] {
    override def apply[T: Reader](t: T): Either[String, AdmissionReview] = for {
      obj <- ObjectReader(t)
      request <- obj.readOpt[AdmissionRequest]("request")
      response <- obj.readOpt[AdmissionResponse]("response")
    } yield AdmissionReview (
      request = request,
      response = response
    )
  }
}