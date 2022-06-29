package api

import controllers._

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._
import play4s.SmithyPlayRouter

import scala.concurrent.ExecutionContext

class ApiRouter @Inject() (customRouter: CustomRouter) extends SimpleRouter {
  override def routes: Routes = {
    println("[ApiRouter]")
    customRouter.routes
  }
}

@Singleton
class CustomRouter @Inject() (homeController: HomeController)(implicit
    cc: ControllerComponents,
    ec: ExecutionContext
) {
  val routes = {
    println("[RoutingDing]")
    val routes = new SmithyPlayRouter(homeController).routes()
    println("[RoutingDing]2")
    routes
  }

}
