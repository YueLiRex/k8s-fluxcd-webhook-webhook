package com.github.k8s.webhook.routes

import com.github.k8s.api.v1.AdmissionResponse.Patch
import com.github.k8s.api.v1.{AdmissionResponse, AdmissionReview}
import com.github.pjfanning.pekkohttpcirce.FailFastCirceSupport
import io.k8s.api.apps.v1.Deployment
import org.apache.pekko
import pekko.http.scaladsl.server.Directives._
import pekko.http.scaladsl.model.StatusCodes
import pekko.http.scaladsl.server.Route
import pekko.actor.typed.ActorSystem
import dev.hnaderi.k8s.circe._
import io.k8s.api.core.v1.ResourceRequirements
import io.k8s.apimachinery.pkg.api.resource.Quantity

class WebhookRoutes()(implicit val system: ActorSystem[_]) extends FailFastCirceSupport {

  val routes: Route =
    pathPrefix("webhook") {
      path("default-value") {
        post {
          entity(as[AdmissionReview]) { admissionReview =>
            system.log.info("received request")
            system.log.info(admissionReview.request.flatMap(_.uid).getOrElse(""))
            system.log.info("====")

            val patch = Patch(
              op = "add", path = "/spec/replicas", value = 3
            )

            val response = AdmissionReview(request = None, response = Some(AdmissionResponse(
              uid = admissionReview.request.flatMap(_.uid),
              allowed = Some(true),
              status = None,
              patch = Some(patch.toBase64String),
              patchType = Some("JSONPatch"),
              auditAnnotations = None,
              warnings = None
            )))
            complete(StatusCodes.OK, response)
          }
        }
      }
    }
}
