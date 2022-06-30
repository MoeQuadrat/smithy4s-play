package api

import controllers.{HomeController, PizzaController}

import javax.inject.{Inject, Singleton}
import play.api.mvc._
import play.api.routing.Router.Routes
import play4s.BaseRouter

import scala.concurrent.ExecutionContext

@Singleton
class ApiRouter @Inject() (
    homeController: HomeController,
    pizzaController: PizzaController
)(implicit
    cc: ControllerComponents,
    ec: ExecutionContext
) extends BaseRouter {
  override val controllers: Seq[Routes] = Seq(homeController, pizzaController)
}
