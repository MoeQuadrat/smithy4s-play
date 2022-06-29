package smithy4s

import play.api.mvc.{Headers, Request, Result}
import smithy4s.http.{CaseInsensitive, HttpMethod}

package object play4s {

/*  implicit final class ServiceOps[Alg[_[_, _, _, _, _]], Op[_, _, _, _, _]](
      private[this] val serviceProvider: smithy4s.Service.Provider[Alg, Op]
  ) {

    def simpleRestJson: SimpleRestJsonBuilder.ServiceBuilder[Alg, Op] =
      SimpleRestJsonBuilder(serviceProvider.service)

  }*/

  def toPlayMethod(method: HttpMethod): String =
    method match {
      case smithy4s.http.HttpMethod.PUT    => method.showUppercase
      case smithy4s.http.HttpMethod.POST   => method.showUppercase
      case smithy4s.http.HttpMethod.DELETE => method.showUppercase
      case smithy4s.http.HttpMethod.GET    => method.showUppercase
      case smithy4s.http.HttpMethod.PATCH  => method.showUppercase
    }

  def toHeaders(mp: Map[CaseInsensitive, Seq[String]]): Headers =
    Headers.apply(mp.flatMap { case (k, v) =>
      v.map((k.toString, _))
    }.toSeq: _*)

  def getFirstHeader[F[_]](
      headers: Option[Headers],
      s: String
  ): Option[(String, String)] =
    if (headers.isDefined) Some(headers.get.headers.head) else None

  /*def toMap(hd: Headers) = hd.headers.map { h =>
    h.name.toString -> h.value
  }.toMap*/

  def getHeaders[F[_]](req: Request[F[_]]) =
    req.headers.headers.groupBy(_._1).map { case (k, v) =>
      (CaseInsensitive(k), v.map(_._2))
    }
}
