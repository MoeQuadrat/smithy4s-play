package play4s

import cats.data.{EitherT, Kleisli}
import play.api.http.Writeable
import play.api.libs.json.{Format, JsValue, Writes}
import play.api.mvc.ControllerComponents

import scala.concurrent.{ExecutionContext, Future}



trait MyErrorType

object MyMonads {

  type MyMonad[O] = EitherT[Future, MyErrorType, (O, Format[O])]

  case class MyEndpoint()(implicit  ec: ExecutionContext) {
    def out[O](f: Future[(O, Format[O])])(implicit format: Format[O]): MyMonad[O] = EitherT.right[MyErrorType] {
      f
    }

    def outF[O](value: O)(implicit format: Format[O]): MyMonad[O] = EitherT.right[MyErrorType] {
      Future((value, format))
    }

    def outF[O](value: () => O)(implicit format: Format[O]): MyMonad[O] = EitherT.right[MyErrorType] {
      Future((value(), format))
    }

  }

}
