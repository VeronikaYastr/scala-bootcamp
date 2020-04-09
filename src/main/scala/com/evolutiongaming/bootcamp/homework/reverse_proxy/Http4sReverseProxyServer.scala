package com.evolutiongaming.bootcamp.homework.reverse_proxy

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.evolutiongaming.bootcamp.homework.crud.config.{Config, ServerConfig}
import com.evolutiongaming.bootcamp.homework.crud.dao.{AuthorsDaoImpl, BooksDaoImpl, DaoInit}
import com.evolutiongaming.bootcamp.homework.crud.routes.{AuthorsRoutes, BooksRoutes}
import doobie.util.transactor.Transactor
import fs2.Stream
import org.http4s._
import org.http4s.implicits._
import org.http4s.server.blaze.BlazeServerBuilder

object Http4sReverseProxyServer extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO]
      .bindHttp(9000, "localhost")
      .withHttpApp(ProxyRoutes.routes().orNotFound)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
}
