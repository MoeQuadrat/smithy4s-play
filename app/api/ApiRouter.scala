package api

import controllers._

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.api.routing.SimpleRouter
import play4s.SmithyPlayRouter

import scala.concurrent.ExecutionContext

class ApiRouter @Inject() (customRouter: CustomRouter) extends SimpleRouter {
  override def routes = {
    println("[ApiRouter]")
    customRouter.routes
  }
}

@Singleton
class CustomRouter @Inject() (homeController: HomeController, pizzaController: PizzaController)(implicit
    cc: ControllerComponents,
    ec: ExecutionContext
) {
  val routes = new SmithyPlayRouter(pizzaController).routes()

}
