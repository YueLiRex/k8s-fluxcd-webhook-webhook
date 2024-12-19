package com.github.k8s.api.v1

import dev.hnaderi.k8s.utils.{Builder, Decoder, Encoder, ObjectReader, ObjectWriter, Reader}
import io.k8s.api.authentication.v1.UserInfo
import io.k8s.api.storagemigration.v1alpha1.GroupVersionResource
import io.k8s.apimachinery.pkg.runtime.RawExtension

case class AdmissionRequest(
                             kind: Option[GroupVersionKind],
                             name: Option[String],
                             namespace: Option[String],
                             `object`: Option[RawExtension],
                             oldObject: Option[RawExtension],
                             operation: Option[String],
                             options: Option[RawExtension],
                             requestKind: Option[GroupVersionKind],
                             requestResource: Option[GroupVersionResource],
                             requestSubResource: Option[String],
                             subResource: Option[String],
                             uid: Option[String],
                             userInfo: Option[UserInfo],
                             dryRun: Option[Boolean] = Some(false),
                           )

object AdmissionRequest {
  implicit val admissionRequestEncoder: Encoder[AdmissionRequest] = new Encoder[AdmissionRequest] {
    override def apply[T: Builder](admissionRequest: AdmissionRequest): T = {
      val obj = ObjectWriter[T]()
      obj.write("uid", admissionRequest.uid)
        .write("kind", admissionRequest.kind)
        .write("name", admissionRequest.name)
        .write("namespace", admissionRequest.namespace)
        .write("object", admissionRequest.`object`)
        .write("oldObject", admissionRequest.oldObject)
        .write("operation", admissionRequest.operation)
        .write("options", admissionRequest.options)
        .write("requestKind", admissionRequest.requestKind)
        .write("requestResource", admissionRequest.requestResource)
        .write("requestSubResource", admissionRequest.requestSubResource)
        .write("subResource", admissionRequest.subResource)
        .write("userInfo", admissionRequest.userInfo)
        .write("dryRun", admissionRequest.dryRun)
        .build
    }
  }

  implicit val admissionRequestDecoder: Decoder[AdmissionRequest] = new Decoder[AdmissionRequest] {
    override def apply[T: Reader](t: T): Either[String, AdmissionRequest] = for {
      obj <- ObjectReader(t)
      kind <- obj.readOpt[GroupVersionKind]("kind")
      name <- obj.readOpt[String]("name")
      namespace <- obj.readOpt[String]("namespace")
      objt <- obj.readOpt[RawExtension]("object")
      oldobjt <- obj.readOpt[RawExtension]("oldObject")
      operation <- obj.readOpt[String]("operation")
      options <- obj.readOpt[RawExtension]("options")
      requestKind <- obj.readOpt[GroupVersionKind]("requestKind")
      requestResource <- obj.readOpt[GroupVersionResource]("requestResource")
      requestSubResource <- obj.readOpt[String]("requestSubResource")
      subResource <- obj.readOpt[String]("subResource")
      uid <- obj.readOpt[String]("uid")
      userInfo <- obj.readOpt[UserInfo]("userInfo")
      dryRun <- obj.readOpt[Boolean]("dryRun")
    } yield AdmissionRequest(
      kind = kind,
      name = name,
      namespace = namespace,
      `object` = objt,
      oldObject = oldobjt,
      operation = operation,
      options = options,
      requestKind = requestKind,
      requestResource = requestResource,
      requestSubResource = requestSubResource,
      subResource = subResource,
      uid = uid,
      userInfo = userInfo,
      dryRun = dryRun
    )
  }
}
