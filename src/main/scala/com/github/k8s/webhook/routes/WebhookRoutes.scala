package com.github.k8s.webhook.routes

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
      path("defaultValue") {
        post {
          entity(as[Deployment]) { deployment =>
            val updatedDeployment = for {
              dSpec <- deployment.spec
              pTemplate <- deployment.spec.map(_.template)
              containers <- deployment.spec.flatMap(_.template.spec.map(_.containers))
            } yield {
              val updatedCt = containers.map { c =>
                c.copy(resources = Some(ResourceRequirements(
                  requests = Some(Map("cpu" -> Quantity("500m"), "memory" -> Quantity("1024Mi"))),
                  limits = Some(Map("cpu" -> Quantity("500m"), "memory" -> Quantity("1024Mi")))
                )))
              }

              println(updatedCt)

              val updatedPtmp = pTemplate.copy(spec = pTemplate.spec.map(_.copy(containers = updatedCt)))
              val updatedDp = dSpec.copy(template = updatedPtmp)

              updatedDp
            }

            complete(StatusCodes.OK, updatedDeployment)
          }
        }
      }
    }
}
