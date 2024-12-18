package com.github

//#user-routes-spec
//#test-top
import com.github.k8s.webhook.routes.WebhookRoutes
import io.k8s.api.apps.v1.{Deployment, DeploymentSpec}
import io.k8s.api.core.v1.{Container, PodSpec, PodTemplateSpec}
import io.k8s.apimachinery.pkg.api.resource.Quantity
import io.k8s.apimachinery.pkg.apis.meta.v1.LabelSelector
import org.apache.pekko
import pekko.actor.testkit.typed.scaladsl.ActorTestKit
import pekko.actor.typed.ActorSystem
import pekko.http.scaladsl.marshalling.Marshal
import pekko.http.scaladsl.model._
import pekko.http.scaladsl.testkit.ScalatestRouteTest
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class WebhookRoutesSpec extends AnyWordSpec with Matchers with ScalaFutures with ScalatestRouteTest {
  lazy val testKit = ActorTestKit()

  implicit val typedSystem: ActorSystem[_] = testKit.system
  implicit val ctx = typedSystem.executionContext

  override def createActorSystem(): pekko.actor.ActorSystem = testKit.system.classicSystem
  lazy val webhookRoutes = new WebhookRoutes().routes

  "WebhookRoutes" should {
//    "return Ok Get(/webhook/defaultValue)" in {
//      val request = HttpRequest(method = HttpMethods.GET, uri = "/webhook/defaultValue")
//
//      request ~> webhookRoutes ~> check {
//        status should ===(StatusCodes.OK)
//      }
//    }

    "return Ok POST(/webhook/defaultValue)" in {
      import com.github.pjfanning.pekkohttpcirce.FailFastCirceSupport._
      import dev.hnaderi.k8s.circe._

      val deployment = Deployment(spec = Some(DeploymentSpec(selector = LabelSelector(), template = PodTemplateSpec(spec = Some(PodSpec(containers = Seq(Container(name = "c1", image = Some("nginx")))))))))
      val deploymentEntity = Marshal(deployment).to[MessageEntity].futureValue

      val request = Post("/webhook/defaultValue").withEntity(deploymentEntity)

      request ~> webhookRoutes ~> check {
        status should === (StatusCodes.OK)
        val deploymentWithDefaultValue = entityAs[Deployment]

        val updatedResource = deploymentWithDefaultValue.spec.toSeq.flatMap(_.template.spec.toSeq.flatMap(_.containers.flatMap(_.resources)))

        updatedResource.foreach { resource =>
          resource.requests should be(Some(Map("cpu" -> Quantity("500m"), "memory" -> Quantity("1024Mi"))))
          resource.limits should be(Some(Map("cpu" -> Quantity("500m"), "memory" -> Quantity("1024Mi"))))
        }
      }
    }
  }
}
