package controllers

import cats.data.EitherT
import play.api.http.Writeable
import play.api.libs.json
import play.api.libs.json.Json
import play.api.mvc.{Action, _}
import play4s.MyMonads.{MyEndpoint, MyMonad}
import playSmithy._
import play4s.{MyErrorType, SmithyPlayRouter}
import smithy4s.GenLift

import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HomeController @Inject(
) (implicit cc: ControllerComponents, ec: ExecutionContext)
    extends AbstractController(cc)
    with HomeControllerService[MyMonad] {

  implicit val format = Json.format[Hi]

  /*def index(): Action[AnyContent] = Action[AnyContent] {
    Ok(Json.toJson(Hi(Some("")))).body
  }*/

  val endpoint = MyEndpoint()

  override def index(): MyMonad[Hi] = MyEndpoint().out(Hi(Some("ASD")))

  override def index1(): MyMonad[Hi] = MyEndpoint().out(Hi(Some("ASD1")))

  override def index2(): MyMonad[Hi] = MyEndpoint().out(Hi(Some("ASD2")))

  override def indexPost(test: String, bye: Bye): MyMonad[Hi] = MyEndpoint().out(Hi(Some(bye.toString + " " + test)))
}

