package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import play4s.MyMonads.{MyEndpoint, MyMonad}
import playSmithy._
import smithy4s.Timestamp

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class PizzaController @Inject(
) (implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc)
    with PizzaAdminService[MyMonad] {

  val endpoint = MyEndpoint()

  implicit val format = Json.format[Pizza]
  implicit val formatMenuItem = Json.format[MenuItem]
  implicit val formatMenuResult = Json.format[GetMenuResult]
  implicit val formatVersionOutput = Json.format[VersionOutput]
  implicit val formatHealthResponse = Json.format[HealthResponse]

  var pizzaList = Seq.empty[MenuItem]

  override def version(): MyMonad[VersionOutput] =
    endpoint.outF(VersionOutput("1.0"))

  override def health(query: Option[String]): MyMonad[HealthResponse] =
    endpoint.outF(HealthResponse("I'm alive"))

  override def addMenuItem(menuItem: MenuItem): MyMonad[MenuItem] = {
    endpoint.outF({
      val newItem = menuItem.copy(
        id = Some(UUID.randomUUID().toString),
        added = Some(LocalDateTime.now().toString)
      )
      pizzaList = pizzaList :+ newItem
      newItem
    })
  }

  override def getMenu(id: String): MyMonad[GetMenuResult] =
    endpoint.outF(
      {
        println(pizzaList)
        println(id)
        GetMenuResult(pizzaList.filter(m => m.id.get == id).head)
      }
    )
}
