package play4s

import cats.data.{EitherT, Kleisli}
import play.api.http.Writeable
import play.api.libs.json.{Format, JsValue, Writes}
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}




case class BadRequest(
                       message: String = "Entity or request malformed",
                       additionalInfoToLog: Option[String] = None,
                       additionalInfoErrorCode: Option[String] = None,
                       statusCode: Int = 400
                     ) extends MyErrorType

trait MyErrorType  {
  def message: String
  def additionalInfoToLog: Option[String]
  def additionalInfoErrorCode: Option[String]
  def statusCode: Int
}

object MyMonads {

  type MyMonad[O] = EitherT[Future, MyErrorType, O]

  case class MyEndpoint()(implicit  ec: ExecutionContext) {
    /*def out[O](f: Future[(O, Format[O])])(implicit format: Format[O]): MyMonad[O] = EitherT.right[MyErrorType] {
      f
    }*/

    def outF[O](value: O)(implicit format: Format[O]): MyMonad[O] = EitherT.right[MyErrorType] {
      Future(value)
    }

    /*def outF[O](value: () => O)(implicit format: Format[O]): MyMonad[O] = EitherT.right[MyErrorType] {
      Future((value(), format))
    }*/

  }

}
