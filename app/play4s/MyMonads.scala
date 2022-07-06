package play4s

import cats.data.{EitherT, Kleisli}
import play.api.http.Writeable
import play.api.libs.json.{Format, JsValue, Writes}
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}

trait MyErrorType

object MyMonads {


  type MyMonad[O] = EitherT[Future, ErrorResult, (O, Format[O])]

  case class MyEndpoint()(implicit ec: ExecutionContext) {

    def outF[O](value: O)(implicit format: Format[O]): MyMonad[O] =
      EitherT.right[ErrorResult] {
        Future((value, format))
      }


  }

  case class NotFound(
      message: String = "BadRequest | Entity or request malformed",
      additionalInfoToLog: Option[String] = None,
      additionalInfoErrorCode: Option[String] = None,
      statusCode: Int = 404
  ) extends ErrorResult()

  abstract class ErrorResult() {
    def message: String
    def additionalInfoToLog: Option[String]
    def additionalInfoErrorCode: Option[String]
    def statusCode: Int
  }

}
