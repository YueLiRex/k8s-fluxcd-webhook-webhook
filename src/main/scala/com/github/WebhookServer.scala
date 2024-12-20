package com.github

import com.github.k8s.webhook.routes.WebhookRoutes
import org.apache.pekko
import pekko.actor.typed.ActorSystem
import pekko.actor.typed.scaladsl.Behaviors
import pekko.http.scaladsl.Http
import pekko.http.scaladsl.server.Route

import scala.util.Failure
import scala.util.Success

//#main-class
object WebhookServer {
  //#start-http-server
  private def startHttpServer(routes: Route)(implicit system: ActorSystem[_]): Unit = {
    // Pekko HTTP still needs a classic ActorSystem to start
    import system.executionContext

    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
  //#start-http-server
  def main(args: Array[String]): Unit = {
    //#server-bootstrapping
    val rootBehavior = Behaviors.setup[Nothing] { context =>

      val webhookRoutes = new WebhookRoutes()(context.system)
      startHttpServer(webhookRoutes.routes)(context.system)

      Behaviors.empty
    }
    val _ = ActorSystem[Nothing](rootBehavior, "HelloPekkoHttpServer")
  }
}
//#main-class
