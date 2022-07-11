package controllers

import cats.data.{EitherT, Validated}
import play.api.libs.json.{JsObject, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import play4s.MyErrorType
import play4s.MyMonads.{MyEndpoint, MyMonad}
import playSmithy._
import smithy4s.{ByteArray, Document, Timestamp}

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.nio.file.{Files, Path}
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PizzaController @Inject(
) (implicit cc: ControllerComponents, ec: ExecutionContext)
    extends PizzaAdminService[MyMonad] {

  val endpoint = MyEndpoint()


  var pizzaList = Seq.empty[MenuItem]



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
    for {
      _ <- EitherT(
        Future(
          Validated.cond(pizzaList.exists(p => p.id.get == id), (), play4s.BadRequest("No Pizza with this ID")).toEither
        )
      )
      pizza <- EitherT.right[MyErrorType](Future(pizzaList.find(p => p.id.get == id).get))
    } yield GetMenuResult(pizza)

  override def version(body: ByteArray): MyMonad[VersionOutput] = {
    val bos: BufferedOutputStream = new BufferedOutputStream(new FileOutputStream(s"/tmp/${UUID.randomUUID().toString}"))
    bos.write(body.array)
    bos.close()
    endpoint.outF(VersionOutput(Document.obj(
      "test" -> Document.fromString("test")
    )))
  }

  override def getAll(): MyMonad[GetAllRequest] = endpoint.outF(GetAllRequest(pizzaList.toList))
}
