package com.github.k8s.api.v1

import dev.hnaderi.k8s.utils.{Builder, Decoder, Encoder, ObjectReader, ObjectWriter, Reader}

case class GroupVersionKind(group: Option[String], version: Option[String], kind: Option[String])

object GroupVersionKind {
  implicit val groupVersionKindEncoder: Encoder[GroupVersionKind] = new Encoder[GroupVersionKind] {
    override def apply[T: Builder](groupVersionKind: GroupVersionKind): T = {
      val obj = ObjectWriter[T]()
      obj
        .write("group", groupVersionKind.group)
        .write("version", groupVersionKind.version)
        .write("kind", groupVersionKind.kind)
        .build
    }
  }

  implicit val groupVersionKindDecoder: Decoder[GroupVersionKind] = new Decoder[GroupVersionKind] {
    override def apply[T: Reader](t: T): Either[String, GroupVersionKind] = for {
      obj <- ObjectReader(t)
      group <- obj.readOpt[String]("group")
      version <- obj.readOpt[String]("version")
      kind <- obj.readOpt[String]("kind")
    } yield GroupVersionKind(group, version, kind)
  }
}
