import play.api.mvc.{Headers, Request, RequestHeader}
import smithy4s.http.{CaseInsensitive, HttpMethod}

package object play4s {

  def toPlayMethod(method: HttpMethod): String =
    method match {
      case smithy4s.http.HttpMethod.PUT    => method.showUppercase
      case smithy4s.http.HttpMethod.POST   => method.showUppercase
      case smithy4s.http.HttpMethod.DELETE => method.showUppercase
      case smithy4s.http.HttpMethod.GET    => method.showUppercase
      case smithy4s.http.HttpMethod.PATCH  => method.showUppercase
    }

  def getHeaders[F](req: RequestHeader) =
    req.headers.headers.groupBy(_._1).map { case (k, v) =>
      (CaseInsensitive(k), v.map(_._2))
    }

}
