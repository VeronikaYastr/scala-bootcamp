package com.evolutiongaming.bootcamp.homework.reverse_proxy

import cats.effect.{ContextShift, IO}
import org.http4s.Uri.{Authority, RegName}
import org.http4s._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.dsl.io._

import scala.concurrent.ExecutionContext.global

object ProxyRoutes {

  var cache: Map[Uri, Response[IO]] = Map[Uri, Response[IO]]()

  def routes()(implicit cs: ContextShift[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case req =>
      val result = cache.get(req.uri)
      result match {
        case None => for {
          clientResponse <- BlazeClientBuilder[IO](global).resource.use { client =>
            val newAuthority = Authority(userInfo = None, host = RegName("localhost"), port = Some(8000))
            val proxiedReq = req.withUri(req.uri.copy(authority = Some(newAuthority))).withHeaders(req.headers.put(Header("host", "localhost")))
            val res = for {
              response <- client.fetch(proxiedReq)(IO.pure)
            } yield {
              if (req.method == GET) {
                cache += (req.uri -> response)
              }
              response
            }
            res
          }
        } yield clientResponse
        case Some(value) => IO(value)
      }
  }

}
