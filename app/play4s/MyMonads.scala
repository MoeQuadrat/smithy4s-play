package play4s

import cats.data.{EitherT, Kleisli}
import play.api.http.Writeable
import play.api.libs.json.{Format, JsValue, Writes}
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}



trait MyErrorType

object MyMonads {


  //type MyMonad[O] = EitherT[Future, MyErrorType, O]
  type MyMonad[O] = EitherT[Future, MyErrorType, (O, Format[O])]

  case class MyEndpoint[O]()(implicit  ec: ExecutionContext, format: Format[O]) {
    def out(value: O): MyMonad[O] = EitherT.right[MyErrorType] {
      Future((value, format))
    }
  }

}
